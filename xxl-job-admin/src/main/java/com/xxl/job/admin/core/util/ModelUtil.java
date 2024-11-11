package com.xxl.job.admin.core.util;

import java.util.*;
import java.util.function.IntSupplier;

public class ModelUtil {

	/**
	 * 如果能够直接计算出分页记录总数，就直接计算并返回，避免再去查询 COUNT(*)
	 *
	 * @return 返回 -1 表示还需要查询数据库
	 */
	public static int calcTotalCount(List<?> list, int offset, int pageSize) {
		final int size = list.size();
		if (size < pageSize && offset >= 0 && (size > 0 || offset == 0)) {
			return offset + size;
		}
		return -1;
	}

	public static Map<String, Object> pageListResult(final List<?> list, final Integer totalCount) {
		Map<String, Object> maps = new HashMap<>(4, 1F);
		maps.put("recordsTotal", totalCount); // 总记录数
		maps.put("recordsFiltered", totalCount); // 过滤后的总记录数
		maps.put("data", list); // 分页列表
		return maps;
	}

	public static Map<String, Object> pageListResult(final List<?> list, int offset, int pageSize, final IntSupplier totalCountSupplier) {
		int totalCount = calcTotalCount(list, offset, pageSize);
		if (totalCount == -1) {
			totalCount = totalCountSupplier.getAsInt();
		}
		return pageListResult(list, totalCount);
	}

}