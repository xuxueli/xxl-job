package com.xxl.job.admin.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Entity
@Table(name = "xxl_job_user", indexes = { @Index(name = "i_username", columnList = "username") })
public class XxlJobUser {
	
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "native", parameters = {
			@org.hibernate.annotations.Parameter(name = "sequence_name", value = "s_xxl_job_user") })
	@Column(name = "id", nullable = false, unique = true)
	private int id;
	@Column(name="username", length = 50, nullable = false)
	@Comment("账号")
	private String username;		// 账号
	@Column(name="password", length = 50, nullable = false)
	@Comment("密码")
	private String password;		// 密码
	@Column(name="role", nullable = false)
	@Comment("角色：0-普通用户、1-管理员")
	private int role;				// 角色：0-普通用户、1-管理员
	@Column(name="permission", length = 255)
	@Comment("权限：执行器ID列表，多个逗号分割")
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
