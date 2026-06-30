package com.doinner.csys.domain;
import com.doinner.common.core.domain.DataTable;
import com.github.pagehelper.PageInfo;


import java.util.List;

public class PageDataTable<T> extends DataTable<List<? extends T>> {

    /*public PageDataTable(List<? extends T> data) {

        super(data, new PageInfo<>(data).getTotal());
    }

    public static <T> PageDataTable<T> success(List<? extends T> data) {
        return new PageDataTable<T>(data);
    }*/

}
