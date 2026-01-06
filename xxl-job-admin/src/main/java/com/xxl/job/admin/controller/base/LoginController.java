package com.xxl.job.admin.controller.base;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.admin.platform.security.SecurityContext;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
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
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithCookie(request, response);

		if (loginInfoResponse.isSuccess()) {
			modelAndView.setView(new RedirectView("/",true,false));
			return modelAndView;
		}
		return new ModelAndView("base/login");
	}

	@RequestMapping(value="/doLogin", method=RequestMethod.POST)
	@ResponseBody
	@XxlSso(login=false)
	public Response<String> doLogin(HttpServletRequest request, HttpServletResponse response,
									@RequestParam("userName") String userName,
									@RequestParam("password") String password,
									@RequestParam("sign") String sign,
									@RequestParam(value = "ifRemember", required = false) String ifRemember) throws Exception {
		Keypair keypair = SecurityContext.getInstance().findKeypair(sign);
		if (keypair == null) {
			return Response.ofFail(I18nUtil.getString("login_param_unvalid"));
		}
		password = Sm2.doDecrypt(password, keypair.getPrivateKey());


		// param
		boolean ifRem = StringTool.isNotBlank(ifRemember) && "on".equals(ifRemember);
		if (StringTool.isBlank(userName) || StringTool.isBlank(password)){
			return Response.ofFail( I18nUtil.getString("login_param_empty") );
		}

		// valid user、status
		XxlJobUser xxlJobUser = xxlJobUserMapper.loadByUserName(userName);
		if (xxlJobUser == null) {
			return Response.ofFail( I18nUtil.getString("login_param_unvalid") );
		}

		// valid passowrd
		if (!SecurityContext.getInstance().matchPassword(password,xxlJobUser.getPassword())) {
			return Response.ofFail(I18nUtil.getString("login_param_unvalid"));
		}

		// xxl-sso, do login
		LoginInfo loginInfo = new LoginInfo(String.valueOf(xxlJobUser.getId()), UUIDTool.getSimpleUUID());
		Response<String> result= XxlSsoHelper.loginWithCookie(loginInfo, response, ifRem);

		return Response.of(result.getCode(), result.getMsg());
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.POST)
	@ResponseBody
	@XxlSso(login=false)
	public Response<String> logout(HttpServletRequest request, HttpServletResponse response){

		// xxl-sso, do logout
		Response<String> result = XxlSsoHelper.logoutWithCookie(request, response);

		return Response.of(result.getCode(), result.getMsg());
	}

	@RequestMapping("/updatePwd")
	@ResponseBody
	@XxlSso
	public Response<String> updatePwd(HttpServletRequest request,
									  @RequestParam("oldPassword") String oldPassword,
									  @RequestParam("password") String password,
									  @RequestParam("repeatPassword") String repeatPassword,
									  @RequestParam("sign") String sign) throws Exception {

		// valid
		if (oldPassword==null || oldPassword.trim().isEmpty()){
			return Response.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
		}
		if (password==null || password.trim().isEmpty()){
			return Response.ofFail(I18nUtil.getString("system_please_input") + I18nUtil.getString("change_pwd_field_oldpwd"));
		}

		Keypair keypair = SecurityContext.getInstance().findKeypair(sign);
		if (keypair == null) {
			return Response.ofFail(I18nUtil.getString("login_param_unvalid"));
		}

		password = Sm2.doDecrypt(password, keypair.getPrivateKey());
		oldPassword = Sm2.doDecrypt(oldPassword, keypair.getPrivateKey());
		repeatPassword = Sm2.doDecrypt(repeatPassword, keypair.getPrivateKey());
		if(!password.equals(repeatPassword)){
			return Response.ofFail(I18nUtil.getString("repeat_password_not_match"));
		}

		password = password.trim();
		if (!(password.length()>=4 && password.length()<=20)) {
			return Response.ofFail(I18nUtil.getString("system_lengh_limit")+"[4-20]" );
		}

		// md5 password
		String passwordHash = SecurityContext.getInstance().encodePassword(password);

		// valid old pwd
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		XxlJobUser existUser = xxlJobUserMapper.loadByUserName(loginInfoResponse.getData().getUserName());
		if (!SecurityContext.getInstance().matchPassword(oldPassword,existUser.getPassword())) {
			return Response.ofFail(I18nUtil.getString("change_pwd_field_oldpwd") + I18nUtil.getString("system_unvalid"));
		}

		// write new
		existUser.setPassword(passwordHash);
		xxlJobUserMapper.update(existUser);

		return Response.ofSuccess();
	}

}
