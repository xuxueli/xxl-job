package com.xxl.job.admin.controller.login;

import com.xxl.job.admin.annotation.PermissionLimit;
import com.xxl.job.admin.service.impl.LoginService;
import com.xxl.job.core.biz.model.ReturnT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/auth")
public class LoginController {


	@Resource
	private LoginService loginService;


	@RequestMapping("/toLogin")
	@PermissionLimit(limit=false)
	public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
		if (loginService.ifLogin(request, response) != null) {
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}
		return new ModelAndView("login");
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request,
								   HttpServletResponse response,
								   @RequestParam("userName") String userName,
								   @RequestParam("password") String password,
								   @RequestParam(value = "ifRemember", required = false) String ifRemember){

		boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember))?true:false;
		return loginService.login(request, response, userName, password, ifRem);
	}

	@RequestMapping(value="/logout", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		return loginService.logout(request, response);
	}

}
