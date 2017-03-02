package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface AdminBiz {

    public ReturnT<String> callback(HandleCallbackParam handleCallbackParam);

}
