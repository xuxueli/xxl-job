package com.xxl.job.admin.common.pojo.query;

import com.xxl.job.admin.common.pojo.dto.JobInfoFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 任务信息 查询对象
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobInfoQuery extends JobInfoFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;




}
