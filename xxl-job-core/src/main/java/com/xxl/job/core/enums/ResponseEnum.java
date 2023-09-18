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

    UNAUTHORIZED(401, "无权访问(未授权)"),
    AUTHORIZATION_EXPIRES(401, "授权过期, 请求重新登录"),
    NOT_LOGGED_IN(401, "未登录，或者授权过期"),
    ANONYMOUS_SUBJECT_UNAUTHORIZED(401, "无权访问:当前用户是匿名用户，请先登录"),
    AUTHENTICATION_FAILED(401, "身份验证未通过"),
    MISSING_TOKEN_AUTHENTICATION_FAILED(401, "缺失令牌,鉴权失败"),

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
    FILE_DOES_NOT_EXIST(1013, "文件不存在, 请检查"),
    THE_PARAMETER_TYPE_IS_INCORRECT(1014, "参数类型不正确"),



    /*-------------------------common end----------------------------------------*/

    SUBJECT_UNAUTHORIZED(4000, "无权访问:当前用户没有此请求所需权限"),
    USER_NAME_OR_PASSWORD_ERRORS_GREATER_THAN_5_TIMES(4001, "用户名或密码错误次数大于5次,账户已锁定, 请10分钟后再次访问"),
    THE_ACCOUNT_DOES_NOT_EXIST_PLEASE_CHANGE_THE_ACCOUNT_TO_LOGIN(4004, "账号不存在，请更换账号登录"),
    THE_ACCOUNT_OR_PASSWORD_IS_INCORRECT(4009, "账号或密码不正确"),
    ACCOUNT_AUTHORIZATION_EXPIRED(4002, "账号授权过期"),
    ACCOUNT_LOGIN_IS_PROHIBITED(4003, "账号禁止登录"),
    PROHIBIT_THE_LOGIN(4005, "禁止登录"),
    NO_PERMISSIONS(4006, "暂无权限， 请联系管理员"),
    THE_ROLE_IDS_DOES_NOT_EXIST_PLEASE_CHANGE_THE_ACCOUNT_TO_LOGIN(4007, "角色不存在，请联系管理员分配角色"),
    ACCOUNT_AUTOMATIC_LOGOUT(4008, "账号已自动退出登录，无需再次退出登录"),
    THE_ACCOUNT_IS_NOT_EXISTS(4010, "账号不存在"),

    THE_TASK_GROUP_ALREADY_EXISTS(5002, "任务组已存在"),
    THE_TASK_GROUP_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED(5003, "任务组不存在或已删除"),
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

    THE_USER_ALREADY_EXISTS(5007, "用户已存在"),
    THE_USER_DOES_NOT_EXIST(5008, "用户不存在,或已删除"),
    THE_OLD_PASSWORD_IS_INCORRECT(5009, "原密码不正确"),
    THE_NEW_PASSWORD_IS_THE_SAME_AS_THE_OLD_PASSWORD(5010, "新密码与原密码相同"),
    SYSTEM_ADMINISTRATOR_CANNOT_DISABLE(5011,"系统管理员不能禁用"),
    CURRENT_USER_CANNOT_DISABLE(5012,"当前用户不能禁用, 该用户为当前登录用户"),
    THE_ACCOUNT_CANNOT_BE_EMPTY(5013, "账号不能为空"),
    THE_PASSWORD_FORMAT_IS_INCORRECT(5014, "密码格式不正确"),
    SYSTEM_ADMINISTRATOR_CANNOT_DELETE(5015, "系统管理员不能删除"),
    THE_CURRENT_LOGIN_USER_CANNOT_BE_DELETED(5016, "当前登录用户不可删除"),

    THE_KETTLE_ALREADY_EXISTS(5028, "kettle信息已存在"),
    THE_KETTLE_DOES_NOT_EXIST(5029, "kettle信息不存在或已删除"),
    THE_KJB_BOOT_FILE_CANNOT_BE_EMPTY(5030, "kjb引导文件不能为空"),
    FILE_RELATIVE_PATH_PROCESSING_EXCEPTION(5031, "文件相对路径处理异常, 请检查格式是否正确"),
    THE_FILES_IN_THE_COMPRESSED_PACKAGE_ARE_EMPTY(5032, "压缩包内文件为空,请检查"),
    THERE_IS_NO_KJB_FILE_IN_THE_ZIP_PACKAGE(5033, "压缩包内没有'kjb'文件,请检查"),
    THE_FILE_RECOMPRESSED_PACKAGE_IS_ABNORMAL_PROCEDURE(5034, "文件重新打压缩包异常"),
    THE_CRON_EXPRESSION_CANNOT_BE_EMPTY(5035, "CRON表达式不能为空"),







    ;

    private final Integer code;
    private final String message;





}
