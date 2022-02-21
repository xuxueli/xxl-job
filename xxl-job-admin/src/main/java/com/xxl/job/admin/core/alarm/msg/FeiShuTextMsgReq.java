package com.xxl.job.admin.core.alarm.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 飞书消息
 */
public class FeiShuTextMsgReq {

    public  FeiShuTextMsgReq() {
        List<Map<String, Object>> elements = new ArrayList<>();
        Map<String, Boolean> config = new HashMap<>();
        config.put("wide_screen_mode", true);
        card.put("config", config);
        card.put("elements",elements);
        text.put("tag", "markdown");
        elements.add(text);
    }


    private Map<String,Object> card=new HashMap<>();

    @JsonIgnore
    private Map<String,Object> text=new HashMap<>();

    @JsonProperty(value = "msg_type")
    private  String msgType="interactive";

    /**
     * 设置用户
     *
     * @param content
     */
    @JsonIgnore
    public FeiShuTextMsgReq withContent(String content) {
        text.put("content", content);
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Map<String, Object> getCard() {
        return card;
    }

    public void setCard(Map<String, Object> card) {
        this.card = card;
    }
}
