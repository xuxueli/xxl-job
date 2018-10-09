package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermessionLimit;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.serialize.HessianSerializer;
import com.xxl.job.core.util.RpcUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
public class JobApiController {

    private RpcResponse doInvoke(HttpServletRequest request) {
        return RpcUtil.getRpcResponse(request);
    }

    @RequestMapping(AdminBiz.MAPPING)
    @PermessionLimit(limit=false)
    public void api(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // invoke
        RpcResponse rpcResponse = doInvoke(request);

        // serialize response
        byte[] responseBytes = HessianSerializer.serialize(rpcResponse);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        //baseRequest.setHandled(true);

        OutputStream out = response.getOutputStream();
        out.write(responseBytes);
        out.flush();
    }

}
