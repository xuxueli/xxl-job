/**
 * @author Haining.Liu
 * @Description:
 * @Copyright: 2019 版权所有：
 */
package com.xxl.job.executor.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 发送超链接
 * @author Haining.Liu
 * @Description:
 */
public class HttpSender {
	private static Logger log = LoggerFactory.getLogger(HttpSender.class);

	public static String Charset = StandardCharsets.UTF_8.name();	//编码格式（GBK、UTF-8、……）

	static {
		
	}

	/**
	 * http协议GET请求方式
	 * @param url {@link String}：发送远程服务器请求的 URL；
	 * @return 远程服务器的响应结果。
	 * @author Haining.Liu
	 * @Title: Get
	 */
	public static String Get(String url) {
		return Get(url, null);
	}
	/**
	 * http协议GET请求方式
	 * @param url {@link String}：发送远程服务器请求的 URL，不包含 ? 和之后的参数；
	 * @param param {@link String}：请求参数。以 param1=value1&param2=value2 的方式。
	 * @return 远程服务器的响应结果。
	 * @author Haining.Liu
	 * @Title: Get
	 */
	public static String Get(String url, String param) {
		return SendGetStr(Arg.New(Method.GET, url, param));
	}
	/**
	 * http协议POST请求方式
	 * @param url {@link String}：发送远程服务器请求的 URL；
	 * @return 远程服务器的响应结果。
	 * @author Haining.Liu
	 * @Title: Post
	 */
	public static String Post(String url) {
		return Post(url, null);
	}
	/**
	 * http协议POST请求方式
	 * @param url {@link String}：发送远程服务器请求的 URL，不包含 ? 和之后的参数；
	 * @param param {@link String}：请求参数。以 param1=value1&param2=value2 的方式。
	 * @return 远程服务器的响应结果。
	 * @author Haining.Liu
	 * @Title: Post
	 */
	public static String Post(String url, String param) {
		return SendGetStr(Arg.New(Method.POST, url, param));
	}
	/**
	 * Java实现http协议的请求和响应
	 * @param arg {@link Arg}：请求参数；
	 * @return {@link String}：远程服务器的响应结果。
	 * @author Haining.Liu
	 */
	public static String SendGetStr(Arg arg) {
		HttpURLConnection conn = Send(arg);
		if (conn == null)
			return null;

		String result = null;
		InputStream in = null;
		BufferedReader bufRed = null;
		try {
			int resCode = conn.getResponseCode();
			if (resCode == HttpURLConnection.HTTP_OK || (resCode > HttpURLConnection.HTTP_OK && resCode < HttpURLConnection.HTTP_MULT_CHOICE)) {
				in = conn.getInputStream();
			} else if (resCode >= HttpURLConnection.HTTP_MULT_CHOICE && resCode < HttpURLConnection.HTTP_BAD_REQUEST) {	//重定向
				if (arg.redirectFn != null) {
					arg = arg.redirectFn.apply(conn);
				} else {
					if (resCode != HttpURLConnection.HTTP_MOVED_TEMP)
						arg.setMethod(Method.GET);
					String location = conn.getHeaderField("Location");
					arg.setUrl(location);
				}
				return result = SendGetStr(arg);
			} else {
				String resMsg = "";
				try {
					resMsg = conn.getResponseMessage();
				} catch (IOException e) {}
				log.info("提示：http响应异常：[" + resCode + ": " + resMsg + "]。");
				in = conn.getErrorStream();
				if (in == null)
					return result = resCode + ": " + resMsg;
			}
//			in = new BufferedInputStream(in, 1024 << (10 - 1));	//设置底层缓冲区为1MB（1kb的10次方）
			bufRed = new BufferedReader(new InputStreamReader(in, Charset));
			int cl = conn.getContentLength();	//获取此连接的 URL 引用的资源的内容长度
			StringBuilder sbf = new StringBuilder(cl > 0 ? cl : (cl = 100));
			int cLen = 1024 << 1;
			char[] c = new char[cl < cLen ? cl : cLen];
			for (int len = 0;(len = bufRed.read(c)) != -1;)
				sbf.append(c, 0, len);
			result = sbf.toString();
		} catch (IOException e) {
			log.error("提示：获取http响应异常！", e);
		} catch (RuntimeException e) {
			log.error("提示：http服务处理异常！", e);
		} finally {
			if (bufRed != null)
				try {
					bufRed.close();
				} catch (IOException e) {
					bufRed = null;
					log.error("提示：http响应流关闭失败！", e);
				}
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					in = null;
				}
			conn.disconnect();
		}
		return result;
	}
	/**
	 * Java实现http协议的请求和响应
	 * @param arg {@link Arg}：请求参数；
	 * @return {@link InputStream}：远程服务器的响应流。
	 * @author Haining.Liu
	 */
	public static HttpURLConnection Send(Arg arg) {
		HttpURLConnection conn = null;
		if (!(Method.POST == arg.method))
		try {	//URL编码
			arg.setUrl(URLEncoder.encode(arg.url, arg.charset));
		} catch (Exception e) {
			log.error("提示：http请求URL[" + arg.url + "]编码异常。", e);
		}
		try {
			URL url = new URL(arg.url);
			conn = (HttpURLConnection) url.openConnection();
		/*设置请求头信息。告诉服务器，你客户端的配置、需求*/
			if (arg.header == null)
				arg.header = new HashMap<String, Object>(4);
			if (arg.header.get("Accept-Language") == null)
				arg.header.put("Accept-Language", "zh-CN, zh; q=0.9");
			if (arg.header.get("Charset") == null)
				arg.header.put("Charset", arg.charset);
			if (arg.header.get("Accept") == null)
				arg.header.put("Accept", "application/json, application/xml, text/html; q=0.9, text/plain, */*; q=0.1");
			for (Map.Entry<String, Object> entry : arg.header.entrySet()) {
				Object v = entry.getValue();
				if (v != null)
					conn.setRequestProperty(entry.getKey(), v.toString());
			}
		/*配置客户端*/
			conn.setRequestMethod(arg.method.name());		//设置请求方式。
			conn.setDoInput(true);							//设置为允许获取服务器的响应流。
			conn.setDoOutput(true);							//设置为允许请求给服务器的输出流。
			conn.setUseCaches(false);						//不允许连接使用任何可用的缓存。
			conn.setInstanceFollowRedirects(arg.redirect);	//是否自动重定向。
			conn.setConnectTimeout(30000);					//设置一个指定的通信链接超时值（毫秒）。

			conn.connect();	//打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
			if (!StringUtils.isEmpty(arg.param)) {
				if (!(Method.POST == arg.method))			// 非"POST"请求，将请求参数进行编码
					arg.setParam(URLEncoder.encode(arg.param, arg.charset));
				try (
						OutputStream out = conn.getOutputStream(); // 获取输出流。
					) {
					out.write(arg.param.getBytes(arg.charset)); // 发送参数信息。
					out.flush();
				} catch (Exception e) {
					log.error("提示：请求参数输出流发送失败！", e);
				}
			}
		} catch (MalformedURLException e) {
			log.error("提示：发送的链接URL路径‘" + arg.url + "’字符不规范，不能被解析。", e);
		} catch (IOException e) {
			log.error("提示：发送链接‘" + arg.url + "’，获取本地链接失败！", e);
		} catch (Exception e) {
			log.error("提示：发送链接‘" + arg.url + "’异常！", e);
		}
		return conn;
	}

	/**
	 * 请求方式
	 * @author Haining.Liu
	 * @Description: GET, POST, PUT, HEAD, PATCH, DELETE, OPTIONS, TRACE
	 */
	public enum Method {
		GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
	}
	/**
	 * 请求参数
	 * @author Haining.Liu
	 * @Description:
	 */
	public static class Arg {
		public Method method = Method.POST;
		public String url;
		public String param;
		public String charset = Charset;
		public boolean redirect = true;
		public Map<String, Object> header;
		public Function<HttpURLConnection, Arg> redirectFn;

		/**
		 * @param url {@link String}：URL路径；
		 * @param param {@link String}：请求参数；
		 * @author Haining.Liu
		 */
		public Arg(String url, String param) {
			super();
			this.url = url;
			this.param = param;
		}

		public Method getMethod() {
			return method;
		}
		public Arg setMethod(Method method) {
			this.method = method;
			return this;
		}
		public String getUrl() {
			return url;
		}
		public Arg setUrl(String url) {
			this.url = url;
			return this;
		}
		public String getParam() {
			return param;
		}
		public Arg setParam(String param) {
			this.param = param;
			return this;
		}
		public String getCharset() {
			return charset;
		}
		public Arg setCharset(String charset) {
			this.charset = charset;
			return this;
		}
		public boolean isRedirect() {
			return redirect;
		}
		public Arg setRedirect(boolean redirect) {
			this.redirect = redirect;
			return this;
		}
		public Map<String, Object> getHeader() {
			return header;
		}
		public Arg setHeader(Map<String, Object> header) {
			this.header = header;
			return this;
		}
		public Function<HttpURLConnection, Arg> getRedirectFn() {
			return redirectFn;
		}
		public Arg setRedirectFn(Function<HttpURLConnection, Arg> redirectFn) {
			this.redirectFn = redirectFn;
			return this;
		}

		/**
		 * @param url {@link String}：URL路径；
		 * @author Haining.Liu
		 */
		public static Arg New(String url) {
			return Arg.New(url, null);
		}
		/**
		 * @param url {@link String}：URL路径；
		 * @param param {@link String}：请求参数；
		 * @author Haining.Liu
		 */
		public static Arg New(String url, String param) {
			return new Arg(url, param);
		}
		/**
		 * @param method {@link Method}：请求方式；
		 * @param url {@link String}：URL路径；
		 * @param param {@link String}：请求参数；
		 * @author Haining.Liu
		 */
		public static Arg New(Method method, String url, String param) {
			return Arg.New(url, param).setMethod(method);
		}
		/**
		 * @param url {@link String}：URL路径；
		 * @param param {@link String}：请求参数；
		 * @param header {@link Map}<{@link String}, {@link Object}>：请求头；
		 * @author Haining.Liu
		 */
		public static Arg New(String url, String param, Map<String, Object> header) {
			return Arg.New(url, param).setHeader(header);
		}
		/**
		 * @param method {@link Method}：请求方式；
		 * @param url {@link String}：URL路径；
		 * @param param {@link String}：请求参数；
		 * @param header {@link Map}<{@link String}, {@link Object}>：请求头；
		 * @author Haining.Liu
		 */
		public static Arg New(Method method, String url, String param, Map<String, Object> header) {
			return Arg.New(method, url, param).setHeader(header);
		}
	}
}
