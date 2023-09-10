package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.entity.LoginToken;
import org.apache.ibatis.annotations.Param;

/**
 * 登录TOKEN Mapper接口
 * @author Rong.Jia
 * @date 2023/09/09
 */
public interface LoginTokenMapper extends BaseMapper<LoginToken> {

    /**
     * 根据token 查询登录令牌
     *
     * @param token token
     * @return {@link LoginToken}
     */
    LoginToken findLoginTokenByToken(@Param("token") String token);

    /**
     * 保存
     * @param entity 实体
     * @return 成功数量
     */
    @Override
    int insert(LoginToken entity);

    /**
     * 更新登录令牌
     * @param token token
     * @param updatedTime 更新时间
     */
    void updateLoginTokenByToken(@Param("token") String token, @Param("updatedTime") Long updatedTime);

    /**
     * 删除登录令牌
     * @param token token
     */
    void deleteLoginTokenByToken(@Param("token") String token);


}
