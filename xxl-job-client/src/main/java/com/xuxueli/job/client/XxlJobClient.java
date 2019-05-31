package com.xuxueli.job.client;

import com.xuxueli.job.client.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;

import java.io.IOException;

/**
 * @author Luo Bao Ding
 * @since 2019/5/23
 */
public interface XxlJobClient {

    ReturnT<String> add(XxlJobInfo jobInfo) throws IOException;

    ReturnT<String> update(XxlJobInfo jobInfo) throws IOException;

    ReturnT<String> remove(String uniqName) throws IOException;

    ReturnT<String> stop(String uniqName) throws IOException;

    ReturnT<String> start(String uniqName) throws IOException;

    ReturnT<String> trigger(String uniqName, String executorParam) throws IOException;

}
