package com.xxl.job.core.biz.impl;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.impl.ScriptJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.JobThread;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
public class ExecutorBizImplTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10000);

  /* testedClasses: ExecutorBizImpl */
  // Test written by Diffblue Cover.
  @Test
  public void beatOutputNotNull() {

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();

    // Act
    final ReturnT<String> retval = objectUnderTest.beat();

    // Assert result
    Assert.assertNotNull(retval);
    Assert.assertNull(((ReturnT<String>)retval).getContent());
    Assert.assertEquals(200, retval.getCode());
    Assert.assertNull(retval.getMsg());
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorOutputVoid() {

    // Act, creating object to test constructor
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();

    // Method returns void, testing that no exception is thrown
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({ExecutorBizImpl.class, ConcurrentHashMap.class, XxlJobExecutor.class})
  @Test
  public void idleBeatInputZeroOutputNotNull() throws Exception, InvocationTargetException {

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final int jobId = 0;
    final ConcurrentHashMap concurrentHashMap1 = PowerMockito.mock(ConcurrentHashMap.class);
    final JobThread jobThread =
        (JobThread)Reflector.getInstance("com.xxl.job.core.thread.JobThread");
    Reflector.setField(jobThread, "stopReason", null);
    Reflector.setField(jobThread, "running", true);
    Reflector.setField(jobThread, "jobId", 0);
    Reflector.setField(jobThread, "idleTimes", 0);
    Reflector.setField(jobThread, "toStop", false);
    Reflector.setField(jobThread, "triggerQueue", null);
    Reflector.setField(jobThread, "handler", null);
    Reflector.setField(jobThread, "triggerLogIdSet", null);
    final Method getMethod = DTUMemberMatcher.method(ConcurrentHashMap.class, "get", Object.class);
    PowerMockito.doReturn(jobThread)
        .when(concurrentHashMap1, getMethod)
        .withArguments(or(isA(Object.class), isNull(Object.class)));
    final ConcurrentHashMap concurrentHashMap = PowerMockito.mock(ConcurrentHashMap.class);
    PowerMockito.whenNew(ConcurrentHashMap.class)
        .withNoArguments()
        .thenReturn(concurrentHashMap)
        .thenReturn(concurrentHashMap1);

    // Act
    final ReturnT<String> retval = objectUnderTest.idleBeat(jobId);

    // Assert result
    Assert.assertNotNull(retval);
    Assert.assertNull(((ReturnT<String>)retval).getContent());
    Assert.assertEquals(500, retval.getCode());
    Assert.assertEquals("job thread is running or has trigger queue.", retval.getMsg());
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({LinkedBlockingQueue.class, ExecutorBizImpl.class, ConcurrentHashMap.class,
                   XxlJobExecutor.class})
  @Test
  public void
  idleBeatInputZeroOutputNotNull2() throws Exception, InvocationTargetException {

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final int jobId = 0;
    final ConcurrentHashMap concurrentHashMap1 = PowerMockito.mock(ConcurrentHashMap.class);
    final JobThread jobThread =
        (JobThread)Reflector.getInstance("com.xxl.job.core.thread.JobThread");
    Reflector.setField(jobThread, "stopReason", null);
    Reflector.setField(jobThread, "running", false);
    Reflector.setField(jobThread, "jobId", 0);
    Reflector.setField(jobThread, "idleTimes", 0);
    Reflector.setField(jobThread, "toStop", false);
    final LinkedBlockingQueue linkedBlockingQueue = PowerMockito.mock(LinkedBlockingQueue.class);
    final Method sizeMethod = DTUMemberMatcher.method(LinkedBlockingQueue.class, "size");
    PowerMockito.doReturn(0).when(linkedBlockingQueue, sizeMethod).withNoArguments();
    Reflector.setField(jobThread, "triggerQueue", linkedBlockingQueue);
    Reflector.setField(jobThread, "handler", null);
    Reflector.setField(jobThread, "triggerLogIdSet", null);
    final Method getMethod = DTUMemberMatcher.method(ConcurrentHashMap.class, "get", Object.class);
    PowerMockito.doReturn(jobThread)
        .when(concurrentHashMap1, getMethod)
        .withArguments(or(isA(Object.class), isNull(Object.class)));
    final ConcurrentHashMap concurrentHashMap = PowerMockito.mock(ConcurrentHashMap.class);
    PowerMockito.whenNew(ConcurrentHashMap.class)
        .withNoArguments()
        .thenReturn(concurrentHashMap)
        .thenReturn(concurrentHashMap1);

    // Act
    final ReturnT<String> retval = objectUnderTest.idleBeat(jobId);

    // Assert result
    Assert.assertNotNull(retval);
    Assert.assertNull(((ReturnT<String>)retval).getContent());
    Assert.assertEquals(200, retval.getCode());
    Assert.assertNull(retval.getMsg());
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({ExecutorBizImpl.class, ConcurrentHashMap.class, XxlJobExecutor.class})
  @Test
  public void idleBeatInputZeroOutputNotNull3() throws Exception, InvocationTargetException {

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final int jobId = 0;
    final ConcurrentHashMap concurrentHashMap1 = PowerMockito.mock(ConcurrentHashMap.class);
    final Method getMethod = DTUMemberMatcher.method(ConcurrentHashMap.class, "get", Object.class);
    PowerMockito.doReturn(null)
        .when(concurrentHashMap1, getMethod)
        .withArguments(or(isA(Object.class), isNull(Object.class)));
    final ConcurrentHashMap concurrentHashMap = PowerMockito.mock(ConcurrentHashMap.class);
    PowerMockito.whenNew(ConcurrentHashMap.class)
        .withNoArguments()
        .thenReturn(concurrentHashMap)
        .thenReturn(concurrentHashMap1);

    // Act
    final ReturnT<String> retval = objectUnderTest.idleBeat(jobId);

    // Assert result
    Assert.assertNotNull(retval);
    Assert.assertNull(((ReturnT<String>)retval).getContent());
    Assert.assertEquals(200, retval.getCode());
    Assert.assertNull(retval.getMsg());
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({ExecutorBizImpl.class, ConcurrentHashMap.class, XxlJobExecutor.class})
  @Test
  public void killInputZeroOutputNotNull() throws Exception, InvocationTargetException {

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final int jobId = 0;
    final ConcurrentHashMap concurrentHashMap1 = PowerMockito.mock(ConcurrentHashMap.class);
    final JobThread jobThread =
        (JobThread)Reflector.getInstance("com.xxl.job.core.thread.JobThread");
    Reflector.setField(jobThread, "stopReason", null);
    Reflector.setField(jobThread, "running", false);
    Reflector.setField(jobThread, "jobId", 0);
    Reflector.setField(jobThread, "idleTimes", 0);
    Reflector.setField(jobThread, "toStop", false);
    Reflector.setField(jobThread, "triggerQueue", null);
    Reflector.setField(jobThread, "handler", null);
    Reflector.setField(jobThread, "triggerLogIdSet", null);
    final Method getMethod = DTUMemberMatcher.method(ConcurrentHashMap.class, "get", Object.class);
    PowerMockito.doReturn(jobThread)
        .when(concurrentHashMap1, getMethod)
        .withArguments(or(isA(Object.class), isNull(Object.class)));
    final Method removeMethod =
        DTUMemberMatcher.method(ConcurrentHashMap.class, "remove", Object.class);
    PowerMockito.doReturn(null)
        .when(concurrentHashMap1, removeMethod)
        .withArguments(or(isA(Object.class), isNull(Object.class)));
    final ConcurrentHashMap concurrentHashMap = PowerMockito.mock(ConcurrentHashMap.class);
    PowerMockito.whenNew(ConcurrentHashMap.class)
        .withNoArguments()
        .thenReturn(concurrentHashMap)
        .thenReturn(concurrentHashMap1);

    // Act
    final ReturnT<String> retval = objectUnderTest.kill(jobId);

    // Assert result
    Assert.assertNotNull(retval);
    Assert.assertNull(((ReturnT<String>)retval).getContent());
    Assert.assertEquals(200, retval.getCode());
    Assert.assertNull(retval.getMsg());
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({ExecutorBizImpl.class, ConcurrentHashMap.class, XxlJobExecutor.class})
  @Test
  public void killInputZeroOutputNotNull2() throws Exception, InvocationTargetException {

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final int jobId = 0;
    final ConcurrentHashMap concurrentHashMap1 = PowerMockito.mock(ConcurrentHashMap.class);
    final Method getMethod = DTUMemberMatcher.method(ConcurrentHashMap.class, "get", Object.class);
    PowerMockito.doReturn(null)
        .when(concurrentHashMap1, getMethod)
        .withArguments(or(isA(Object.class), isNull(Object.class)));
    final ConcurrentHashMap concurrentHashMap = PowerMockito.mock(ConcurrentHashMap.class);
    PowerMockito.whenNew(ConcurrentHashMap.class)
        .withNoArguments()
        .thenReturn(concurrentHashMap)
        .thenReturn(concurrentHashMap1);

    // Act
    final ReturnT<String> retval = objectUnderTest.kill(jobId);

    // Assert result
    Assert.assertNotNull(retval);
    Assert.assertNull(((ReturnT<String>)retval).getContent());
    Assert.assertEquals(200, retval.getCode());
    Assert.assertEquals("job thread aleady killed.", retval.getMsg());
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({XxlJobFileAppender.class, ExecutorBizImpl.class})
  @Test
  public void logInputZeroZeroZeroOutputNotNull() throws Exception, InvocationTargetException {

    // Setup mocks
    PowerMockito.mockStatic(XxlJobFileAppender.class);

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final long logDateTim = 0L;
    final int logId = 0;
    final int fromLineNum = 0;
    final ReturnT returnT = PowerMockito.mock(ReturnT.class);
    PowerMockito.whenNew(ReturnT.class)
        .withParameterTypes(Object.class)
        .withArguments(or(isA(Object.class), isNull(Object.class)))
        .thenReturn(returnT);
    final Date date = PowerMockito.mock(Date.class);
    Reflector.setField(date, "fastTime", 1_515_585_600_000L);
    PowerMockito.whenNew(Date.class)
        .withParameterTypes(long.class)
        .withArguments(anyLong())
        .thenReturn(date);
    final LogResult logResult =
        (LogResult)Reflector.getInstance("com.xxl.job.core.biz.model.LogResult");
    final Method readLogMethod =
        DTUMemberMatcher.method(XxlJobFileAppender.class, "readLog", String.class, int.class);
    PowerMockito.doReturn(logResult)
        .when(XxlJobFileAppender.class, readLogMethod)
        .withArguments(or(isA(String.class), isNull(String.class)), anyInt());
    final Method makeLogFileNameMethod =
        DTUMemberMatcher.method(XxlJobFileAppender.class, "makeLogFileName", Date.class, int.class);
    PowerMockito.doReturn("?")
        .when(XxlJobFileAppender.class, makeLogFileNameMethod)
        .withArguments(or(isA(Date.class), isNull(Date.class)), anyInt());

    // Act
    final ReturnT<LogResult> retval = objectUnderTest.log(logDateTim, logId, fromLineNum);

    // Assert result
    Assert.assertNotNull(retval);
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({XxlJobExecutor.class, GlueTypeEnum.class, JobThread.class, ExecutorBizImpl.class,
                   TriggerParam.class})
  @Test
  public void
  runInputNotNullOutputNotNull() throws Exception, InvocationTargetException {

    // Setup mocks
    PowerMockito.mockStatic(XxlJobExecutor.class);
    PowerMockito.mockStatic(GlueTypeEnum.class);

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final TriggerParam triggerParam = PowerMockito.mock(TriggerParam.class);
    final Method getJobIdMethod = DTUMemberMatcher.method(TriggerParam.class, "getJobId");
    PowerMockito.doReturn(0).when(triggerParam, getJobIdMethod).withNoArguments();
    final Method getGlueTypeMethod = DTUMemberMatcher.method(TriggerParam.class, "getGlueType");
    ((PowerMockitoStubber)PowerMockito.doReturn("?").doReturn("?"))
        .when(triggerParam, getGlueTypeMethod)
        .withNoArguments();
    final ReturnT returnT = PowerMockito.mock(ReturnT.class);
    PowerMockito.whenNew(ReturnT.class)
        .withParameterTypes(int.class, String.class)
        .withArguments(anyInt(), or(isA(String.class), isNull(String.class)))
        .thenReturn(returnT);
    final GlueTypeEnum glueTypeEnum = PowerMockito.mock(GlueTypeEnum.class);
    final Method isScriptMethod = DTUMemberMatcher.method(GlueTypeEnum.class, "isScript");
    PowerMockito.doReturn(false).when(glueTypeEnum, isScriptMethod).withNoArguments();
    final Method matchMethod = DTUMemberMatcher.method(GlueTypeEnum.class, "match", String.class);
    PowerMockito.doReturn(glueTypeEnum)
        .when(GlueTypeEnum.class, matchMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final JobThread jobThread = PowerMockito.mock(JobThread.class);
    final IJobHandler iJobHandler =
        (IJobHandler)Reflector.getInstance("com.xxl.job.core.handler.IJobHandler");
    final Method getHandlerMethod = DTUMemberMatcher.method(JobThread.class, "getHandler");
    PowerMockito.doReturn(iJobHandler).when(jobThread, getHandlerMethod).withNoArguments();
    final Method loadJobThreadMethod =
        DTUMemberMatcher.method(XxlJobExecutor.class, "loadJobThread", int.class);
    PowerMockito.doReturn(jobThread)
        .when(XxlJobExecutor.class, loadJobThreadMethod)
        .withArguments(anyInt());

    // Act
    final ReturnT<String> retval = objectUnderTest.run(triggerParam);

    // Assert result
    Assert.assertNotNull(retval);
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({ExecutorBizImpl.class, XxlJobExecutor.class, TriggerParam.class, JobThread.class,
                   GlueTypeEnum.class})
  @Test
  public void
  runInputNotNullOutputNotNull2() throws Exception, InvocationTargetException {

    // Setup mocks
    PowerMockito.mockStatic(GlueTypeEnum.class);
    PowerMockito.mockStatic(XxlJobExecutor.class);

    // Arrange
    final ExecutorBizImpl objectUnderTest = new ExecutorBizImpl();
    final TriggerParam triggerParam = PowerMockito.mock(TriggerParam.class);
    final Method getJobIdMethod = DTUMemberMatcher.method(TriggerParam.class, "getJobId");
    ((PowerMockitoStubber)PowerMockito.doReturn(0).doReturn(0).doReturn(0))
        .when(triggerParam, getJobIdMethod)
        .withNoArguments();
    final Method getGlueTypeMethod = DTUMemberMatcher.method(TriggerParam.class, "getGlueType");
    ((PowerMockitoStubber)PowerMockito.doReturn("?").doReturn("?"))
        .when(triggerParam, getGlueTypeMethod)
        .withNoArguments();
    final Method getGlueUpdatetimeMethod =
        DTUMemberMatcher.method(TriggerParam.class, "getGlueUpdatetime");
    PowerMockito.doReturn(0L).when(triggerParam, getGlueUpdatetimeMethod).withNoArguments();
    final Method getGlueSourceMethod = DTUMemberMatcher.method(TriggerParam.class, "getGlueSource");
    PowerMockito.doReturn("?").when(triggerParam, getGlueSourceMethod).withNoArguments();
    final ScriptJobHandler scriptJobHandler = PowerMockito.mock(ScriptJobHandler.class);
    PowerMockito.whenNew(ScriptJobHandler.class)
        .withParameterTypes(int.class, long.class, String.class, GlueTypeEnum.class)
        .withArguments(anyInt(), anyLong(), or(isA(String.class), isNull(String.class)),
                       or(isA(GlueTypeEnum.class), isNull(GlueTypeEnum.class)))
        .thenReturn(scriptJobHandler);
    final JobThread jobThread = PowerMockito.mock(JobThread.class);
    final ReturnT returnT = (ReturnT)Reflector.getInstance("com.xxl.job.core.biz.model.ReturnT");
    final Method pushTriggerQueueMethod =
        DTUMemberMatcher.method(JobThread.class, "pushTriggerQueue", TriggerParam.class);
    PowerMockito.doReturn(returnT)
        .when(jobThread, pushTriggerQueueMethod)
        .withArguments(or(isA(TriggerParam.class), isNull(TriggerParam.class)));
    final Method registJobThreadMethod = DTUMemberMatcher.method(
        XxlJobExecutor.class, "registJobThread", int.class, IJobHandler.class, String.class);
    PowerMockito.doReturn(jobThread)
        .when(XxlJobExecutor.class, registJobThreadMethod)
        .withArguments(anyInt(), or(isA(IJobHandler.class), isNull(IJobHandler.class)),
                       or(isA(String.class), isNull(String.class)));
    final GlueTypeEnum glueTypeEnum1 =
        (GlueTypeEnum)Reflector.getInstance("com.xxl.job.core.glue.GlueTypeEnum");
    final GlueTypeEnum glueTypeEnum = PowerMockito.mock(GlueTypeEnum.class);
    final Method isScriptMethod = DTUMemberMatcher.method(GlueTypeEnum.class, "isScript");
    PowerMockito.doReturn(true).when(glueTypeEnum, isScriptMethod).withNoArguments();
    final Method matchMethod = DTUMemberMatcher.method(GlueTypeEnum.class, "match", String.class);
    ((PowerMockitoStubber)PowerMockito.doReturn(glueTypeEnum).doReturn(glueTypeEnum1))
        .when(GlueTypeEnum.class, matchMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final JobThread jobThread1 = PowerMockito.mock(JobThread.class);
    final IJobHandler iJobHandler1 =
        (IJobHandler)Reflector.getInstance("com.xxl.job.core.handler.IJobHandler");
    final IJobHandler iJobHandler =
        (IJobHandler)Reflector.getInstance("com.xxl.job.core.handler.IJobHandler");
    final Method getHandlerMethod = DTUMemberMatcher.method(JobThread.class, "getHandler");
    ((PowerMockitoStubber)PowerMockito.doReturn(iJobHandler).doReturn(iJobHandler1))
        .when(jobThread1, getHandlerMethod)
        .withNoArguments();
    final Method loadJobThreadMethod =
        DTUMemberMatcher.method(XxlJobExecutor.class, "loadJobThread", int.class);
    PowerMockito.doReturn(jobThread1)
        .when(XxlJobExecutor.class, loadJobThreadMethod)
        .withArguments(anyInt());

    // Act
    final ReturnT<String> retval = objectUnderTest.run(triggerParam);

    // Assert result
    Assert.assertNotNull(retval);
  }

  // Test written by Diffblue Cover.

  @Test
  public void staticInitOutputVoid() throws InvocationTargetException {

    // Act, using constructor to test static initializer
    final Object constructed = Reflector.getInstance("com.xxl.job.core.biz.impl.ExecutorBizImpl");

    // Method returns void, testing that no exception is thrown
  }
}
