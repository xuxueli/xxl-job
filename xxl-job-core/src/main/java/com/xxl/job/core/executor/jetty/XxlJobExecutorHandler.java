package com.xxl.job.core.executor.jetty;

import com.xxl.job.core.router.HandlerRouter;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.util.XxlJobNetCommUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xuxueli on 2016/3/2 21:23.
 */
public class XxlJobExecutorHandler extends AbstractHandler {
    private static Logger logger = LoggerFactory.getLogger(XxlJobExecutorHandler.class);

	@Override
	public void handle(String s, Request baseRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");

        // parse hex-json to request model
        String requestHex = httpServletRequest.getParameter(XxlJobNetCommUtil.HEX);
        ResponseModel responseModel = null;
        if (requestHex!=null && requestHex.trim().length()>0) {
            try {
                // route trigger
                RequestModel requestModel = XxlJobNetCommUtil.parseHexJson2Obj(requestHex, RequestModel.class);
                responseModel = HandlerRouter.route(requestModel);
            } catch (Exception e) {
                logger.error("", e);
                responseModel = new ResponseModel(ResponseModel.SUCCESS, e.getMessage());
            }
        }
        if (responseModel == null) {
            responseModel = new ResponseModel(ResponseModel.SUCCESS, "系统异常");
        }

        // format response model to hex-json
        String responseHex = XxlJobNetCommUtil.formatObj2HexJson(responseModel);

        // return
		httpServletResponse.setContentType("text/plain;charset=utf-8");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		httpServletResponse.getWriter().println(responseHex);
	}

}
