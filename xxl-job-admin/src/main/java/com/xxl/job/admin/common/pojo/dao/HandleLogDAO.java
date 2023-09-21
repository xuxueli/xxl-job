package com.xxl.job.admin.common.pojo.dao;

import lombok.Data;

import java.io.Serializable;

/**
 * 处理日志DAO
 *
 * @author Rong.Jia
 * @date 2023/09/21
 */
@Data
public class HandleLogDAO implements Serializable {

    private static final long serialVersionUID = -7373885778138814893L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 执行-时间
     */
    private String handleTime;

    /**
     * 执行-状态(-1: 运行中,0:成功,其他:失败)
     */
    private Integer handleCode;

    /**
     * 执行-日志
     */
    private String handleMessage;

}
