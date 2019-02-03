package com.xxl.job.admin.controller.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限限制
 * @author yang.liu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermessionLimit {

	/**
	 * 要求用户登录
	 *
	 * @return
	 */
	boolean limit() default true;

	/**
	 * 要求管理员权限
	 *
	 * @return
	 */
	boolean adminuser() default false;

}