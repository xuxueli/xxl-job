package com.xxl.job.admin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Data
@NoArgsConstructor
public class XxlJobUser {
	
	private int id;
	private String username;		// 账号
	private String password;		// 密码
	private String token;			// 登录token
	private int role;				// 角色：0-普通用户、1-管理员
	private String permission;		// 权限：执行器ID列表，多个逗号分割

	private String repeatPassword;        // 确认密码
	private String sign;


}
