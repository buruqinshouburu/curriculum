package com.example.cscy.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SoMap extends HashMap<String, Object> {

    public SoMap() {

    }

    public static Field[] getFields(Class<?> clazz) {
        ArrayList<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    public SoMap(Object obj) {
        Class<?> aClass = obj.getClass();
        Field[] fields = getFields(aClass);
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if(field.get(obj) == null) {
                    this.put(field.getName(), "");
                }else{
                    String string = field.get(obj) + "@";
                    this.put(field.getName(), string.replace("@","\n  "));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
