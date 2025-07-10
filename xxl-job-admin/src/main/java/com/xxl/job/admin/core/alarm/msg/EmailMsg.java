package com.xxl.job.admin.core.alarm.msg;

import com.xxl.job.admin.core.util.JacksonUtil;

/**
 * @author: Dao-yang.
 * @date: Created in 2025/7/10 19:48
 */
public class EmailMsg extends BaseMsg {
    private String title;
    private String content;
    private String[] recipients;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    @Override
    public String toJson() {
        return JacksonUtil.writeValueAsString(this);
    }
}
