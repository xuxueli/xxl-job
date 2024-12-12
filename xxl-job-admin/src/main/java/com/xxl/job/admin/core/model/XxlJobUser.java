package com.xxl.job.admin.core.model;

import org.springframework.util.StringUtils;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
public class XxlJobUser {

	private int id;
	private String username;        // 账号
	private String password;        // 密码
	private int role;                // 角色：0-普通用户、1-管理员
	private String permission;    // 权限：执行器ID列表，多个逗号分割

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
	public boolean validPermission(int jobGroup) {
		if (this.role == 1) {
			return true;
		}
		final String permissions = this.permission;
		if (StringUtils.hasLength(permissions)) {
			final String target = Integer.toString(jobGroup);
			int pos = permissions.indexOf(target);
			final int length = permissions.length();
			while (pos != -1 && pos < length) {
				int end = pos + target.length();
				if ((pos == 0 || permissions.charAt(pos - 1) == ',')
						&& (end == length || permissions.charAt(end) == ',')) {
					return true;
				}
				pos = permissions.indexOf(target, end);
			}
		}
		return false;
	}

}
