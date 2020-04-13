package com.xxl.job.admin.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(name = "job_registry")
@TableGenerator(name = "job_registry_gen",
        table="primary_key_gen",
        pkColumnName="gen_name",
        valueColumnName="gen_value",
        pkColumnValue="JOB_REGISTRY_PK",
        allocationSize=1
)
public class XxlJobRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="job_registry_gen")
    @Column(length = 11)
    private int id;
    @Column(name = "registry_group",length = 50)
    private String registryGroup;
    @Column(name = "registry_key",length = 255)
    private String registryKey;
    @Column(name = "registry_value",length = 255)
    private String registryValue;
    @Column(name = "update_time")
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
