package com.xxl.job.admin.web.interceptor;

import com.xxl.job.admin.util.I18nUtil;
import com.xxl.tool.freemarker.FtlTool;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;

/**
 * push cookies to model as cookieMap
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class CommonDataInterceptor implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AsyncHandlerInterceptor() {
			@Override
			public void postHandle(HttpServletRequest request,
								   HttpServletResponse response,
								   Object handler,
								   ModelAndView modelAndView) throws Exception {

				// cookie
				if (modelAndView!=null && request.getCookies()!=null && request.getCookies().length>0) {
					HashMap<String, Cookie> cookieMap = new HashMap<String, Cookie>();
					for (Cookie ck : request.getCookies()) {
						cookieMap.put(ck.getName(), ck);
					}
					modelAndView.addObject("cookieMap", cookieMap);
				}

				// static method
				if (modelAndView != null) {
					modelAndView.addObject("I18nUtil", FtlTool.generateStaticModel(I18nUtil.class.getName()));
				}

			}
		}).addPathPatterns("/**");
	}

}
