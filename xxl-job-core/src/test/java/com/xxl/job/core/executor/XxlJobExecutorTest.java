package com.xxl.job.core.executor;

import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.openapi.executor.dto.TriggerRequest;
import com.xxl.job.core.openapi.executor.impl.ExecutorBizImpl;
import com.xxl.job.core.thread.JobThread;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XxlJobExecutorTest {

    /**
     * Tests trigger - concurrent first scheduling for the same job ID.
     * Input: twenty serial trigger requests with the same job ID and different log IDs.
     * Output: all requests succeed, one JobThread is created, and the handler initializes once and executes all requests.
     */
    @Test
    void testTrigger_ConcurrentFirstSchedulingUsesOneJobThread(@TempDir Path tempDir) {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            int requestCount = 20;
            int jobId = 111;
            Logger logger = Logger.getLogger(XxlJobExecutorTest.class.getName());
            AtomicInteger initCount = new AtomicInteger();
            AtomicInteger executeCount = new AtomicInteger();
            Set<Long> executedLogIds = java.util.concurrent.ConcurrentHashMap.newKeySet();
            Set<String> executionThreadNames = java.util.concurrent.ConcurrentHashMap.newKeySet();
            CountDownLatch executionsFinished = new CountDownLatch(requestCount);
            IJobHandler handler = new IJobHandler() {
                @Override
                public void init() {
                    int currentInitCount = initCount.incrementAndGet();
                    logger.info(() -> String.format(
                            "[HANDLER_INIT] jobId=%d, initCount=%d, thread=%s",
                            jobId, currentInitCount, Thread.currentThread().getName()));
                }

                @Override
                public void execute() {
                    long logId = XxlJobContext.getXxlJobContext().getLogId();
                    String threadName = Thread.currentThread().getName();
                    executedLogIds.add(logId);
                    executionThreadNames.add(threadName);
                    int currentExecuteCount = executeCount.incrementAndGet();
                    logger.info(() -> String.format(
                            "[EXECUTE] jobId=%d, logId=%d, executeCount=%d/%d, thread=%s",
                            jobId, logId, currentExecuteCount, requestCount, threadName));
                    executionsFinished.countDown();
                }
            };

            logger.info(() -> String.format(
                    "[START] Concurrent first scheduling, jobId=%d, requestCount=%d",
                    jobId, requestCount));
            XxlJobExecutor executor = startExecutor(tempDir);
            ExecutorService callers = Executors.newFixedThreadPool(requestCount);
            try {
                executor.registryJobHandler("concurrentHandler", handler);
                ExecutorBizImpl executorBiz = new ExecutorBizImpl();
                CyclicBarrier startBarrier = new CyclicBarrier(requestCount);
                List<Future<Response<String>>> results = new ArrayList<>();
                Set<Long> expectedLogIds = new HashSet<>();

                for (int index = 0; index < requestCount; index++) {
                    long logId = index + 1L;
                    expectedLogIds.add(logId);
                    results.add(callers.submit(() -> {
                        startBarrier.await(5, TimeUnit.SECONDS);
                        logger.info(() -> String.format(
                                "[TRIGGER_START] jobId=%d, logId=%d, callerThread=%s",
                                jobId, logId, Thread.currentThread().getName()));
                        Response<String> response = executorBiz.trigger(newTriggerRequest(jobId, logId));
                        logger.info(() -> String.format(
                                "[TRIGGER_END] jobId=%d, logId=%d, code=%d, callerThread=%s",
                                jobId, logId, response.getCode(), Thread.currentThread().getName()));
                        return response;
                    }));
                }

                for (Future<Response<String>> result : results) {
                    assertTrue(result.get(5, TimeUnit.SECONDS).isSuccess());
                }
                assertTrue(executionsFinished.await(10, TimeUnit.SECONDS));

                JobThread registeredThread = executor.loadJobThread(jobId);
                logger.info(() -> String.format(
                        "[SUMMARY] jobId=%d, jobThread=%s, initCount=%d, executeCount=%d, executedLogIds=%s",
                        jobId,
                        registeredThread == null ? null : registeredThread.getName(),
                        initCount.get(),
                        executeCount.get(),
                        new java.util.TreeSet<>(executedLogIds)));
                assertNotNull(registeredThread);
                assertSame(handler, registeredThread.getHandler());
                assertEquals(1, initCount.get());
                assertEquals(requestCount, executeCount.get());
                assertEquals(expectedLogIds, executedLogIds);
                assertEquals(Set.of(registeredThread.getName()), executionThreadNames);
            } finally {
                callers.shutdownNow();
                JobThread removed = executor.removeJobThread(jobId, "concurrency test completed.");
                if (removed != null) {
                    removed.join(2_000);
                }
                executor.destroy();
                logger.info(() -> String.format(
                        "[CLEANUP] jobId=%d, removedThread=%s",
                        jobId, removed == null ? null : removed.getName()));
            }
        });
    }

    private static XxlJobExecutor startExecutor(Path tempDir) throws Exception {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAdminAddresses("http://127.0.0.1:1");
        executor.setAppname("xxl-job-concurrency-test");
        executor.setAccessToken("test-token");
        executor.setIp("127.0.0.1");
        executor.setPort(0);
        executor.setLogPath(tempDir.toString());
        executor.start();
        return executor;
    }

    private static TriggerRequest newTriggerRequest(int jobId, long logId) {
        TriggerRequest request = new TriggerRequest();
        request.setJobId(jobId);
        request.setExecutorHandler("concurrentHandler");
        request.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        request.setGlueType(GlueTypeEnum.BEAN.name());
        request.setLogId(logId);
        request.setLogDateTime(System.currentTimeMillis());
        request.setBroadcastTotal(1);
        return request;
    }
}
