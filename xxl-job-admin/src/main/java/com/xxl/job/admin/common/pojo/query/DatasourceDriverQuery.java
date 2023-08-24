package com.xxl.job.admin.common.pojo.query;

import com.xxl.job.admin.common.pojo.dto.DatasourceDriverFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 数据源驱动查询对象
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceDriverQuery extends DatasourceDriverFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;



}
