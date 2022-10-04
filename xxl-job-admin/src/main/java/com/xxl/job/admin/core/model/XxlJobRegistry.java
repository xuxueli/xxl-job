package com.xxl.job.admin.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(name = "xxl_job_registry", indexes = {
		@Index(name = "i_g_k_v", columnList = "registry_group,registry_key,registry_value") })
public class XxlJobRegistry {

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "native", parameters = {
			@org.hibernate.annotations.Parameter(name = "sequence_name", value = "s_xxl_job_registry") })
	@Column(name = "id", nullable = false, unique = true)
    private int id;
	@Column(name="registry_group", length = 50, nullable = false)
	private String registryGroup;
	 @Column(name="registry_key", length = 255, nullable = false)
    private String registryKey;
	@Column(name="registry_value", length = 255, nullable = false)
    private String registryValue;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="update_time")
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
