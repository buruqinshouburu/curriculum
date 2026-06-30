package com.doinner.csys.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Map 转换为 Java 对象的工具类
 */
public class MapToObjectUtil {

    /**
     * 将 Map 转换为指定类型的 Java 对象
     *
     * @param map 数据映射
     * @param clazz 目标类类型
     * @return 转换后的对象，转换失败返回 null
     */
    public static <T> T convertToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty() || clazz == null) {
            return null;
        }

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            // 递归获取所有字段（包括父类）
            List<Field> allFields = new ArrayList<>();
            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                allFields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
                currentClass = currentClass.getSuperclass();
            }

            for (Field field : allFields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = map.get(fieldName);

                if (ObjectUtils.isNotEmpty(value)) {
                    // 尝试类型转换
                    Object convertedValue = convertValue(value, field.getType());
                    field.set(instance, convertedValue);
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("转换 Map 到对象失败: " + clazz.getName(), e);
        }
    }


    /**
     * 转换值的类型
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 如果已经是目标类型，直接返回
        if (targetType.isInstance(value)) {
            return value;
        }

        // 处理String类型 - 在处理日期时间转换之前先检查String
        if (targetType == String.class) {
            return value.toString();
        }

        // 处理日期时间类型转换 - 只有当value是String且targetType是日期时间类型时才转换
        if (value instanceof String) {
            String strValue = (String) value;

            // 转换为 LocalDateTime
            if (targetType == LocalDateTime.class) {
                return parseDateTime(strValue);
            }

            // 转换为 LocalDate
            if (targetType == LocalDate.class) {
                return parseDate(strValue);
            }

            // 转换为 LocalTime
            if (targetType == LocalTime.class) {
                return parseTime(strValue);
            }
        }

        // 处理数值类型转换
        if (value instanceof Number) {
            Number num = (Number) value;
            if (targetType == int.class || targetType == Integer.class) {
                return num.intValue();
            } else if (targetType == long.class || targetType == Long.class) {
                return num.longValue();
            } else if (targetType == float.class || targetType == Float.class) {
                return num.floatValue();
            } else if (targetType == double.class || targetType == Double.class) {
                return num.doubleValue();
            } else if (targetType == short.class || targetType == Short.class) {
                return num.shortValue();
            } else if (targetType == byte.class || targetType == Byte.class) {
                return num.byteValue();
            }
        }

        // 处理Boolean类型
        if (targetType == boolean.class || targetType == Boolean.class) {
            return value instanceof Boolean ? value : Boolean.parseBoolean(value.toString());
        }
        //处理list类型
        if(List.class.isAssignableFrom(targetType)){
            return convertToList(value,targetType);
        }

        // 其他情况返回原值
        return value;
    }

    /**
     * 将值转换为list类型
     */
    private static Object convertToList(Object value, Class<?> targetType) {
        if (value instanceof List) {
            List<?> itemList = (List<?>) value;

            // 判断目标List的泛型类型
            Class<?> genericType = getListItemType(targetType);

            // 如果是List<Integer>类型
            if (genericType == Integer.class || genericType == int.class) {
                List<Integer> result = new ArrayList<>();
                for (Object item : itemList) {
                    if (item instanceof Number) {
                        result.add(((Number) item).intValue());
                    } else if (item instanceof String) {
                        result.add(Integer.parseInt((String) item));
                    }
                }
                return result;
            }

            // 如果是List<String>类型
            if (genericType == String.class) {
                List<String> result = new ArrayList<>();
                for (Object item : itemList) {
                    result.add(item.toString());
                }
                return result;
            }

            // 其他类型，返回原列表
            return value;
        }
        return value;
    }

    /**
     * 获取List的泛型类型（通过反射）
     */
    private static Class<?> getListItemType(Class<?> listType) {
        // 默认返回Object，实际应用中可能需要更复杂的处理
        if (listType == List.class) {
            return Object.class;
        }
        return Object.class;
    }

    /**
     * 解析字符串为 LocalDateTime
     */
    private static LocalDateTime parseDateTime(String str) {
        try {
            // 尝试常见的日期时间格式
            String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm",
                "yyyyMMddHHmmss",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
            };

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDateTime.parse(str, formatter);
                } catch (Exception ignored) {
                    // 继续尝试下一个格式
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回 null
        }
        return null;
    }

    /**
     * 解析字符串为 LocalDate
     */
    private static LocalDate parseDate(String str) {
        try {
            String[] patterns = {
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "yyyyMMdd",
                "yyyy-MM",
                "yyyy/MM"
            };

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDate.parse(str, formatter);
                } catch (Exception ignored) {
                    // 继续尝试下一个格式
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回 null
        }
        return null;
    }

    /**
     * 解析字符串为 LocalTime
     */
    private static LocalTime parseTime(String str) {
        try {
            String[] patterns = {
                "HH:mm:ss",
                "HH:mm",
                "HHmmss",
                "HH:mm:ss.SSS"
            };

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalTime.parse(str, formatter);
                } catch (Exception ignored) {
                    // 继续尝试下一个格式
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回 null
        }
        return null;
    }
}