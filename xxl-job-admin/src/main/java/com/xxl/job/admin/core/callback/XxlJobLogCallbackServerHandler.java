package com.xxl.job.admin.core.callback;

import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.util.XxlJobNetCommUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by xuxueli on 2016-5-22 11:15:42
 */
public class XxlJobLogCallbackServerHandler extends AbstractHandler {

	@Override
	public void handle(String s, Request baseRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");

		// parse hex-json to request model
		String requestHex = httpServletRequest.getParameter(XxlJobNetCommUtil.HEX);
		RequestModel requestModel = XxlJobNetCommUtil.parseHexJson2Obj(requestHex, RequestModel.class);

		// process
		ResponseModel responseModel = null;
		XxlJobLog log = DynamicSchedulerUtil.xxlJobLogDao.load(requestModel.getLogId());
		if (log!=null) {
			log.setHandleTime(new Date());
			log.setHandleStatus(requestModel.getStatus());
			log.setHandleMsg(requestModel.getMsg());
			DynamicSchedulerUtil.xxlJobLogDao.updateHandleInfo(log);
			responseModel = new ResponseModel(ResponseModel.SUCCESS, null);
		} else {
			responseModel = new ResponseModel(ResponseModel.FAIL, "log item not found.");
		}

		// format response model to hex-json
		String responseHex = XxlJobNetCommUtil.formatObj2HexJson(responseModel);

		// response
		httpServletResponse.setContentType("text/html;charset=utf-8");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		httpServletResponse.getWriter().println(responseHex);
	}

}
