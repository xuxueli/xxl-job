package com.xxl.job.admin.business.service.impl;

import com.xxl.job.admin.business.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.business.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.business.model.XxlJobGroup;
import com.xxl.job.admin.business.model.XxlJobInfo;
import com.xxl.job.admin.framework.constant.Consts;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XxlJobServiceImplTest {

    private XxlJobInfoMapper jobInfoMapper;
    private XxlJobServiceImpl service;

    @BeforeEach
    public void setUp() {
        jobInfoMapper = mock(XxlJobInfoMapper.class);
        XxlJobGroupMapper jobGroupMapper = mock(XxlJobGroupMapper.class);
        when(jobGroupMapper.load(1)).thenReturn(new XxlJobGroup());
        when(jobGroupMapper.load(2)).thenReturn(new XxlJobGroup());

        service = new XxlJobServiceImpl();
        ReflectionTestUtils.setField(service, "xxlJobInfoMapper", jobInfoMapper);
        ReflectionTestUtils.setField(service, "xxlJobGroupMapper", jobGroupMapper);
    }

    @Test
    public void updateRejectsJobFromUnauthorizedCurrentGroup() {
        XxlJobInfo existing = existingJob(2);
        when(jobInfoMapper.loadById(42)).thenReturn(existing);

        Response<String> response = service.update(updateRequest(1), userWithJobGroups("1"));

        assertFalse(response.isSuccess());
        verify(jobInfoMapper, never()).update(any());
    }

    @Test
    public void updateRejectsUnauthorizedTargetGroup() {
        XxlJobInfo existing = existingJob(1);
        when(jobInfoMapper.loadById(42)).thenReturn(existing);

        Response<String> response = service.update(updateRequest(2), userWithJobGroups("1"));

        assertFalse(response.isSuccess());
        verify(jobInfoMapper, never()).update(any());
    }

    @Test
    public void updateAllowsAuthorizedCurrentGroup() {
        XxlJobInfo existing = existingJob(1);
        when(jobInfoMapper.loadById(42)).thenReturn(existing);

        Response<String> response = service.update(updateRequest(1), userWithJobGroups("1"));

        assertTrue(response.isSuccess());
        verify(jobInfoMapper).update(existing);
    }

    @Test
    public void updateRejectsMissingJob() {
        when(jobInfoMapper.loadById(42)).thenReturn(null);

        Response<String> response = service.update(updateRequest(1), userWithJobGroups("1"));

        assertFalse(response.isSuccess());
        verify(jobInfoMapper, never()).update(any());
    }

    @Test
    public void updateAllowsAdministrator() {
        XxlJobInfo existing = existingJob(2);
        when(jobInfoMapper.loadById(42)).thenReturn(existing);
        LoginInfo admin = new LoginInfo("1", "admin", "Admin", null, List.of(Consts.ADMIN_ROLE), List.of(), -1L, null);

        Response<String> response = service.update(updateRequest(1), admin);

        assertTrue(response.isSuccess());
        verify(jobInfoMapper).update(existing);
    }

    private static LoginInfo userWithJobGroups(String jobGroups) {
        return new LoginInfo("2", "user", "User", Map.of("jobGroups", jobGroups), List.of(), List.of(), -1L, null);
    }

    private static XxlJobInfo existingJob(int jobGroup) {
        XxlJobInfo job = new XxlJobInfo();
        job.setId(42);
        job.setJobGroup(jobGroup);
        job.setScheduleType("NONE");
        job.setScheduleConf("");
        return job;
    }

    private static XxlJobInfo updateRequest(int jobGroup) {
        XxlJobInfo job = new XxlJobInfo();
        job.setId(42);
        job.setJobGroup(jobGroup);
        job.setName("updated job");
        job.setAuthor("user");
        job.setScheduleType("NONE");
        job.setScheduleConf("");
        job.setMisfireStrategy("DO_NOTHING");
        job.setExecutorRouteStrategy("FIRST");
        job.setExecutorHandler("");
        job.setExecutorParam("");
        job.setExecutorBlockStrategy("SERIAL_EXECUTION");
        job.setExecutorTimeout(0);
        job.setExecutorFailRetryCount(0);
        return job;
    }
}
