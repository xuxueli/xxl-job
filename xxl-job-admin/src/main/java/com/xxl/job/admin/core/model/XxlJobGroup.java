package com.xxl.job.admin.core.model;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public class XxlJobGroup {

    private int id;
    private String appName;
    private String title;
    private int order;

    // registry list
    private List<String> registryList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<String> getRegistryList() {
        return registryList;
    }

    public void setRegistryList(List<String> registryList) {
        this.registryList = registryList;
    }
}
