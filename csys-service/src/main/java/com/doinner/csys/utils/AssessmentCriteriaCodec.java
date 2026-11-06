package com.doinner.csys.utils;

import com.doinner.csys.domain.TeachingPlanAssessment;
import com.doinner.system.domain.entity.SysDictData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Assessment standard stays a String; only grade criteria use the JSON array contract. */
public final class AssessmentCriteriaCodec {
    private static final ObjectMapper JSON = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    private static final List<String> GRADES = Arrays.asList("优秀", "良好", "中等", "及格", "不及格", "合格", "不合格");
    private static final Pattern HEADING = Pattern.compile("^(优秀|良好|中等|及格|不及格|合格|不合格)[：:](.*)$");

    private AssessmentCriteriaCodec() { }

    public static void validate(TeachingPlanAssessment assessment, List<SysDictData> mechanisms) {
        if (Integer.valueOf(5).equals(assessment.getAssessmentCategory())) return;
        Map<String, String> labels = new LinkedHashMap<>();
        if (mechanisms != null) {
            for (SysDictData dict : mechanisms) {
                if (dict != null) labels.put(StringUtils.trim(dict.getDictValue()), StringUtils.trim(dict.getDictLabel()));
            }
        }
        String raw = effectiveMechanism(assessment);
        String mechanism = mechanismLabel(raw, labels);
        List<String> required;
        switch (StringUtils.defaultString(mechanism)) {
            case "百分制": return;
            case "五级制": required = GRADES.subList(0, 5); break;
            case "四级制": required = Arrays.asList("优秀", "良好", "及格", "不及格"); break;
            case "两级制": required = GRADES.subList(5, 7); break;
            default: return;
        }
        if (!isJson(assessment.getStandard())) {
            throw new IllegalArgumentException("请填写" + mechanism + "每个等级的评价标准");
        }
        Set<String> completed = new HashSet<>();
        for (JsonNode entry : criteria(assessment.getStandard())) {
            if (StringUtils.isNotBlank(entry.path("description").asText())) completed.add(entry.path("label").asText());
        }
        for (String label : required) {
            if (!completed.contains(label)) throw new IllegalArgumentException("请填写“" + label + "”的评价标准");
        }
    }

    public static String effectiveMechanism(TeachingPlanAssessment assessment) {
        return Integer.valueOf(3).equals(assessment.getAssessmentCategory())
                ? StringUtils.defaultIfBlank(assessment.getScoreSystem(), assessment.getMechanism())
                : StringUtils.defaultIfBlank(assessment.getMechanism(), assessment.getScoreSystem());
    }

    public static boolean isGraded(String mechanism) {
        return "五级制".equals(mechanism) || "四级制".equals(mechanism) || "两级制".equals(mechanism);
    }

    public static String mechanismLabel(String raw, Map<String, String> dictionary) {
        String value = StringUtils.trim(raw);
        String label = dictionary.getOrDefault(value, value);
        if ("1".equals(label) || "百分制".equals(label)) return "百分制";
        if ("2".equals(label) || "五级制".equals(label)) return "五级制";
        if ("3".equals(label) || "两级制".equals(label) || "二级制".equals(label)) return "两级制";
        if ("4".equals(label) || "四级制".equals(label)) return "四级制";
        return label;
    }

    public static String mechanismFromWord(String raw, Map<String, String> dictionary) {
        if (StringUtils.isBlank(raw)) return raw;
        if (dictionary != null) {
            String actual = dictionary.get("sys_assessment_mechanism#" + raw.trim());
            if (StringUtils.isNotBlank(actual)) return actual;
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                if (entry.getKey().startsWith("sys_assessment_mechanism#") && raw.equals(entry.getValue())) return raw;
            }
        }
        String label = wordMechanismLabel(raw, dictionary);
        String actual = dictionary == null ? null : dictionary.get("sys_assessment_mechanism#" + label);
        if (StringUtils.isNotBlank(actual)) return actual;
        if ("百分制".equals(label)) return "1";
        if ("五级制".equals(label)) return "2";
        if ("两级制".equals(label)) return "3";
        if ("四级制".equals(label)) return "4";
        return raw;
    }

    private static String wordMechanismLabel(String raw, Map<String, String> dictionary) {
        if (dictionary != null) {
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                if (entry.getKey().startsWith("sys_assessment_mechanism#") && StringUtils.equals(entry.getValue(), raw)) {
                    return mechanismLabel(entry.getKey().substring("sys_assessment_mechanism#".length()), Collections.emptyMap());
                }
            }
        }
        return mechanismLabel(raw, Collections.emptyMap());
    }

    public static boolean isJson(String standard) {
        return StringUtils.startsWith(StringUtils.trim(standard), "[")
                || StringUtils.startsWith(StringUtils.trim(standard), "{");
    }

    private static ArrayNode criteria(String standard) {
        try {
            JsonNode root = JSON.readTree(standard);
            if (root == null || !root.isArray()) throw new IllegalArgumentException();
            Set<String> labels = new HashSet<>();
            for (JsonNode entry : root) {
                if (!entry.isObject() || !entry.path("value").isTextual()
                        || StringUtils.isBlank(entry.path("value").asText())
                        || !entry.path("label").isTextual() || !GRADES.contains(entry.path("label").asText())
                        || !entry.path("description").isTextual()
                        || !labels.add(entry.path("label").asText())) {
                    throw new IllegalArgumentException();
                }
            }
            return (ArrayNode) root;
        } catch (Exception ex) {
            throw new IllegalArgumentException("评价标准必须为 label 不重复的等级 value、label、description 字符串数组", ex);
        }
    }

    public static String toWord(String standard) {
        if (!isJson(standard)) {
            Map<String, String> labels = new LinkedHashMap<>();
            for (int i = 0; i < GRADES.size(); i++) labels.put(String.valueOf(i + 1), GRADES.get(i));
            return translateLegacy(standard, labels);
        }
        ArrayNode entries;
        try {
            entries = criteria(standard);
        } catch (IllegalArgumentException ex) {
            return standard;
        }
        StringBuilder text = new StringBuilder();
        for (JsonNode entry : entries) {
            if (text.length() > 0) text.append('\n');
            text.append(entry.path("label").asText()).append("：\n");
            // Indent every description line so a grade-like sentence cannot become a heading on import.
            text.append("  ").append(entry.path("description").asText().replace("\n", "\n  "));
        }
        return text.toString();
    }

    public static String fromWord(String text, Map<String, String> dictionary) {
        return fromWord(text, "五级制", dictionary, false);
    }

    public static String fromWord(String text, String mechanism, Map<String, String> dictionary) {
        return fromWord(text, mechanism, dictionary, true);
    }

    private static String fromWord(String text, String mechanism, Map<String, String> dictionary, boolean requireIndent) {
        if (!isGraded(wordMechanismLabel(mechanism, dictionary))) return text;
        if (StringUtils.isBlank(text) || isJson(text)) return text;
        String[] lines = text.split("\n", -1);
        Matcher firstHeading = HEADING.matcher(lines[0]);
        boolean hasHeading = firstHeading.matches();
        if (hasHeading && requireIndent && (!firstHeading.group(2).isEmpty()
                || lines.length < 2 || !lines[1].startsWith("  "))) return text;
        if (!hasHeading) {
            Map<String, String> values = new LinkedHashMap<>();
            for (String label : GRADES) values.put(label, gradeValue(label, dictionary));
            return translateLegacy(text, values);
        }
        ArrayNode result = JSON.createArrayNode();
        ObjectNode current = null;
        StringBuilder description = new StringBuilder();
        boolean indented = false;
        boolean firstLine = true;
        for (String line : lines) {
            Matcher heading = HEADING.matcher(line);
            if (heading.matches()) {
                if (current != null) current.put("description", description.toString());
                String label = heading.group(1);
                current = result.addObject();
                current.put("value", gradeValue(label, dictionary));
                current.put("label", label);
                description.setLength(0);
                indented = heading.group(2).isEmpty();
                firstLine = indented;
                if (!indented) description.append(heading.group(2));
            } else {
                if (indented && !line.startsWith("  ")) return text;
                if (!firstLine) description.append('\n');
                description.append(indented ? line.substring(2) : line);
                firstLine = false;
            }
        }
        current.put("description", description.toString());
        return result.toString();
    }

    /** Translate only a complete legacy dictionary list; free text and JSON remain byte-for-byte unchanged. */
    public static String translateLegacy(String raw, Map<String, String> dictionary) {
        if (StringUtils.isBlank(raw) || isJson(raw)) return raw;
        String[] tokens = raw.split("(?<=[、,，;；/|])|(?=[、,，;；/|])", -1);
        StringBuilder result = new StringBuilder();
        for (String token : tokens) {
            if (token.matches("[、,，;；/|]")) {
                result.append(token);
            } else {
                String trimmed = token.trim();
                String translated = dictionary.get(trimmed);
                if (translated == null) return raw;
                int start = token.indexOf(trimmed);
                result.append(token, 0, start).append(translated).append(token.substring(start + trimmed.length()));
            }
        }
        return result.toString();
    }

    private static String gradeValue(String label, Map<String, String> dictionary) {
        String value = dictionary == null ? null : dictionary.get("sys_evaluation_standard#" + label);
        return StringUtils.isNotBlank(value) ? value : String.valueOf(GRADES.indexOf(label) + 1);
    }
}
