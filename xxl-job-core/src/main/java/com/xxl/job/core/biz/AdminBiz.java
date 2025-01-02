package com.xxl.job.core.biz;

import java.util.List;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {

	ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);

	ReturnT<String> registry(RegistryParam registryParam);

	ReturnT<String> registryRemove(RegistryParam registryParam);

}
