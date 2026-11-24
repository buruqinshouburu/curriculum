package com.doinner.csys.service;

import com.doinner.csys.domain.vo.CourseVo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Component
public class CourseImportAuthorVerifier {
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper json = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    private final String tokenUrl, userUrl, clientId, secret;

    public CourseImportAuthorVerifier(@Value("${course-import.portal.token-url:}") String tokenUrl,
                                      @Value("${course-import.portal.user-url:}") String userUrl,
                                      @Value("${course-import.portal.client-id:}") String clientId,
                                      @Value("${course-import.portal.secret:}") String secret) {
        this.tokenUrl = tokenUrl;
        this.userUrl = userUrl;
        this.clientId = clientId;
        this.secret = secret;
    }

    public void verify(List<CourseVo> rows) {
        String token = null;
        Map<String, JsonNode> results = new HashMap<>();
        for (CourseVo row : rows) {
            if (row == null || Objects.toString(row.getAuthors(), "").isBlank()) continue;
            Map<String, String> owners = new LinkedHashMap<>();
            boolean rowUnavailable = false;
            for (String item : Objects.toString(row.getAuthors(), "").split("[,，、]")) {
                String keyword = item.trim();
                if (keyword.isEmpty()) continue;
                try {
                    if (token == null) {
                        if (tokenUrl.isBlank() || userUrl.isBlank() || clientId.isBlank() || secret.isBlank())
                            throw invalid("鉴权");
                        token = fetch(HttpRequest.newBuilder(URI.create(tokenUrl))
                                .header("clientId", clientId).header("secretKey", secret), "鉴权").textValue();
                        if (token == null || token.isBlank()) throw invalid("鉴权");
                    }
                    JsonNode user = results.get(keyword);
                    if (user == null) {
                        URI uri = URI.create(userUrl + "?keyWord=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8));
                        user = fetch(HttpRequest.newBuilder(uri).header("access-token", token), "人员查询");
                        results.put(keyword, user);
                    }
                    if (user.isNull()) continue;
                    String account = user.path("email").textValue();
                    String name = user.path("userName").textValue();
                    if (account == null || name == null || account.isBlank() || name.isBlank()
                            || account.contains(",") || name.contains(",")
                            || !(keyword.equals(account.trim()) || keyword.equals(name.trim()))) throw invalid("人员查询");
                    owners.putIfAbsent(account.trim(), name.trim());
                } catch (RuntimeException ex) {
                    rowUnavailable = true;
                    break;
                }
            }
            if (rowUnavailable) continue;
            row.setAuthorIds(owners.isEmpty() ? null : String.join(",", owners.keySet()));
            row.setAuthors(owners.isEmpty() ? null : String.join(",", owners.values()));
        }
    }

    private JsonNode fetch(HttpRequest.Builder request, String operation) {
        try {
            HttpResponse<String> response = http.send(request.timeout(Duration.ofSeconds(10)).GET().build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) throw invalid(operation);
            JsonNode body = json.readTree(response.body());
            if (body == null || !"00000".equals(body.path("code").textValue()) || !body.has("data"))
                throw invalid(operation);
            return body.get("data");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw invalid(operation);
        } catch (IOException e) {
            throw invalid(operation);
        }
    }

    private IllegalStateException invalid(String operation) {
        return new IllegalStateException("办事大厅" + operation + "失败，课程未导入");
    }
}
