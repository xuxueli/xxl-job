package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FeiShuJobAlarm implements JobAlarm {
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog){
        String urlString= XxlJobAdminConfig.getAdminConfig().getFeishurl();
        String msgbody = "执行器：" + jobLog.getJobTitle()
                + "\n任务ID：" + jobLog.getJobId()
                +"\n任务描述：" + info.getJobDesc() +"执行出错，请及时处理!"
                +"\n日志id: " + jobLog.getId()
                +"\n" + jobLog.getTriggerMsgFs();

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("text",msgbody);
        jsonObject.put("msg_type","text");
        jsonObject.put("content",jsonObject1);

        try {
            OkHttpClient httpClient  = new OkHttpClient.Builder()
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            MediaType contetType = MediaType.get("application/json");
            RequestBody body = RequestBody.create(jsonObject.toString(),contetType);

            Request request = new Request.Builder()
                    .url(urlString)
                    .post(body)
                    .build();

            Response response = httpClient.newCall(request).execute();
            assert response.body() != null;
            return true;
        }catch(Exception e){
            return  false;
        }
    }
}
