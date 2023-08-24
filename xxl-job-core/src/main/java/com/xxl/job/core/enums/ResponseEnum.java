package com.xxl.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应枚举
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {

    /**
     * 枚举类code 开头使用规则：
     * 0: 成功；
     * 500: 失败
     * 502: 超时
     * 1：参数不正确
     *
     * 1000：公共
     */

    // 成功
    SUCCESS(0, "成功"),

    // 参数不正确
    PARAMETER_ERROR(1, "参数不正确"),

    // 未找到
    NOT_FOUND(404, "请求接口不存在"),

    // 请求方式错误
    REQUEST_MODE_ERROR(405, "请求方式错误, 请检查"),

    //媒体类型不支持
    MEDIA_TYPE_NOT_SUPPORTED(415, "媒体类型不支持"),

    // 失败
    ERROR(500, "失败"),
    SYSTEM_ERROR(500, "系统错误"),
    FILE_LIMIT_EXCEEDED(500, "文件超出限制, 请选择较小文件"),

    TIMEOUT(502, "调用超时"),

    /*-------------------------common begin----------------------------------------*/

    // 1000：公共
    REQUEST_PARAMETER_FORMAT_IS_INCORRECT(1000, "请求参数格式不正确"),
    THE_ID_CANNOT_BE_EMPTY(1002, "ID 不能为空"),
    THE_NAME_CANNOT_BE_EMPTY(1003, "名称不能为空"),
    DATA_QUOTE(1004, "数据被引用，无法执行操作"),
    TIME_IS_EMPTY(1005, "时间为空"),
    INVALID_SPECIFIED_STATE(1006, "指定状态无效"),
    THE_STARTING_TIME_CANNOT_BE_LESS_THAN_OR_EQUAL_TO_THE_CURRENT_TIME(1007, "开始时间不能小于等于当前时间"),
    THE_END_TIME_CANNOT_BE_LESS_THAN_OR_EQUAL_TO_THE_START_TIME(1008, "结束时间不能等于小于开始时间"),
    THE_END_TIME_CANNOT_BE_LESS_THAN_OR_EQUAL_TO_THE_CURRENT_TIME(1009, "结束时间不能小于等于当前时间"),
    THE_CODE_CANNOT_BE_EMPTY(1010, "CODE不能为空"),
    LACK_OF_PARAMETER(1011, "缺少必要参数，请检查"),
    FILE_UPLOAD_EXCEPTION(1012, "文件上传异常 【%s】"),
    FILE_DOES_NOT_EXIST(1013, "文件不存在"),
    THE_PARAMETER_TYPE_IS_INCORRECT(1014, "参数类型不正确"),



    /*-------------------------common end----------------------------------------*/


    DATA_SOURCE_ALREADY_EXISTS(5000, "数据源已存在"),
    THE_DATA_SOURCE_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED(5001, "数据源不存在或已删除"),
    THE_TASK_GROUP_ALREADY_EXISTS(5002, "任务组已存在"),
    THE_TASK_GROUP_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED(5003, "任务组不存在或已删除"),
    DATA_SOURCE_CONNECTION_EXCEPTION(5004, "数据源连接异常,请检查连接参数"),
    THE_DATA_SOURCE_DRIVER_ALREADY_EXISTS(5005, "数据源驱动已存在"),
    THE_DATA_SOURCE_DRIVER_DOES_NOT_EXIST(5006, "数据源驱动不存在,或已删除"),
    THE_TASK_ALREADY_EXISTS(5007, "任务已存在"),
    THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED(5008, "任务不存在或已删除"),
    THE_CHILD_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED(5009, "子任务不存在或已删除"),
    THE_TASK_GROUP_HAS_A_TASK_ASSOCIATION_PROCEDURE(5010, "任务组有存在任务关联, 不可删除"),
    THE_CRON_EXPRESSION_FORMAT_IS_INCORRECT(5011, "CRON表达式格式不正确"),
    THE_SCHEDULING_CONFIGURATION_CANNOT_BE_EMPTY(5012, "调度配置不能为空"),
    THE_SCHEDULING_CONFIGURATION_CANNOT_BE_SMALLER_THAN_1(5013, "调度配置不能小于1"),
    TASK_HANDLER_CANNOT_BE_EMPTY(5014, "任务handler不能为空"),
    HEARTBEAT(5015, "心跳确认异常"),
    IDLE_HEARTBEAT(5016, "空闲心跳确认异常"),
    LOST_FAIL(5017, "任务结果丢失，标记失败"),
    SCHEDULING_FAILED_THE_ACTUATOR_ADDRESS_IS_EMPTY(5018, "调度失败,执行器地址为空"),
    THE_ACTUATOR_ADDRESS_CANNOT_BE_EMPTY(5019, "执行器地址不能为空"),
    THE_CURRENT_SCHEDULING_TYPE_CANNOT_BE_STARTED(5020, "当前调度类型禁止启动"),
    THE_TASK_CONFIGURATION_DOES_NOT_EXIST(5021, "任务配置不存在,或者已删除"),
    THE_TASK_CONFIGURATION_ALREADY_EXISTS(5022, "任务配置已存在"),
    THE_SRC_DATA_SOURCE_DOES_NOT_EXIST(5023, "源数据源不存在或已删除"),
    THE_TARGET_DATA_SOURCE_DOES_NOT_EXIST(5024, "目标数据源不存在或已删除"),
    THE_SRC_TABLE_DOES_NOT_EXIST(5025, "源表不存在或已删除"),
    THE_TARGET_TABLE_DOES_NOT_EXIST(5026, "目标表不存在或已删除"),
    THE_SYNC_BASIS_FIELD_CANNOT_BE_EMPTY(5027, "同步依据字段不能为空"),


















    ;

    private final Integer code;
    private final String message;





}
