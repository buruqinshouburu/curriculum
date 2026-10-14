package com.doinner.csys.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 字典多值字段（如教法、学法）的规范化工具。
 * <p>
 * 落库口径：多个值统一用中文顿号「、」拼接，顺序按前端传入顺序，重复项只保留一次。
 * 兼容历史单选数据（单值拆分后仍是自身）与历史混合分隔符（英文逗号/中文逗号/分号/斜杠/竖线）。
 */
public final class MultiValueUtils {

    /** 多值字段的统一分隔符 */
    public static final String JOINER = "、";

    /** 解析时接受的分隔符：顿号、英文逗号、中文逗号、分号、斜杠、竖线 */
    private static final String SPLIT_REGEX = "[、,，;；/|]";

    private MultiValueUtils() {
    }

    /**
     * 拆分为去重后的值列表（保留原顺序，过滤空值）。
     * 空白输入返回空列表。
     */
    public static List<String> split(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null) {
            return result;
        }
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (String part : raw.split(SPLIT_REGEX, -1)) {
            String value = part.replace("　", "").trim();
            if (!value.isEmpty()) {
                values.add(value);
            }
        }
        result.addAll(values);
        return result;
    }

    /**
     * 规范化为顿号拼接的去重字符串；空白输入返回 null（避免写入空串）。
     */
    public static String normalize(String raw) {
        List<String> values = split(raw);
        return values.isEmpty() ? null : String.join(JOINER, values);
    }
}
