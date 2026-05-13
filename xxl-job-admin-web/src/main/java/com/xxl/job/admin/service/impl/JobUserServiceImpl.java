package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.admin.service.JobUserService;
import com.xxl.tool.response.PageModel;

import org.springframework.stereotype.Service;

/**
 * JobUser service implementation for xxl-job admin web module.
 *
 * @author xuxueli 2019-05-04
 */
@Service
public class JobUserServiceImpl extends ServiceImpl<XxlJobUserMapper, XxlJobUser> implements JobUserService {
    @Override
    public PageModel<XxlJobUser> pageList(int page, int pagesize, String username, int role) {
        /** 原XxlJobUserMapper
         * 
        <select id="pageList" parameterType="java.util.HashMap" resultMap="XxlJobUser">
            SELECT <include refid="Base_Column_List" />
            FROM xxl_job_user AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="username != null and username != ''">
                    AND t.username like CONCAT(CONCAT('%', #{username}), '%')
                </if>
                <if test="role gt -1">
                    AND t.role = #{role}
                </if>
            </trim>
            ORDER BY username ASC
            LIMIT #{offset}, #{pagesize}
        </select>

        <select id="pageListCount" parameterType="java.util.HashMap" resultType="int">
            SELECT count(1)
            FROM xxl_job_user AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="username != null and username != ''">
                    AND t.username like CONCAT(CONCAT('%', #{username}), '%')
                </if>
                <if test="role gt -1">
                    AND t.role = #{role}
                </if>
            </trim>
        </select>
         */
        
        Page<XxlJobUser> p = new Page<XxlJobUser>(page, pagesize);

        p.addOrder(new OrderItem().setColumn("username").setAsc(true));

        QueryWrapper<XxlJobUser> qw = new QueryWrapper<>();
        if (username != null && username != "") {
            qw = qw.like("username", username);
        }
        if (role >= -1) {
            qw = qw.eq("role", role);
        }
        IPage<XxlJobUser> iPage = this.page(p, qw);

        // package result
        PageModel<XxlJobUser> pageModel = new PageModel<>();
        pageModel.setData(iPage.getRecords());
        pageModel.setTotal(Math.toIntExact(iPage.getTotal()));

        return pageModel;
    }

    @Override
    public XxlJobUser loadByUserName(String username) {
        /** 原XxlJobUserMapper
         *  <select id="loadByUserName" parameterType="java.util.HashMap" resultMap="XxlJobUser">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_user AS t
                WHERE t.username = #{username}
            </select>
         */
        return this.getOne(new QueryWrapper<XxlJobUser>().eq("username", username));
    }

    @Override
    public XxlJobUser loadById(int id) {
        /** 原XxlJobUserMapper
         *  <select id="loadById" parameterType="java.util.HashMap" resultMap="XxlJobUser">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_user AS t
                WHERE t.id = #{id}
            </select>
         */
        return this.getById(id);
    }

    @Override
    public boolean updateToken(int id, String token) {
        /** 原XxlJobUserMapper
         *  <update id="updateToken" parameterType="java.util.HashMap" >
                UPDATE xxl_job_user
                SET token = #{token}
                WHERE id = #{id}
            </update>
         */
        return this.update(new UpdateWrapper<XxlJobUser>()
            .set("token", token)
            .eq("id", id)
        );
    }

    @Override
    public boolean updateUser(XxlJobUser xxlJobUser) {
        /** 原XxlJobUserMapper
         *  <update id="update" parameterType="com.xxl.job.admin.model.XxlJobUser" >
                UPDATE xxl_job_user
                SET
                    <if test="password != null and password != ''">
                        password = #{password},
                    </if>
                    role = #{role},
                    permission = #{permission}
                WHERE id = #{id}
            </update>
         */
        UpdateWrapper<XxlJobUser> uw = new UpdateWrapper<XxlJobUser>();
        if (xxlJobUser.getPassword() != null && xxlJobUser.getPassword() != "") {
            uw = uw.set("password", xxlJobUser.getPassword());
        }
        uw = uw.set("role ", xxlJobUser.getRole()).set("permission", xxlJobUser.getPermission()).eq("id", xxlJobUser.getId());
        
        return this.update(uw);
    }

    
}