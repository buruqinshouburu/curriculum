package com.doinner.csys.utils;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.doinner.common.security.utils.DictUtils;
import com.doinner.system.domain.entity.SysDictData;

import java.util.List;

public class CurDictUtils {
    public static List<SysDictData> getDictData(String dictType){
        return getCache(dictType);
    }

    public static SysDictData getDictData(String dictType, String dictValue){
        List<SysDictData> cache = getCache(dictType);
        if(ObjectUtils.isEmpty(cache)){
            return null;
        }
        for (SysDictData sysDictData : cache) {
            if (sysDictData.getDictValue().equals(dictValue)) {
                return sysDictData;
            }
        }
        return null;
    }

    private static List<SysDictData> getCache(String dictType){
        return DictUtils.getDictCache(dictType);
    }
}
