package com.xxl.job.admin.core.alarm.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xxl.job.admin.core.util.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 企业微信消息
 */
public class WechatTextMsgReq extends  BaseMsg {

//    {
//        "msgtype": "text",
//            "text": {
//                   "content": ""
//                      "mentioned_mobile_list":[]
//          }
//    }

    @JsonProperty(value = "msgtype")
    private String msgType = "text";

    @JsonProperty(value = "text")
    private Map<String, Object> text = new HashMap<>();

    @JsonIgnore
    public List<String> userList;

    /**
     * 设置用户
     *
     * @param content
     */
    @JsonIgnore
    public WechatTextMsgReq withContent(String content) {
        text.put("content", content);
        return this;
    }

    /**
     * 设置@人
     *
     * @param userList
     */
    @JsonIgnore
    public WechatTextMsgReq withUserList(List<String> userList) {
        text.put("mentioned_mobile_list", userList);
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
