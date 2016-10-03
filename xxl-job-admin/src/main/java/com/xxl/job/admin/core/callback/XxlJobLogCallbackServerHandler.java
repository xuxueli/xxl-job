package com.xxl.job.admin.core.callback;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.util.XxlJobNetCommUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by xuxueli on 2016-5-22 11:15:42
 */
public class XxlJobLogCallbackServerHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(XxlJobLogCallbackServerHandler.class);

	@Override
	public void handle(String s, Request baseRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");

		// parse hex-json to request model
		String requestHex = httpServletRequest.getParameter(XxlJobNetCommUtil.HEX);

		// do biz
		ResponseModel responseModel = dobiz(requestHex);

		// format response model to hex-json
		String responseHex = XxlJobNetCommUtil.formatObj2HexJson(responseModel);

		// response
		httpServletResponse.setContentType("text/html;charset=utf-8");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		httpServletResponse.getWriter().println(responseHex);
	}

	private ResponseModel dobiz(String requestHex){

		// valid hex
		if (requestHex==null || requestHex.trim().length()==0) {
			return new ResponseModel(ResponseModel.FAIL, "request hex is null.");
		}

		// valid request model
		RequestModel requestModel = XxlJobNetCommUtil.parseHexJson2Obj(requestHex, RequestModel.class);
		if (requestModel==null) {
			return new ResponseModel(ResponseModel.FAIL, "request hex parse fail.");
		}

		// valid log item
		XxlJobLog log = DynamicSchedulerUtil.xxlJobLogDao.load(requestModel.getLogId());
		if (log == null) {
			return new ResponseModel(ResponseModel.FAIL, "log item not found.");
		}

		// trigger success, to trigger child job, and avoid repeat trigger child job
		String childTriggerMsg = null;
		if (ResponseModel.SUCCESS.equals(requestModel.getStatus()) && !ResponseModel.SUCCESS.equals(log.getHandleStatus())) {
			XxlJobInfo xxlJobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(log.getJobGroup(), log.getJobName());
			if (xxlJobInfo!=null && StringUtils.isNotBlank(xxlJobInfo.getChildJobKey())) {
				childTriggerMsg = "<hr>";
				String[] childJobKeys = xxlJobInfo.getChildJobKey().split(",");
				for (int i = 0; i < childJobKeys.length; i++) {
					String[] jobKeyArr = childJobKeys[i].split("_");
					if (jobKeyArr!=null && jobKeyArr.length==2) {
						XxlJobInfo childJobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(Integer.valueOf(jobKeyArr[0]), jobKeyArr[1]);
						if (childJobInfo!=null) {
							try {
								boolean ret = DynamicSchedulerUtil.triggerJob(childJobInfo.getJobName(), String.valueOf(childJobInfo.getJobGroup()));

								// add msg
								childTriggerMsg += MessageFormat.format("<br> {0}/{1} 触发子任务成功, 子任务Key: {2}, status: {3}, 子任务描述: {4}",
										(i+1), childJobKeys.length, childJobKeys[i], ret, childJobInfo.getJobDesc());
							} catch (SchedulerException e) {
								logger.error("", e);
							}
						} else {
							childTriggerMsg += MessageFormat.format("<br> {0}/{1} 触发子任务失败, 子任务xxlJobInfo不存在, 子任务Key: {2}",
									(i+1), childJobKeys.length, childJobKeys[i]);
						}
					} else {
						childTriggerMsg += MessageFormat.format("<br> {0}/{1} 触发子任务失败, 子任务Key格式错误, 子任务Key: {2}",
								(i+1), childJobKeys.length, childJobKeys[i]);
					}
				}

			}
		}

		// success, save log
		log.setHandleTime(new Date());
		log.setHandleStatus(requestModel.getStatus());
		log.setHandleMsg(requestModel.getMsg() + childTriggerMsg);
		DynamicSchedulerUtil.xxlJobLogDao.updateHandleInfo(log);

		return new ResponseModel(ResponseModel.SUCCESS, null);
	}

}
