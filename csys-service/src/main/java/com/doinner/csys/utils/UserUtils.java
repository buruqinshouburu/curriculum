package com.doinner.csys.utils;

import com.doinner.common.security.utils.SecurityUtils;
import com.doinner.system.domain.view.LoginUser;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * 创建人，
 * 修改人，
 * 创建时间，
 * 更新时间工具类
 */
public class UserUtils {

    public static <T> void reflash(T entity) {
        if (entity == null) {
            return;
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            String userName = SecurityUtils.getUsername();
            if (StringUtils.isBlank(userName)){
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null && StringUtils.isNotBlank(loginUser.getUsername())){
                    userName = loginUser.getUsername();
                }
            }
            Class<?> clazz = entity.getClass();
            // 设置创建时间
            Field createTimeField = getField(clazz, "createTime");
            if (createTimeField != null) {
                createTimeField.setAccessible(true);
                Object currentValue = createTimeField.get(entity);
                if (currentValue == null) {
                    createTimeField.set(entity, now);
                }
            }
            // 设置最后修改时间
            Field lastModifiedTimeField = getField(clazz, "lastModifiedTime");
            if (lastModifiedTimeField != null) {
                lastModifiedTimeField.setAccessible(true);
                lastModifiedTimeField.set(entity, now);
            }
            // 设置创建人
            Field creatorField = getField(clazz, "creator");
            if (creatorField != null && StringUtils.isNotBlank(userName)) {
                creatorField.setAccessible(true);
                Object currentValue = creatorField.get(entity);
                if (currentValue == null || StringUtils.isBlank((String) currentValue)) {
                    creatorField.set(entity, userName);
                }
            }
            // 设置最后修改人
            Field lastModifierField = getField(clazz, "lastModifier");
            if (lastModifierField != null && StringUtils.isNotBlank(userName)) {
                lastModifierField.setAccessible(true);
                lastModifierField.set(entity, userName);
            }
        } catch (Exception e) {
            throw new RuntimeException("设置实体属性失败:"+e.getMessage());
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 查找父类字段
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return getField(superClass, fieldName);
            }
            return null;
        }
    }

    /**
     * 删除和修改 只有创建人和admin才能修改。
     * @param mysqlEntity
     * @param <T>
     */
    public static <T> void checkDataPermission(T mysqlEntity) {
       /* if (mysqlEntity == null) {
            return;
        }
        try {
            String userName = SecurityUtils.getUsername();
            if (StringUtils.isBlank(userName)){
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null && StringUtils.isNotBlank(loginUser.getUsername())){
                    userName = loginUser.getUsername();
                }
            }
            Class<?> clazz = mysqlEntity.getClass();
            Field creatorField = getField(clazz, "creator");
            if (creatorField != null && StringUtils.isNotBlank(userName) && !(userName.equals("admin")||userName.equals("cur"))) {
                creatorField.setAccessible(true);
                Object currentValue = creatorField.get(mysqlEntity);
                if (ObjectUtils.isNotEmpty(currentValue) && !userName.equals(currentValue.toString())) {
                    throw new RuntimeException("您没有修改和删除该数据权利，请联系创建人："+currentValue+"！");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }*/
    }

    public static <T> void clearAndRefreshObj(T entity) {
        if(entity==null){return;}
        String[] clearFields = {"id","createTime","lastModifiedTime","creator","lastModifier"};
        Class<?> clazz = entity.getClass();
        try {
            for (String clearFieldName : clearFields) {
                Field field = getField(clazz, clearFieldName);
                if (ObjectUtils.isNotEmpty(field)) {
                    field.setAccessible(true);
                    field.set(entity, null);
                }
            }
            reflash(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
