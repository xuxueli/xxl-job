package com.xuxueli.executor.sample.nutz;

import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
/**
 * 
 * @author 邓华锋
 *
 */
@IocBy(type=ComboIocProvider.class,args={"*org.nutz.ioc.loader.json.JsonLoader","ioc/",
	  "*org.nutz.ioc.loader.annotation.AnnotationIocLoader","com.xuxueli"})
@Encoding(input="utf-8",output="utf-8")
@Modules(scanPackage=true)
@Localization("msg")
@Ok("json")
@Fail("json")
@SetupBy(MainSetup.class)
public class MainModule {
	
}
