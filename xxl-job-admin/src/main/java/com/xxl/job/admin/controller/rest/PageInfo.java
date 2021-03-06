package com.xxl.job.admin.controller.rest;

import java.util.List;

/**
 * @Author: 63198
 * @Date: 2021/2/25 上午8:49
 * @Version 1.0
 */
public class PageInfo<T> {
    private int total;
    private int current;
    private int pageSize;
    private List<T> data;


    PageInfo(List<T> data, int total) {
        this.data = data;
        this.total = total;
    }

    PageInfo(List<T> data, int total, int current, int pageSize) {
        this.data = data;
        this.total = total;
        this.current = current;
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
            "total=" + total +
            ", current=" + current +
            ", pageSize=" + pageSize +
            ", data=" + data +
            '}';
    }
}
