package com.xxl.job.admin.core.alarm.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xxl.job.admin.core.util.JacksonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 飞书消息
 */
public class FeiShuTextMsgReq extends BaseMsg {


    public FeiShuTextMsgReq() {
        List<Map<String, Object>> elements = new ArrayList<>();
        Map<String, Boolean> config = new HashMap<>();
        config.put("wide_screen_mode", true);
        card.put("config", config);
        card.put("elements", elements);
        card.put("header", header);
        text.put("tag", "markdown");
        //header
        header.put("template", "green");
        header.put("title", headTxt);

        headTxt.put("tag", "plain_text");
        headTxt.put("content", "");
        elements.add(text);
    }


    private Map<String, Object> card = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> text = new HashMap<>();

    @JsonProperty(value = "msg_type")
    private String msg_type = "interactive";


    @JsonIgnore
    private Map<String, Object> header = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> headTxt = new HashMap<>();



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

    @JsonIgnore
    public FeiShuTextMsgReq withTitle(String title) {
        headTxt.put("content",title);
        return this;
    }


    @JsonIgnore
    public FeiShuTextMsgReq withTitleColor(String color) {
        header.put("template", color);
        return this;
    }

    public Map<String, Object> getCard() {
        return card;
    }

    @Override
    public String toJson() {
        return JacksonUtil.writeValueAsString(this);
    }
}
