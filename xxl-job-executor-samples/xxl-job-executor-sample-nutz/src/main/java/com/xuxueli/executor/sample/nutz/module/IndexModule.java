package com.xuxueli.executor.sample.nutz.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class IndexModule {
	
	@At("/")
	@Ok("json")
	public String index() {
		return "xxl job executor running.";
	}

}
