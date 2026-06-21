package com.xxl.job.core.openapi.admin.dto;

import java.io.Serializable;

/**
 * request DTO for job operation (remove / start / stop) by id
 */
public class JobOperateRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    private int id;

    public JobOperateRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
