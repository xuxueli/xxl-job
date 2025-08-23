package com.xxl.job.admin.controller.login;

import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.encrypt.SHA256Tool;
import com.xxl.tool.id.UUIDTool;
import com.xxl.tool.response.Response;
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
	private XxlJobUserMapper xxlJobUserMapper;

	@RequestMapping("/login")
	@XxlSso(login = false)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {

		// xxl-sso, logincheck
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		if (loginInfoResponse.isSuccess()) {
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}

		return new ModelAndView("login");
	}

	@RequestMapping(value="/doLogin", method=RequestMethod.POST)
	@ResponseBody
	@XxlSso(login = false)
	public ReturnT<String> doLogin(HttpServletRequest request,
									HttpServletResponse response,
									@RequestParam("userName") String userName,
									@RequestParam("password") String password,
									@RequestParam(value = "ifRemember", required = false) String ifRemember){

		// param
		boolean ifRem = StringTool.isNotBlank(ifRemember) && "on".equals(ifRemember);
		if (StringTool.isBlank(userName) || StringTool.isBlank(password)){
			return ReturnT.ofFail( I18nUtil.getString("login_param_empty") );
		}

		// valid user、status
		XxlJobUser xxlJobUser = xxlJobUserMapper.loadByUserName(userName);
		if (xxlJobUser == null) {
			return ReturnT.ofFail( I18nUtil.getString("login_param_unvalid") );
		}

		// valid passowrd
		String passwordHash = SHA256Tool.sha256(password);
		if (!passwordHash.equals(xxlJobUser.getPassword())) {
			return ReturnT.ofFail( I18nUtil.getString("login_param_unvalid") );
		}

		// xxl-sso, do login
		LoginInfo loginInfo = new LoginInfo(String.valueOf(xxlJobUser.getId()), UUIDTool.getSimpleUUID());
		Response<String> result= XxlSsoHelper.loginWithCookie(loginInfo, response, ifRem);

		return ReturnT.of(result.getCode(), result.getMsg());
	}

	@RequestMapping(value="/logout", method=RequestMethod.POST)
	@ResponseBody
	@XxlSso(login = false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		// xxl-sso, do logout
		Response<String> result = XxlSsoHelper.logoutWithCookie(request, response);

		return ReturnT.of(result.getCode(), result.getMsg());
	}

}
