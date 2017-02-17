package com.xxl.job.core.router.action;

import java.util.Date;

import com.xxl.job.core.log.TwoTuple;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.router.IAction;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;

/**
 * Created by xuxueli on 16/7/22.
 */
public class LogAction extends IAction {

    @Override
    public ResponseModel execute(RequestModel requestModel) {
    	TwoTuple<String,Integer> logConteng = XxlJobFileAppender.readLog(new Date(requestModel.getLogDateTim()), requestModel.getLogId(),requestModel.getLogStart());
        return new ResponseModel(ResponseModel.SUCCESS, logConteng.first,logConteng.second);
    }

}
