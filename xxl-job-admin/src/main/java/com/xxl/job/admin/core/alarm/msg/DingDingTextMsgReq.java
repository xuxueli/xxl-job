package com.xxl.job.admin.core.alarm.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xxl.job.admin.core.util.JacksonUtil;

import java.util.HashMap;
import java.util.Map;

public class DingDingTextMsgReq  extends  BaseMsg {

//    {
//        "msgtype": "markdown",
//            "text": {
//                   "content": ""
//
//          }
//    }

    @JsonProperty(value = "msgtype")
    private String msgType = "text";

    @JsonProperty(value = "text")
    private Map<String, Object> text = new HashMap<>();


    /**
     * 设置用户
     *
     * @param content
     */
    @JsonIgnore
    public DingDingTextMsgReq withContent(String content) {
        text.put("content", content);
        return this;
    }


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toJson() {
        return JacksonUtil.writeValueAsString(this);
    }
}
