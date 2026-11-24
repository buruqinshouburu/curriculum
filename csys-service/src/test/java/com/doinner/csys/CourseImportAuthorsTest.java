package com.doinner.csys;

import com.doinner.common.core.domain.DataSet;
import com.doinner.csys.dao.CourseMapper;
import com.doinner.csys.dao.CourseScheduleMapper;
import com.doinner.csys.domain.vo.CourseVo;
import com.doinner.csys.service.CourseImportAuthorVerifier;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import com.doinner.system.service.DoinnerDeptService;
import com.doinner.system.service.DoinnerDictDataService;
import com.sun.net.httpserver.HttpServer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseImportAuthorsTest {
    private final CurriculumServiceImpl service = new CurriculumServiceImpl();
    private final CourseMapper courses = mock(CourseMapper.class);
    private final CourseScheduleMapper schedules = mock(CourseScheduleMapper.class);
    private HttpServer portal;
    private int tokens;
    private final List<String> keywords = new ArrayList<>();
    private String badResponse = "{\"code\":\"00000\",\"data\":null}";

    @BeforeEach void setup() throws Exception {
        DoinnerDeptService departments = mock(DoinnerDeptService.class);
        DoinnerDictDataService dict = mock(DoinnerDictDataService.class);
        when(departments.list(any())).thenReturn(DataSet.success(Collections.emptyList()));
        when(dict.dictType(anyString())).thenReturn(DataSet.success(Collections.emptyList()));
        ReflectionTestUtils.setField(service, "courseMapper", courses);
        ReflectionTestUtils.setField(service, "courseScheduleMapper", schedules);
        ReflectionTestUtils.setField(service, "doinnerDeptService", departments);
        ReflectionTestUtils.setField(service, "doinnerDictDataService", dict);
        portal = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        portal.createContext("/", exchange -> {
            String body = "";
            int status = 403;
            if ("/token".equals(exchange.getRequestURI().getPath())
                    && "client".equals(exchange.getRequestHeaders().getFirst("clientId"))
                    && "secret".equals(exchange.getRequestHeaders().getFirst("secretKey"))) {
                tokens++;
                body = "{\"code\":\"00000\",\"data\":\"test-token\"}";
                status = 200;
            } else if ("/user".equals(exchange.getRequestURI().getPath())
                    && "test-token".equals(exchange.getRequestHeaders().getFirst("access-token"))) {
                String query = URLDecoder.decode(exchange.getRequestURI().getRawQuery(), "UTF-8");
                keywords.add(query);
                body = query.equals("keyWord=A+B") || query.equals("keyWord=甲")
                        ? "{\"code\":\"00000\",\"data\":{\"email\":\"A+B\",\"userName\":\"甲\"}}"
                        : query.equals("keyWord=B")
                        ? "{\"code\":\"00000\",\"data\":{\"email\":\"B\",\"userName\":\"乙\"}}" : badResponse;
                status = body.equals("unavailable") ? 503 : 200;
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        portal.start();
        String base = "http://127.0.0.1:" + portal.getAddress().getPort();
        ReflectionTestUtils.setField(service, "courseImportAuthorVerifier",
                new CourseImportAuthorVerifier(base + "/token", base + "/user", "client", "secret"));
    }

    @AfterEach void stop() { portal.stop(0); }

    @Test void keepsOnlyVerifiedPairsInOrder() throws Exception {
        runImport(" A+B，missing、B,甲 ", "missing", "");
        ArgumentCaptor<CourseVo> saved = ArgumentCaptor.forClass(CourseVo.class);
        verify(courses, times(3)).insertCourse(saved.capture());
        assertEquals("甲,乙", saved.getAllValues().get(0).getAuthors());
        assertEquals("A+B,B", saved.getAllValues().get(0).getAuthorIds());
        assertNull(saved.getAllValues().get(1).getAuthors());
        assertEquals("", saved.getAllValues().get(2).getAuthors());
        for (CourseVo row : saved.getAllValues().subList(1, 3)) {
            assertNull(row.getAuthorIds());
        }
        assertEquals(1, tokens);
        assertEquals(Arrays.asList("keyWord=A+B","keyWord=missing","keyWord=B","keyWord=甲"), keywords);
    }

    @Test void remoteFailuresKeepOriginalAuthorsAndDoNotBlockBatch() throws Exception {
        for (String response : Arrays.asList("unavailable", "not-json", "{\"code\":\"401\",\"data\":null}",
                "{\"code\":\"00000\"}", "{\"code\":\"00000\",\"data\":{}}",
                "{\"code\":\"00000\",\"data\":{\"email\":\"unexpected\",\"userName\":\"other\"}}")) {
            badResponse = response;
            runImport("missing", "B");
            ArgumentCaptor<CourseVo> saved = ArgumentCaptor.forClass(CourseVo.class);
            verify(courses, times(2)).insertCourse(saved.capture());
            assertEquals("missing", saved.getAllValues().get(0).getAuthors(), response);
            assertEquals("乙", saved.getAllValues().get(1).getAuthors(), response);
            reset(courses, schedules);
        }
    }

    @Test void blankAuthorsNeedNoToken() throws Exception {
        runImport(" ");
        verify(courses).insertCourse(any());
        assertEquals(0, tokens);
    }

    @Test void rejectedCredentialsKeepOriginalAuthorsAndStillImport() throws Exception {
        String base = "http://127.0.0.1:" + portal.getAddress().getPort();
        ReflectionTestUtils.setField(service, "courseImportAuthorVerifier",
                new CourseImportAuthorVerifier(base + "/token", base + "/user", "client", "wrong"));
        runImport("B");
        assertTrue(keywords.isEmpty());
        ArgumentCaptor<CourseVo> saved = ArgumentCaptor.forClass(CourseVo.class);
        verify(courses).insertCourse(saved.capture());
        assertEquals("B", saved.getValue().getAuthors());
    }

    @Test void everyTemplateTypeVerifiesFilledAuthors() throws Exception {
        for (Integer type : Arrays.asList(1, 2, null, 99)) {
            runImportWithType(type, "B");
        }
        verify(courses, times(4)).insertCourse(any());
        assertEquals(4, tokens);
        assertEquals(4, keywords.size());
    }

    private void runImport(String... authors) throws Exception {
        runImportWithType(1, authors);
    }

    @Test void missingRemoteAuthorIsRemovedAfterSuccessfulLookup() throws Exception {
        String original = " A+B，missing、B ";
        runImportWithType(2, original);
        ArgumentCaptor<CourseVo> saved = ArgumentCaptor.forClass(CourseVo.class);
        verify(courses).insertCourse(saved.capture());
        assertEquals("甲,乙", saved.getValue().getAuthors());
        assertEquals("A+B,B", saved.getValue().getAuthorIds());
    }

    private void runImportWithType(Integer type, String... authors) throws Exception {
        try (Workbook book = new XSSFWorkbook(); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            Sheet sheet = book.createSheet();
            String[] headers = {"*课程名称", "*开课单位", "*适用对象", "课程负责人"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            for (int i = 0; i < authors.length; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue("course" + i);
                row.createCell(1).setCellValue("理学院");
                row.createCell(2).setCellValue("生长军官学员");
                row.createCell(3).setCellValue(authors[i]);
            }
            book.write(bytes);
            service.importCourse(new MockMultipartFile("file", bytes.toByteArray()),
                    "1", "1", null, "1", type, "2026", null, null);
        }
    }
}
