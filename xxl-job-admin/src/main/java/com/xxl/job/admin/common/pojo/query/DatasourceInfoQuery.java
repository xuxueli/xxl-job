package com.xxl.job.admin.common.pojo.query;

import com.xxl.job.admin.common.pojo.dto.DatasourceInfoFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 工作datasource查询
 *
 * @author Rong.Jia
 * @date 2023/05/11
 * @since 2023-05-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceInfoQuery extends DatasourceInfoFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;



}
