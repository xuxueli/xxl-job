package com.xxl.job.admin.core.util;

import javax.persistence.Table;

/**
 * 
 * @author spenggch 2022-09-30 15:57:02
 *
 */
public class EntityUtil {

	private static String XxlJobTablePrefix = null;

	public static void setTablePrefix(String tablePrefix) {
		XxlJobTablePrefix = tablePrefix;
	}

	public static String getFullTablename(Class<?> entityClass) {
		String tableName = getTablename(entityClass);
		if (tableName != null && XxlJobTablePrefix != null && XxlJobTablePrefix.length() > 1) {
			tableName = XxlJobTablePrefix.trim() + tableName;
		}
		return tableName;
	}

	public static String getTablename(Class<?> entityClass) {
		Table table = entityClass.getAnnotation(Table.class);
		if (table != null) {
			return table.name();
		}
		return null;
	}
}
