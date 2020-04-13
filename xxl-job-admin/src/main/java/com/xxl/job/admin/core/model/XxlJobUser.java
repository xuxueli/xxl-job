package com.xxl.job.admin.core.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Entity
@Table(name = "job_user")
@TableGenerator(name = "job_user_gen",
		table="primary_key_gen",
		pkColumnName="gen_name",
		valueColumnName="gen_value",
		pkColumnValue="JOB_USER_PK",
		allocationSize=1
)
public class XxlJobUser {

	@Id
	@Column(length = 11)
	@GeneratedValue(strategy = GenerationType.TABLE,generator="job_user_gen")
	private int id;
	@Column(name = "username",length = 50,nullable = false)
	private String username;		// 账号
	@Column(name = "password",length = 50,nullable = false)
	private String password;		// 密码
	@Column(name = "role",length = 4,nullable = false)
	private int role;				// 角色：0-普通用户、1-管理员
	@Column(name = "permission",length = 255)
	private String permission;	// 权限：执行器ID列表，多个逗号分割

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	// plugin
	public boolean validPermission(int jobGroup){
		if (this.role == 1) {
			return true;
		} else {
			if (StringUtils.hasText(this.permission)) {
				for (String permissionItem : this.permission.split(",")) {
					if (String.valueOf(jobGroup).equals(permissionItem)) {
						return true;
					}
				}
			}
			return false;
		}

	}

}
