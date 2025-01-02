package com.xxl.job.admin.controller.interceptor;

import java.util.HashMap;
import javax.servlet.http.*;

import com.xxl.job.admin.core.util.FtlUtil;
import com.xxl.job.admin.core.util.I18nUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * push cookies to model as cookieMap
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class CookieInterceptor implements AsyncHandlerInterceptor {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// cookie
		if (modelAndView != null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				HashMap<String, Cookie> cookieMap = new HashMap<>(cookies.length, 1F);
				for (Cookie ck : cookies) {
					cookieMap.put(ck.getName(), ck);
				}
				modelAndView.addObject("cookieMap", cookieMap);
			}
			modelAndView.addObject("I18nUtil", FtlUtil.generateStaticModel(I18nUtil.class.getName()));
		}
	}
	
}
