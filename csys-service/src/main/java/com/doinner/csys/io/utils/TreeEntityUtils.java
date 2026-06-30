package com.doinner.csys.io.utils;

import com.doinner.common.core.domain.db.AbstractTreeBaseEntity;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeEntityUtils {

    /*
    public static <T extends TreeEntity> List<T> toList(List<T> list){
        if(ObjectUtils.isEmpty(list)){
            return List.of();
        }
        List<T> children = list.parallelStream().filter(_treeEntity -> ObjectUtils.isNotEmpty(_treeEntity.getChildren()))
                .flatMap(_treeEntity -> ((List<T>)(_treeEntity.getChildren())).stream()).collect(Collectors.toList());
        List<T> allList = toList(children);
        allList.addAll(list);
        return allList;
    }


    public static <T extends AbstractTreeIdEntity> List<T> toList(List<T> list){
        if(ObjectUtils.isEmpty(list)){
            return List.of();
        }
        List<T> children = list.parallelStream().filter(_treeEntity -> ObjectUtils.isNotEmpty(_treeEntity.getChildren()))
                .flatMap(_treeEntity -> ((List<T>)(_treeEntity.getChildren())).stream()).collect(Collectors.toList());
        List<T> allList = toList(children);
        allList.addAll(list);
        return allList;
    }*/

    public static <T extends AbstractTreeBaseEntity> List<T> toList(List<T> list){
        if(ObjectUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<T> children = list.parallelStream().filter(_treeEntity -> ObjectUtils.isNotEmpty(_treeEntity.getChildren()))
                .flatMap(_treeEntity -> ((List<T>)(_treeEntity.getChildren())).stream()).collect(Collectors.toList());
        List<T> allList = toList(children);
        allList.addAll(list);
        return allList;
    }



}
