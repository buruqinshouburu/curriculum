package com.doinner.csys.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SoMap extends HashMap<String, Object> {

    public SoMap(){

    }

    public static Field[] getFields(Class clazz){
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            Field[] fields = clazz.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    /**
     * 构造方法，将任意实体类转化为 Map
     * @param obj
     */
    public SoMap(Object obj){
        Class<?> clazz = obj.getClass();
        Field[] fields = getFields(clazz);
        try {
            for (Field field:fields){
                field.setAccessible(true);
                if (field.get(obj) == null){
                    this.put(field.getName(),"");
                }else {
                    String string = field.get(obj)+"@";
                    this.put(field.getName(),string.replace("@","\n    "));
//                    this.put(field.getName(),field.get(obj)+"\n    ");
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 Map 转化为任意实体类
     * @param clazz 发射获取字节码对象
     * @return
     * @param <T>
     */
    public <T> T toEntity(Class<T> clazz){
        Field[] fields = clazz.getDeclaredFields();
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            T t = (T)constructor.newInstance();
            for (Field field:fields){
                field.setAccessible(true);
                field.set(t,this.get(field));
            }
            return t;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 从集合中获取一个字段的方法，如果字段不存在返回空
     * @param key 字段的唯一标识
     * @return 对应字段的值
     * @param <T> 字段的类型，运行时自动识别，使用时无序声明和强转
     */
    public <T> T get(String key){
        return (T) super.get(key);
    }
}
