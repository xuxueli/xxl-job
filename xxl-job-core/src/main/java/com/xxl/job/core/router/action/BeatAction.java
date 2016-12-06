package com.xxl.job.core.router.action;

import com.xxl.job.core.router.IAction;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;

/**
 * Created by xuxueli on 16/7/22.
 */
public class BeatAction extends IAction {

    @Override
    public ResponseModel execute(RequestModel requestModel) {
        return new ResponseModel(ResponseModel.SUCCESS, "i am alive.");
    }

}
