/**
 * @author Haining.Liu
 * @date 2019年12月7日 下午6:56:54
 * @Description:
 * @Copyright: 2019 版权所有：
 */
package com.xxl.job.executor.core.http;

import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Haining.Liu
 * @Description:
 */
@Component
public class JobReq {
	public static Map<String, Object> Header;

	@Value("${xxl.job.admin.name}")
	private String name;
	@Value("${xxl.job.admin.pwd}")
	private String pwd;
	@Value("${xxl.job.accessToken}")
	private String accessToken;
	@Value("${xxl.job.admin.addresses}")
	private String addresses;

	/**
	 * 模拟RPC请求，绕过权限验证
	 * @param arg {@link HttpSender.Arg}：请求参数；
	 * @return {@link String}：响应结果。
	 */
	public String send(HttpSender.Arg arg) {
		if (arg.header == null)
			arg.setHeader(new HashMap<>(8));
		if (Header != null)
			arg.header.putAll(Header);
		if (arg.header.get("Content-Type") == null || arg.header.get("content-type") == null)
			arg.header.put("Content-Type", "application/json");
		if (!arg.url.startsWith("http://") && !arg.url.startsWith("https://"))
			arg.setUrl(this.addresses + arg.url);
		arg.header.put(XxlJobRemotingUtil.XXL_RPC_ACCESS_TOKEN, this.accessToken);
		return HttpSender.SendGetStr(arg.setRedirect(false).setRedirectFn(conn -> {
			String location = conn.getHeaderField("Location");
			if (location.indexOf("/toLogin") == -1)
				return arg.setUrl(location);

			HttpURLConnection con = HttpSender.Send(HttpSender.Arg.New(this.addresses + "/login", "userName=" + this.name + "&password=" + this.pwd));
			if (Header == null)
				Header = new HashMap<>(8);
			Header.put("Cookie", con.getHeaderField("Set-Cookie"));
			arg.header.putAll(Header);
			return arg;
		}));
	}
}
