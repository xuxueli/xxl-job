package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.entity.JobInfo;
import com.xxl.job.admin.common.pojo.query.JobInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 任务信息 Mapper 接口
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface JobInfoMapper extends BaseMapper<JobInfo> {

    /**
     * 查询任务根据分组id
     *
     * @param groupId 组id
     * @return {@link List}<{@link JobInfo}>
     */
    List<JobInfo> queryJobByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询所有数
     *
     * @return {@link Long}
     */
    Long findAllCount();

    /**
     * 根据下次调度时间查询任务
     *
     * @param maxNextTime 下次调度时间
     * @param pageSize 数量
     * @return {@link List}<{@link JobInfo}>
     */
    List<JobInfo> queryJobInfoByTriggerNextTime(@Param("maxNextTime") Long maxNextTime, @Param("pageSize") Integer pageSize);

    /**
     * 根据ID修改调度时间和状态
     *
     * @param id              ID
     * @param triggerLastTime 上次触发
     * @param triggerNextTime 下次触发
     * @param triggerStatus   触发状态
     * @param updatedTime 修改时间
     */
    void updateTriggerTimeById(@Param("id") Long id, @Param("triggerLastTime") Long triggerLastTime,
                               @Param("triggerNextTime") Long triggerNextTime,
                               @Param("triggerStatus") Integer triggerStatus,
                               @Param("updatedTime") Long updatedTime);

    /**
     * 查询任务信息根据名字
     *
     * @param name 名字
     * @return {@link JobInfo}
     */
    JobInfo queryJobInfoByName(@Param("name") String name);

    /**
     * 查询任务信息
     *
     * @param query 查询对象
     * @return {@link List}<{@link JobInfo}>
     */
    List<JobInfo> queryJobInfo(JobInfoQuery query);

    /**
     * 更新状态根据id
     *
     * @param id     id
     * @param status 调度状态：0-停止，1-运行
     */
    void updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新GLUE根据id
     *
     * @param id              id
     * @param glueSource      GLUE源代码
     * @param glueType        glue 类型
     * @param glueDescription GLUE描述
     * @param glueUpdatedTime GLUE更新时间
     * @param updatedTime     更新时间
     */
    void updateGlueById(@Param("id") Long id, @Param("glueType") String glueType,
                        @Param("glueSource") String glueSource, @Param("glueDescription") String glueDescription,
                        @Param("glueUpdatedTime") Long glueUpdatedTime, @Param("updatedTime") Long updatedTime);















}
