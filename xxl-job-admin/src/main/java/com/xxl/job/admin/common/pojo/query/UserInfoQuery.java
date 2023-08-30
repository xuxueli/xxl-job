package com.xxl.job.admin.common.pojo.query;

import com.xxl.job.admin.common.pojo.dto.UserInfoFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户信息查询对象
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoQuery extends UserInfoFilterDTO implements Serializable {

    private static final long serialVersionUID = -281379933802293123L;







}