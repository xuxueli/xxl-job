package com.xxl.job.admin.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.service.JobRegistryService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class JobRegistryServiceImpl extends ServiceImpl<XxlJobRegistryMapper, XxlJobRegistry> implements JobRegistryService {

    @Override
    public List<Integer> findDead(int timeout, Date nowTime) {
        /** 原XxlJobRegistryMapper
         *  <select id="findDead" parameterType="java.util.HashMap" resultType="java.lang.Integer" >
                SELECT t.id
                FROM xxl_job_registry AS t
                WHERE t.update_time <![CDATA[ < ]]> DATE_ADD(#{nowTime},INTERVAL -#{timeout} SECOND)
            </select>
         */

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, - timeout);
        Date nowMinusTimeout = calendar.getTime();

        return this.list(
            new QueryWrapper<XxlJobRegistry>().lt("update_time", nowMinusTimeout)
        ).stream().map(i -> Math.toIntExact(i.getId())).toList();
    }

    @Override
    public int removeDead(List<Integer> ids) {
        /** 原XxlJobRegistryMapper
         *  <delete id="removeDead" parameterType="java.lang.Integer" >
                DELETE FROM xxl_job_registry
                WHERE id in
                <foreach collection="ids" item="item" open="(" close=")" separator="," >
                    #{item}
                </foreach>
            </delete>
         */
        return this.removeByIds(ids) ? 1 : 0;
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        /** 原XxlJobRegistryMapper
         *  <select id="findAll" parameterType="java.util.HashMap" resultMap="XxlJobRegistry">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_registry AS t
                WHERE t.update_time <![CDATA[ > ]]> DATE_ADD(#{nowTime},INTERVAL -#{timeout} SECOND)
            </select>
         */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, - timeout);
        Date nowMinusTimeout = calendar.getTime();
        return this.list(
            new QueryWrapper<XxlJobRegistry>().gt("update_time", nowMinusTimeout)
        );
    }

    @Override
    public int registrySaveOrUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        /** 原XxlJobRegistryMapper
         *  <insert id="registrySaveOrUpdate" >
                INSERT INTO xxl_job_registry( `registry_group` , `registry_key` , `registry_value`, `update_time`)
                VALUES( #{registryGroup}  , #{registryKey} , #{registryValue}, #{updateTime})
                ON DUPLICATE KEY UPDATE
                    `update_time` = #{updateTime}
            </insert>
         */

        // Try to find existing registry entry
        QueryWrapper<XxlJobRegistry> qw = new QueryWrapper<>();
        qw.eq("registry_group", registryGroup)
          .eq("registry_key", registryKey)
          .eq("registry_value", registryValue);
        XxlJobRegistry existing = this.getOne(qw);

        if (existing != null) {
            // Update existing
            existing.setUpdateTime(updateTime);
            return this.updateById(existing) ? 1 : 0;
        } else {
            // Insert new
            XxlJobRegistry registry = new XxlJobRegistry();
            registry.setRegistryGroup(registryGroup);
            registry.setRegistryKey(registryKey);
            registry.setRegistryValue(registryValue);
            registry.setUpdateTime(updateTime);
            return this.save(registry) ? 1 : 0;
        }
    }

    @Override
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        /** 原XxlJobRegistryMapper
         *  <delete id="registryDelete" >
                DELETE FROM xxl_job_registry
                WHERE registry_group = #{registryGroup}
                    AND registry_key = #{registryKey}
                    AND registry_value = #{registryValue}
            </delete>
         */
        return this.remove(new QueryWrapper<XxlJobRegistry>()
                .eq("registry_group", registryGroup)
                .eq("registry_key", registryKey)
                .eq("registry_value", registryValue)) ? 1 : 0;
    }

    @Override
    public int removeByRegistryGroupAndKey(String registryGroup, String registryKey) {
        /** 原XxlJobRegistryMapper
         *  <delete id="removeByRegistryGroupAndKey" >
                DELETE FROM xxl_job_registry
                WHERE registry_group = #{registryGroup}
                AND registry_key = #{registryKey}
            </delete>
         */
        return this.remove(new QueryWrapper<XxlJobRegistry>()
                .eq("registry_group", registryGroup)
                .eq("registry_key", registryKey)) ? 1 : 0;
    }
}