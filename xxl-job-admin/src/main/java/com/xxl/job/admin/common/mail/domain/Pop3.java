package com.xxl.job.admin.common.mail.domain;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * pop3
 *
 * @author Rong.Jia
 * @date 2021/07/27 11:39:51
 */
@Data
public class Pop3 implements Serializable {

    private static final long serialVersionUID = 3132277528595478625L;

    /**
     * 确定是否在SMTP之前使用pop3, 默认：fasle
     */
    private Boolean popBeforeSmtp = Boolean.FALSE;

    /**
     * pop3服务器地址.
     */
    private String popHost;

    /**
     * pop3 账号
     */
    private String popUsername;

    /**
     * pop3 密码
     */
    private String popPassword;

    public Boolean getPopBeforeSmtp() {
        return ObjectUtil.isNull(popBeforeSmtp) ? Boolean.FALSE : popBeforeSmtp;
    }
}
