package com.xxl.job.admin.common.mail.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author Rong.Jia
 * @date 2021/07/27 08:50:51
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {


    // 附件不存在
    ATTACHMENT_DOES_NOT_EXIST("附件不存在"),

    // 接收者不能为空
    THE_RECEIVER_CANNOT_BE_EMPTY("接收者不能为空"),

    // 消息不能为空
    THE_MESSAGE_CANNOT_BE_EMPTY("消息不能为空"),

    // 你的邮件客户端不支持html邮件
    YOUR_EMAIL_CLIENT_DOES_NOT_SUPPORT_HTML_MESSAGES("你的邮件客户端不支持html邮件"),

    // 主题不能为空
    THE_TOPIC_CANNOT_BE_EMPTY("主题不能为空"),

    // 属性不能为空
    THE_PROPERTY_CANNOT_BE_EMPTY("'%s' 属性不能为空"),

    // 邮箱账号信息不能为空
    THE_EMAIL_ACCOUNT_INFORMATION_CANNOT_BE_EMPTY("邮箱账号信息不能为空"),

    CAN_NOT_RECIPIENT_BCC_CC_AT_THE_SAME_TIME_FOR_EMPTY("不能收件人,密送人,抄送人同时为空"),




















    ;


    private final String value;









}
