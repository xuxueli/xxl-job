package com.xxl.job.core.router;

import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;

/**
 * Created by xuxueli on 16/7/22.
 */
public abstract class IAction {

    public abstract ResponseModel execute(RequestModel requestModel);

}
