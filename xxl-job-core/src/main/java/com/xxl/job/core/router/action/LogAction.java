package com.xxl.job.core.router.action;

import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.router.IAction;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;

import java.util.Date;

/**
 * Created by xuxueli on 16/7/22.
 */
public class LogAction extends IAction {

    @Override
    public ResponseModel execute(RequestModel requestModel) {
        String logConteng = XxlJobFileAppender.readLog(new Date(requestModel.getLogDateTim()), requestModel.getLogId());
        return new ResponseModel(ResponseModel.SUCCESS, logConteng);
    }

}
