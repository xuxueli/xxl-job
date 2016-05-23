package com.xxl.job.core.glue.loader;

/**
 * code source loader
 * @author xuxueli 2016-1-2 20:01:39
 */
public interface GlueLoader {

	/**
	 * load code source by name, ensure every load is the latest.
	 * @param name
	 * @return
	 */
	public String load(String job_group, String job_name);
	
}
