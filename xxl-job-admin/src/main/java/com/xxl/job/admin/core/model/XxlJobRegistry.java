package com.xxl.job.admin.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(name = "xxl_job_registry", indexes = {
        @Index(name = "i_g_k_v", columnList = "registry_group,registry_key,registry_value")})
public class XxlJobRegistry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator") // 使用基于雪花算法的主键生成策略
    @GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
    private Long id;
    @Column(name = "registry_group", nullable = false, length = 50)
    private String registryGroup;
    @Column(name = "registry_key", nullable = false, length = 255)
    private String registryKey;
    @Column(name = "registry_value", nullable = false, length = 255)
    private String registryValue;
    @Column(name = "update_time")
    private Date updateTime;

    public XxlJobRegistry() {
    }

    public XxlJobRegistry(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        this.registryGroup = registryGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
