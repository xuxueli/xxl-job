package com.xxl.job.admin.core.model;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(name = "job_group")
@Proxy(lazy=false)
@TableGenerator(name = "job_group_gen",
        table="primary_key_gen",
        pkColumnName="gen_name",
        valueColumnName="gen_value",
        pkColumnValue="JOB_GROUP_PK",
        allocationSize=1
)
public class XxlJobGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="job_group_gen")
    @Column(name = "id",length = 11,nullable = false)
    private Integer id;
    @Column(name = "app_name",length = 64,nullable = false)
    private String appname;
    @Column(name = "title",length = 12,nullable = false)
    private String title;
    @Column(name = "address_type",length = 4,nullable = false)
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    @Column(name = "address_list",length = 512)
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)

    // registry list
    @Transient
    private List<String> registryList;  // 执行器地址列表(系统注册)
    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

}
