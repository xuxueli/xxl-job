package com.xxl.job.admin.common.pojo.query;

import com.xxl.job.admin.common.pojo.dto.JobConfigDetailsFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 任务配置明细查询对象
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobConfigDetailsQuery extends JobConfigDetailsFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;




}
