package com.xxl.job.admin.core.constant;

/**
 * job group
 * @author xuxueli 2016-1-15 14:23:05
 */
public class Constants {
	
	public enum JobGroupEnum{
		DEFAULT("默认"),
		WAIMAI("外卖"),
		MOVIE("电影");
		private String desc;
		private JobGroupEnum(String desc){
			this.desc = desc;
		}
		public String getDesc() {
			return desc;
		}
		public static JobGroupEnum match(String name){
			if (name==null || name.trim().length()==0) {
				return null;
			}
			for (JobGroupEnum group : JobGroupEnum.values()) {
				if (group.name().equals(name)) {
					return group;
				}
			}
			return null;
		}
	}
}
