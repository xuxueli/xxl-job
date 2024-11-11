package com.xxl.job.core.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class XxlJobTool {

	public static <E extends Enum<E>> E getEnum(final Class<E> enumClass, final String enumName, final E defaultEnum) {
		if (enumName == null || enumName.isEmpty()) {
			return defaultEnum;
		}
		try {
			return Enum.valueOf(enumClass, enumName);
		} catch (final IllegalArgumentException ex) {
			return defaultEnum;
		}
	}

	public static <E extends Enum<E>> E getEnum(final Class<E> enumClass, final String enumName) {
		return getEnum(enumClass, enumName, null);
	}

	public static ThreadFactory namedThreadFactory(final String namePrefix) {
		final AtomicInteger counter = new AtomicInteger();
		return r -> new Thread(r, namePrefix + counter.incrementAndGet());
	}

}