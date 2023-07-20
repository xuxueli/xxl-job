package com.xxl.job.admin.core.alarm.impl;

class TelegramRequest{
    public TelegramRequest() {
    }
    public TelegramRequest(String text, String parse_mode) {
        this.text = text;
        this.parse_mode = parse_mode;
    }
    private String text;

    private String parse_mode;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getParse_mode() {
        return parse_mode;
    }
    public void setParse_mode(String parse_mode) {
        this.parse_mode = parse_mode;
    }
}