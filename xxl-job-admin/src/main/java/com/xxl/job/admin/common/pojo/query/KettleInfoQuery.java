package com.xxl.job.admin.common.pojo.query;

import com.xxl.job.admin.common.pojo.dto.KettleInfoFilterDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * kettle信息查询对象
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KettleInfoQuery extends KettleInfoFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;



 

}
