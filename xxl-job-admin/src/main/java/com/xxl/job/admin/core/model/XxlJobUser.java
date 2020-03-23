package com.xxl.job.admin.core.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Entity
@Table(name = "xxl_job_user", indexes = {
		@Index(name = "i_username", columnList = "username", unique = true)})
public class XxlJobUser {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator") // 使用基于雪花算法的主键生成策略
	@GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
	private Long id;
	@Column(name = "username", nullable = false, length = 50)
	private String username;		// 账号
	@Column(name = "password", nullable = false, length = 50)
	private String password;		// 密码
	@Column(name = "role", nullable = false, length = 4)
	private int role;				// 角色：0-普通用户、1-管理员
	@Column(name = "permission", length = 512)
	private String permission;	// 权限：执行器ID列表，多个逗号分割

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
	public boolean validPermission(long jobGroup){
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
