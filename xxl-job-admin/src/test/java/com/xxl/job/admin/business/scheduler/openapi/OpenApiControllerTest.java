package com.xxl.job.admin.business.scheduler.openapi;

import com.xxl.job.admin.business.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.business.model.XxlJobGroup;
import com.xxl.job.admin.business.model.XxlJobInfo;
import com.xxl.job.admin.business.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.business.scheduler.thread.JobRegistryHelper;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.openapi.admin.AdminBiz;
import com.xxl.job.core.openapi.admin.AdminJobBiz;
import com.xxl.job.core.openapi.admin.dto.JobOperateRequest;
import com.xxl.job.core.openapi.admin.dto.RegistryRequest;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class OpenApiControllerTest {

    private AdminJobBiz adminJobBiz;
    private AdminBiz adminBiz;
    private XxlJobInfoMapper jobInfoMapper;
    private OpenApiController controller;
    private MockedStatic<XxlJobAdminBootstrap> bootstrapMock;

    @BeforeEach
    public void setUp() {
        adminJobBiz = mock(AdminJobBiz.class);
        adminBiz = mock(AdminBiz.class);
        jobInfoMapper = mock(XxlJobInfoMapper.class);
        controller = new OpenApiController();
        ReflectionTestUtils.setField(controller, "adminBiz", adminBiz);
        ReflectionTestUtils.setField(controller, "adminJobBiz", adminJobBiz);
        Field mapperField = ReflectionUtils.findField(OpenApiController.class, "xxlJobInfoMapper");
        if (mapperField != null) {
            ReflectionTestUtils.setField(controller, "xxlJobInfoMapper", jobInfoMapper);
        }

        XxlJobGroup authenticatedGroup = new XxlJobGroup();
        authenticatedGroup.setId(1);
        authenticatedGroup.setAppname("group-one");
        authenticatedGroup.setAccessToken("token-one");
        JobRegistryHelper registryHelper = mock(JobRegistryHelper.class);
        when(registryHelper.loadByAppName("group-one")).thenReturn(authenticatedGroup);
        XxlJobAdminBootstrap bootstrap = mock(XxlJobAdminBootstrap.class);
        when(bootstrap.getJobRegistryHelper()).thenReturn(registryHelper);
        bootstrapMock = mockStatic(XxlJobAdminBootstrap.class);
        bootstrapMock.when(XxlJobAdminBootstrap::getInstance).thenReturn(bootstrap);
    }

    @AfterEach
    public void tearDown() {
        bootstrapMock.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"updateJob", "removeJob", "startJob", "stopJob", "triggerJob"})
    public void jobOperationsRejectAnotherGroup(String uri) {
        XxlJobInfo target = new XxlJobInfo();
        target.setId(42);
        target.setJobGroup(2);
        when(jobInfoMapper.loadById(42)).thenReturn(target);

        Response<?> response = (Response<?>) controller.api(request("{\"id\":42}"), uri, "token-one", "group-one", "{\"id\":42}");

        assertFalse(response.isSuccess());
        verifyNoInteractions(adminJobBiz);
    }

    @ParameterizedTest
    @ValueSource(strings = {"updateJob", "removeJob", "startJob", "stopJob", "triggerJob"})
    public void jobOperationsRejectMissingJob(String uri) {
        when(jobInfoMapper.loadById(42)).thenReturn(null);

        Response<?> response = (Response<?>) controller.api(request("{\"id\":42}"), uri, "token-one", "group-one", "{\"id\":42}");

        assertFalse(response.isSuccess());
        verifyNoInteractions(adminJobBiz);
    }

    @Test
    public void addRejectsAnotherGroup() {
        Response<?> response = (Response<?>) controller.api(request("{\"jobGroup\":2}"), "addJob", "token-one", "group-one", "{\"jobGroup\":2}");

        assertFalse(response.isSuccess());
        verifyNoInteractions(adminJobBiz);
    }

    @Test
    public void addAllowsAuthenticatedGroup() {
        when(adminJobBiz.addJob(any())).thenReturn(Response.ofSuccess("42"));

        Response<?> response = (Response<?>) controller.api(request("{\"jobGroup\":1}"), "addJob", "token-one", "group-one", "{\"jobGroup\":1}");

        assertTrue(response.isSuccess());
        verify(adminJobBiz).addJob(any());
    }

    @Test
    public void removeAllowsJobFromAuthenticatedGroup() {
        XxlJobInfo target = new XxlJobInfo();
        target.setId(42);
        target.setJobGroup(1);
        when(jobInfoMapper.loadById(42)).thenReturn(target);
        when(adminJobBiz.removeJob(any())).thenReturn(Response.ofSuccess());

        Response<?> response = (Response<?>) controller.api(request("{\"id\":42}"), "removeJob", "token-one", "group-one", "{\"id\":42}");

        assertTrue(response.isSuccess());
        verify(adminJobBiz).removeJob(any(JobOperateRequest.class));
    }

    @Test
    public void invalidTokenCannotReachJobOperations() {
        Response<?> response = (Response<?>) controller.api(request("{\"id\":42}"), "removeJob", "wrong-token", "group-one", "{\"id\":42}");

        assertFalse(response.isSuccess());
        verifyNoInteractions(adminJobBiz, jobInfoMapper);
    }

    @ParameterizedTest
    @ValueSource(strings = {"registry", "registryRemove"})
    public void registryOperationsRejectAnotherGroup(String uri) {
        String body = "{\"registryGroup\":\"EXECUTOR\",\"registryKey\":\"group-two\",\"registryValue\":\"http://127.0.0.1:9999/\"}";
        if (uri.equals("registry")) {
            when(adminBiz.registry(any())).thenReturn(Response.ofSuccess());
        } else {
            when(adminBiz.registryRemove(any())).thenReturn(Response.ofSuccess());
        }

        Response<?> response = (Response<?>) controller.api(request(body), uri, "token-one", "group-one", body);

        assertFalse(response.isSuccess());
        verifyNoInteractions(adminBiz, adminJobBiz, jobInfoMapper);
    }

    @ParameterizedTest
    @ValueSource(strings = {"registry", "registryRemove"})
    public void registryOperationsAllowAuthenticatedGroup(String uri) {
        String body = "{\"registryGroup\":\"EXECUTOR\",\"registryKey\":\"group-one\",\"registryValue\":\"http://127.0.0.1:9999/\"}";
        if (uri.equals("registry")) {
            when(adminBiz.registry(any())).thenReturn(Response.ofSuccess());
        } else {
            when(adminBiz.registryRemove(any())).thenReturn(Response.ofSuccess());
        }

        Response<?> response = (Response<?>) controller.api(request(body), uri, "token-one", "group-one", body);

        assertTrue(response.isSuccess());
        if (uri.equals("registry")) {
            verify(adminBiz).registry(any(RegistryRequest.class));
        } else {
            verify(adminBiz).registryRemove(any(RegistryRequest.class));
        }
        verifyNoInteractions(adminJobBiz, jobInfoMapper);
    }

    private static MockHttpServletRequest request(String body) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(Const.XXL_JOB_ACCESS_TOKEN, "token-one");
        request.addHeader(Const.XXL_JOB_APPNAME, "group-one");
        request.setContent(body.getBytes());
        return request;
    }
}
