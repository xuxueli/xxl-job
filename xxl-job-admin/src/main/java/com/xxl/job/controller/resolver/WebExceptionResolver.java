package com.xxl.job.controller.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.xxl.job.client.util.JacksonUtil;
import com.xxl.job.core.model.ReturnT;

/**
 * common exception resolver
 * @author xuxueli 2016-1-6 19:22:18
 */
public class WebExceptionResolver implements HandlerExceptionResolver {
	private static transient Logger logger = LoggerFactory.getLogger(WebExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		logger.error("system catch exception:{}", ex);
		
		ModelAndView mv = new ModelAndView();
		HandlerMethod method = (HandlerMethod)handler;
		ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
		if (responseBody != null) {
			mv.addObject("result", JacksonUtil.writeValueAsString(new ReturnT<String>(500, ex.toString().replaceAll("\n", "<br/>"))));
			mv.setViewName("/common/common.result");
		} else {
			mv.addObject("exceptionMsg", ex.toString().replaceAll("\n", "<br/>"));	
			mv.setViewName("/common/common.exception");
		}
		return mv;
	}
	
}