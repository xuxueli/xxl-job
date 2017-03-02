package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface AdminBiz {

    public ReturnT<String> callback(TriggerParam triggerParam);

}
