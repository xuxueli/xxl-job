package com.xuxueli.executor.sample.nutz.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
/**
 * 
 * @author 邓华锋
 *
 */
@IocBean
public class IndexModule {
	
	@At
	@Ok("jsp:index")
	public void index() {
	}
}
