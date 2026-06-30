package com.doinner.csys.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 *  逻辑分页
 * @author wzg
 * @date 2025/6/30 16:59
 */
public class PaginationUtils {
    public static <T> Page<T> getPage(List<T> list, int pageNum, int pageSize) {
        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return new PageImpl<>(list.subList(total > fromIndex ? fromIndex : 0, toIndex), Pageable.unpaged(), total);
    }
}
