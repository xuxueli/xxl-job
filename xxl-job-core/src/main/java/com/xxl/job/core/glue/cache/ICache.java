package com.xxl.job.core.glue.cache;

/**
 * chche interface
 * @author xuxueli 2016-1-8 15:57:27
 */
public interface ICache {
	
	public boolean set(String key, Object value);
	
	public boolean set(String key, Object value, long timeout);
	
	public Object get(String key);
	
	public boolean remove(String key);
	
}
