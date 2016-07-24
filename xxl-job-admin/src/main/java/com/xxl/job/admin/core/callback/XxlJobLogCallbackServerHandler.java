package com.xxl.job.admin.core.callback;

import com.xxl.job.admin.core.model.ReturnT;
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
		RequestModel requestModel = XxlJobNetCommUtil.parseHexJson2Obj(requestHex, RequestModel.class);

		// process
		ResponseModel responseModel = null;
		XxlJobLog log = DynamicSchedulerUtil.xxlJobLogDao.load(requestModel.getLogId());
		if (log!=null) {

			// trigger success, to trigger child job, and avoid repeat trigger child job
			if (!ResponseModel.SUCCESS.equals(log.getHandleStatus())) {
				XxlJobInfo xxlJobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(log.getJobGroup(), log.getJobName());
				if (xxlJobInfo!=null && StringUtils.isNotBlank(xxlJobInfo.getChildJobKey())) {
					String[] jobKeyArr = xxlJobInfo.getChildJobKey().split("_");
					if (jobKeyArr!=null && jobKeyArr.length==2) {
						XxlJobInfo childJobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(jobKeyArr[0], jobKeyArr[1]);
						if (childJobInfo!=null) {
							try {
								boolean ret = DynamicSchedulerUtil.triggerJob(childJobInfo.getJobName(), childJobInfo.getJobGroup());

								// add msg
								String msg = requestModel.getMsg();
								msg += MessageFormat.format("<br> 触发子任务执行, jobKey:{0}, status:{1}, 描述:{2}", xxlJobInfo.getChildJobKey(), ret, childJobInfo.getJobDesc());
								requestModel.setMsg(msg);
							} catch (SchedulerException e) {
								logger.error("", e);
							}
						}
					}
				}
			}

			// save log
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
