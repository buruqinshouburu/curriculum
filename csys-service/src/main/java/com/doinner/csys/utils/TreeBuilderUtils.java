package com.doinner.csys.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wzg
 * @date 2025/2/28 9:59
 */
public class TreeBuilderUtils {

    /**
     * 构建根节点树结构
     *
     * @param vos 包含待构建树结构的元素列表
     * @param <T> 继承自TreeEntity接口的具体实体类型
     * @return 根节点集合
     */
    public static <T> List<T> buildRootTree(List<T> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }
        try {
            // 获取getId、getParentId和getChildren方法引用
            Method getIdMethod = vos.get(0).getClass().getMethod("getId");
            Method getParentIdMethod = vos.get(0).getClass().getMethod("getParentId");
            Method getChildrenMethod = vos.get(0).getClass().getMethod("getChildren");
            Method setChildrenMethod = vos.get(0).getClass().getMethod("setChildren", List.class);
            // 顶层根节点列表
            List<T> rootTaskInfoList = new ArrayList<>();
            Map<String, T> taskInfoMap = new HashMap<>();
            for (T vo : vos) {
                String id = getIdMethod.invoke(vo).toString();
                taskInfoMap.put(id, vo);
                // if (getParentIdMethod.invoke(vo) == null || DomainFieldConstants.ROOT_NODE_ID.equals(getParentIdMethod.invoke(vo).toString())){
                if (getParentIdMethod.invoke(vo) == null ||  -1 == Integer.parseInt(getParentIdMethod.invoke(vo).toString())){
                    rootTaskInfoList.add(vo);
                }
            }
            // 构建子树
            for (T vo : vos) {
                if (getParentIdMethod.invoke(vo) != null) {
                    String parentId = getParentIdMethod.invoke(vo).toString();
                    T parentTaskInfo = taskInfoMap.get(parentId);
                    if (parentTaskInfo != null) {
                        List<T> children = (List<T>) getChildrenMethod.invoke(parentTaskInfo);
                        if (CollectionUtils.isEmpty(children)) {
                            children = new ArrayList<>();
                        }
                        children.add(vo);
                        setChildrenMethod.invoke(parentTaskInfo, children);
                    }
                }
            }
            return rootTaskInfoList;
        } catch (Exception e) {
            throw new RuntimeException("构建树结构异常", e);
        }
    }

    /**
     *  将树打平
     * @param treeList
     * @return
     * @param <T>
     * @throws Exception
     */
    public static <T> List<T> flattenTree(List<T> treeList) {
        List<T> result = new ArrayList<>();
        for (T node : treeList) {
            // 添加当前节点
            result.add(node);
            Method getChildrenMethod = null;
            try {
                // 尝试获取getChildren方法
                getChildrenMethod = node.getClass().getMethod("getChildren");
            } catch (Exception e) {
                throw new RuntimeException("无法找到children属性的getter方法", e);
            }
            // 调用getChildren方法获取子节点
            Object children = null;
            try {
                children = getChildrenMethod.invoke(node);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (children != null && children instanceof List) {
                List<T> childrenList = (List<T>) children;
                if (!childrenList.isEmpty()) {
                    result.addAll(flattenTree(childrenList));
                }
            }
        }
        return result;
    }
}