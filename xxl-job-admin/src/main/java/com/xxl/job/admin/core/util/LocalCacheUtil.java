package com.xxl.job.admin.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.util.StringUtils;

/**
 * local cache tool
 *
 * @author xuxueli 2018-01-22 21:37:34
 */
public class LocalCacheUtil {

	private static final ConcurrentMap<String, LocalCacheData> cacheRepository = new ConcurrentHashMap<>();   // 类型建议用抽象父类，兼容性更好；

	/**
	 * set cache
	 */
	public static boolean set(String key, Object val, long cacheTime) {
		// clean timeout cache, before set new cache (avoid cache too much)
		cleanTimeoutCache();

		// set new cache
		if (!StringUtils.hasText(key)) {
			return false;
		}
		if (val == null || cacheTime <= 0) {
			remove(key);
		}
		long timeoutTime = System.currentTimeMillis() + cacheTime;
		LocalCacheData item = new LocalCacheData(val, timeoutTime);
		cacheRepository.put(key, item);
		return true;
	}

	/**
	 * remove cache
	 */
	public static boolean remove(String key) {
		if (!StringUtils.hasText(key)) {
			return false;
		}
		cacheRepository.remove(key);
		return true;
	}

	/**
	 * get cache
	 */
	public static Object get(String key) {
		if (!StringUtils.hasText(key)) {
			return null;
		}
		LocalCacheData item = cacheRepository.get(key);
		if (item != null) {
			if (System.currentTimeMillis() < item.timeoutTime) {
				return item.val;
			}
			cacheRepository.remove(key);
		}
		return null;
	}

	/**
	 * clean timeout cache
	 */
	public static boolean cleanTimeoutCache() {
		if (!cacheRepository.isEmpty()) {
			final long now = System.currentTimeMillis();
			for (Map.Entry<String, LocalCacheData> entry : cacheRepository.entrySet()) {
				LocalCacheData item = entry.getValue();
				if (item != null) {
					if (now >= item.timeoutTime) {
						cacheRepository.remove(entry.getKey());
					}
				}
			}
		}
		return true;
	}

	private static class LocalCacheData {

		Object val;
		long timeoutTime;

		public LocalCacheData(Object val, long timeoutTime) {
			this.val = val;
			this.timeoutTime = timeoutTime;
		}

	}

}