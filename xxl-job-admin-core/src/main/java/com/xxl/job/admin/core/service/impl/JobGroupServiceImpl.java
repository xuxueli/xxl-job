package com.xxl.job.admin.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.admin.core.service.JobRegistryService;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.constant.RegistType;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.PageModel;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.*;

@Service
public class JobGroupServiceImpl extends ServiceImpl<XxlJobGroupMapper, XxlJobGroup> implements JobGroupService {
    @Resource
    private JobRegistryService jobRegistryService;

    @Resource
    private JobInfoService jobInfoService;

    @Override
    public List<XxlJobGroup> findAll() {
        /** 原XxlJobGroupMapper
            <select id="findAll" resultMap="XxlJobGroup">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_group AS t
                ORDER BY t.app_name, t.title, t.id ASC
            </select>
        */
        return this.list(
            new QueryWrapper<XxlJobGroup>()
            .orderByAsc("app_name", "title", "id")
        );
    }

    
    @Override
    public List<XxlJobGroup> findByAddressType(int address_type) {
        /** 原XxlJobGroupMapper
            <select id="findByAddressType" parameterType="java.lang.Integer" resultMap="XxlJobGroup">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_group AS t
                WHERE t.address_type = #{addressType}
                ORDER BY t.app_name, t.title, t.id ASC
            </select>
        */
        return this.list(
            new QueryWrapper<XxlJobGroup>()
                .eq("address_type", address_type)
                .orderByAsc("app_name", "title", "id")
        );
    }

    @Override
    public XxlJobGroup load(int id) {
        /** 原XxlJobGroupMapper
            <select id="load" parameterType="java.lang.Integer" resultMap="XxlJobGroup">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_group AS t
                WHERE t.id = #{id}
            </select>
        */
        return this.getById(id);
    }

    @Override
    public int add(XxlJobGroup jobGroup) {
        // valid
        if (StringTool.isBlank(jobGroup.getAppname())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + "AppName");
        }
        if (jobGroup.getAppname().length() < 4 || jobGroup.getAppname().length() > 64) {
            throw new XxlException(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (jobGroup.getAppname().contains(">") || jobGroup.getAppname().contains("<")) {
            throw new XxlException("AppName" + I18nUtil.getString("system_invalid"));
        }
        if (StringTool.isBlank(jobGroup.getTitle())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
        }
        if (jobGroup.getTitle().contains(">") || jobGroup.getTitle().contains("<")) {
            throw new XxlException(I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_invalid"));
        }
        if (jobGroup.getAddressType() != 0) {
            if (StringTool.isBlank(jobGroup.getAddressList())) {
                throw new XxlException(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            if (jobGroup.getAddressList().contains(">") || jobGroup.getAddressList().contains("<")) {
                throw new XxlException(I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_invalid"));
            }

            String[] addresss = jobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringTool.isBlank(item)) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid"));
                }
                if (!(HttpTool.isHttp(item) || HttpTool.isHttps(item))) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid") + "[2]");
                }
            }
        }

        // process
        jobGroup.setUpdateTime(new Date());

        /** 原XxlJobGroupMapper
            <insert id="save" parameterType="com.xxl.job.admin.model.XxlJobGroup" useGeneratedKeys="true" keyProperty="id" >
                INSERT INTO xxl_job_group ( `app_name`, `title`, `address_type`, `address_list`, `update_time`)
                values ( #{appname}, #{title}, #{addressType}, #{addressList}, #{updateTime} );
            </insert>
         */

        return this.save(jobGroup) ? 1 : 0;
    }

    @Override
    public int update(XxlJobGroup jobGroup) {
        // valid
        if (StringTool.isBlank(jobGroup.getAppname())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + "AppName");
        }
        if (jobGroup.getAppname().length() < 4 || jobGroup.getAppname().length() > 64) {
            throw new XxlException(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (StringTool.isBlank(jobGroup.getTitle())) {
            throw new XxlException(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
        }
        if (jobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(jobGroup.getAppname());
            String addressListStr = null;
            if (CollectionTool.isNotEmpty(registryList)) {
                Collections.sort(registryList);
                addressListStr = String.join(",", registryList);
            }
            jobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (StringTool.isBlank(jobGroup.getAddressList())) {
                throw new XxlException(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = jobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringTool.isBlank(item)) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid"));
                }
                if (!(HttpTool.isHttp(item) || HttpTool.isHttps(item))) {
                    throw new XxlException(I18nUtil.getString("jobgroup_field_registryList_invalid") + "[2]");
                }
            }
        }

        // process
        jobGroup.setUpdateTime(new Date());

        /** 原XxlJobGroupMapper
            <update id="update" parameterType="com.xxl.job.admin.model.XxlJobGroup" >
                UPDATE xxl_job_group
                SET `app_name` = #{appname},
                    `title` = #{title},
                    `address_type` = #{addressType},
                    `address_list` = #{addressList},
                    `update_time` = #{updateTime}
                WHERE id = #{id}
            </update>
         */

        return this.updateById(jobGroup) ? 1 : 0;
    }

    @Override
    public int remove(List<Integer> ids) {
        // parse id
		if (CollectionTool.isEmpty(ids) || ids.size() != 1) {
			throw new XxlException(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
		}
		int id = ids.get(0);
        // parse id
        XxlJobGroup xxlJobGroup = this.getById(id);
        if (xxlJobGroup == null) {
            return 1;
        }

        // whether exists job
        int count = jobInfoService.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            throw new XxlException(I18nUtil.getString("jobgroup_del_limit_0"));
        }

        // whether only exists one group
        List<XxlJobGroup> allList = this.findAll();
        if (allList.size() == 1) {
            throw new XxlException(I18nUtil.getString("jobgroup_del_limit_1"));
        }

        /** 原XxlJobGroupMapper
            <delete id="remove" parameterType="java.lang.Integer" >
                DELETE FROM xxl_job_group
                WHERE id = #{id}
            </delete>
         */

        // remove group
        int ret = this.removeById(id) ? 1 : 0;
        // remove registry-data
        removeByRegistryByKey(RegistType.EXECUTOR.name(), xxlJobGroup.getAppname());
        return ret;
    }

    @Override
    public PageModel<XxlJobGroup> pageList(int page, int pagesize, String appName, String title) {
        /** 原XxlJobGroupMapper
         * 
        <select id="pageList" parameterType="java.util.HashMap" resultMap="XxlJobGroup">
            SELECT <include refid="Base_Column_List" />
            FROM xxl_job_group AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="appname != null and appname != ''">
                    AND t.app_name like CONCAT(CONCAT('%', #{appname}), '%')
                </if>
                <if test="title != null and title != ''">
                    AND t.title like CONCAT(CONCAT('%', #{title}), '%')
                </if>
            </trim>
            ORDER BY t.app_name, t.title, t.id ASC
            LIMIT #{offset}, #{pagesize}
        </select>

        <select id="pageListCount" parameterType="java.util.HashMap" resultType="int">
            SELECT count(1)
            FROM xxl_job_group AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="appname != null and appname != ''">
                    AND t.app_name like CONCAT(CONCAT('%', #{appname}), '%')
                </if>
                <if test="title != null and title != ''">
                    AND t.title like CONCAT(CONCAT('%', #{title}), '%')
                </if>
            </trim>
        </select>
         */
        
        Page<XxlJobGroup> p = new Page<XxlJobGroup>(page, pagesize);

        p.addOrder(
                new OrderItem().setColumn("app_name").setAsc(true),
                new OrderItem().setColumn("title").setAsc(true),
                new OrderItem().setColumn("id").setAsc(true));

        QueryWrapper<XxlJobGroup> qw = new QueryWrapper<>();
        if (StringTool.isNotEmpty(appName))
            qw = qw.like("app_name", appName);
        if (StringTool.isNotEmpty(title))
            qw = qw.like("title", title);

        IPage<XxlJobGroup> iPage = this.page(p, qw);

        // package result
        PageModel<XxlJobGroup> pageModel = new PageModel<>();
        pageModel.setData(iPage.getRecords());
        pageModel.setTotal(Math.toIntExact(iPage.getTotal()));

        return pageModel;
    }

    @Override
    public List<String> findRegistryByAppName(String appNameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<>();
        List<XxlJobRegistry> list = jobRegistryService.findAll(Const.DEAD_TIMEOUT, new Date());
        if (CollectionTool.isNotEmpty(list)) {
            for (XxlJobRegistry item: list) {
                if (!RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    continue;
                }

                String appname = item.getRegistryKey();
                List<String> registryList = appAddressMap.computeIfAbsent(appname, k -> new ArrayList<>());

                if (!registryList.contains(item.getRegistryValue())) {
                    registryList.add(item.getRegistryValue());
                }
            }
        }
        return appAddressMap.get(appNameParam);
    }

    @Override
    public void removeByRegistryByKey(String registryGroup, String registryKey) {
        jobRegistryService.removeByRegistryGroupAndKey(registryGroup, registryKey);
    }
}