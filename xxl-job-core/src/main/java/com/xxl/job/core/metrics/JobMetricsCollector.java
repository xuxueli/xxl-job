package com.xxl.job.core.metrics;

import io.micrometer.core.instrument.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Job Metrics Collector for Prometheus/Micrometer integration
 * 
 * Collects metrics for:
 * - Job execution count (success/failure/timeout)
 * - Job execution duration
 * - Job queue size
 * - Executor thread pool metrics
 * - Callback success/failure rates
 * 
 * @author xxl-job-community
 * @since 3.3.0
 */
public class JobMetricsCollector {
    private static final Logger logger = LoggerFactory.getLogger(JobMetricsCollector.class);

    private static volatile JobMetricsCollector instance;
    private MeterRegistry meterRegistry;
    
    // Global metrics
    private Counter jobExecutionSuccessCounter;
    private Counter jobExecutionFailureCounter;
    private Counter jobExecutionTimeoutCounter;
    private Counter callbackSuccessCounter;
    private Counter callbackFailureCounter;
    private DistributionSummary jobExecutionDuration;
    private DistributionSummary callbackDuration;
    
    // Gauges for real-time monitoring
    private final ConcurrentMap<Integer, AtomicLong> jobQueueSizes = new ConcurrentHashMap<>();
    private final AtomicLong activeJobThreads = new AtomicLong(0);
    private final AtomicLong totalJobThreads = new AtomicLong(0);
    
    // Per-job metrics
    private final ConcurrentMap<Integer, Counter> perJobSuccessCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Counter> perJobFailureCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Timer> perJobTimers = new ConcurrentHashMap<>();

    private JobMetricsCollector() {
        // Private constructor for singleton
    }

    /**
     * Get singleton instance
     */
    public static JobMetricsCollector getInstance() {
        if (instance == null) {
            synchronized (JobMetricsCollector.class) {
                if (instance == null) {
                    instance = new JobMetricsCollector();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize metrics with MeterRegistry
     * Should be called once during application startup
     */
    public void init(MeterRegistry meterRegistry) {
        if (this.meterRegistry != null) {
            logger.warn("JobMetricsCollector already initialized");
            return;
        }
        
        this.meterRegistry = meterRegistry;
        logger.info("Initializing XXL-JOB metrics collector");
        
        // Job execution counters
        this.jobExecutionSuccessCounter = Counter.builder("xxl_job_execution_total")
                .tag("status", "success")
                .description("Total number of successful job executions")
                .register(meterRegistry);
        
        this.jobExecutionFailureCounter = Counter.builder("xxl_job_execution_total")
                .tag("status", "failure")
                .description("Total number of failed job executions")
                .register(meterRegistry);
        
        this.jobExecutionTimeoutCounter = Counter.builder("xxl_job_execution_total")
                .tag("status", "timeout")
                .description("Total number of timed out job executions")
                .register(meterRegistry);
        
        // Callback counters
        this.callbackSuccessCounter = Counter.builder("xxl_job_callback_total")
                .tag("status", "success")
                .description("Total number of successful callbacks")
                .register(meterRegistry);
        
        this.callbackFailureCounter = Counter.builder("xxl_job_callback_total")
                .tag("status", "failure")
                .description("Total number of failed callbacks")
                .register(meterRegistry);
        
        // Execution duration summary
        this.jobExecutionDuration = DistributionSummary.builder("xxl_job_execution_duration_seconds")
                .description("Job execution duration in seconds")
                .baseUnit("seconds")
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .register(meterRegistry);
        
        // Callback duration summary
        this.callbackDuration = DistributionSummary.builder("xxl_job_callback_duration_seconds")
                .description("Callback duration in seconds")
                .baseUnit("seconds")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        
        // Thread pool gauges
        Gauge.builder("xxl_job_thread_pool_active", activeJobThreads, AtomicLong::get)
                .description("Number of active job threads")
                .register(meterRegistry);
        
        Gauge.builder("xxl_job_thread_pool_total", totalJobThreads, AtomicLong::get)
                .description("Total number of job threads")
                .register(meterRegistry);
        
        logger.info("XXL-JOB metrics collector initialized successfully");
    }

    /**
     * Check if metrics collector is enabled
     */
    public boolean isEnabled() {
        return meterRegistry != null;
    }

    // ---------------------- Job Execution Metrics ----------------------

    /**
     * Record successful job execution
     */
    public void recordJobSuccess(int jobId, long durationMillis) {
        if (!isEnabled()) return;
        
        try {
            jobExecutionSuccessCounter.increment();
            jobExecutionDuration.record(durationMillis / 1000.0);
            
            // Per-job metrics
            getOrCreateJobSuccessCounter(jobId).increment();
            getOrCreateJobTimer(jobId).record(durationMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Error recording job success metrics", e);
        }
    }

    /**
     * Record failed job execution
     */
    public void recordJobFailure(int jobId, long durationMillis) {
        if (!isEnabled()) return;
        
        try {
            jobExecutionFailureCounter.increment();
            jobExecutionDuration.record(durationMillis / 1000.0);
            
            // Per-job metrics
            getOrCreateJobFailureCounter(jobId).increment();
            getOrCreateJobTimer(jobId).record(durationMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Error recording job failure metrics", e);
        }
    }

    /**
     * Record job timeout
     */
    public void recordJobTimeout(int jobId) {
        if (!isEnabled()) return;
        
        try {
            jobExecutionTimeoutCounter.increment();
            getOrCreateJobFailureCounter(jobId).increment();
        } catch (Exception e) {
            logger.error("Error recording job timeout metrics", e);
        }
    }

    // ---------------------- Callback Metrics ----------------------

    /**
     * Record successful callback
     */
    public void recordCallbackSuccess(long durationMillis) {
        if (!isEnabled()) return;
        
        try {
            callbackSuccessCounter.increment();
            callbackDuration.record(durationMillis / 1000.0);
        } catch (Exception e) {
            logger.error("Error recording callback success metrics", e);
        }
    }

    /**
     * Record failed callback
     */
    public void recordCallbackFailure(long durationMillis) {
        if (!isEnabled()) return;
        
        try {
            callbackFailureCounter.increment();
            callbackDuration.record(durationMillis / 1000.0);
        } catch (Exception e) {
            logger.error("Error recording callback failure metrics", e);
        }
    }

    // ---------------------- Queue Metrics ----------------------

    /**
     * Update job queue size
     */
    public void updateJobQueueSize(int jobId, int queueSize) {
        if (!isEnabled()) return;
        
        try {
            jobQueueSizes.computeIfAbsent(jobId, id -> {
                AtomicLong gauge = new AtomicLong(0);
                Gauge.builder("xxl_job_queue_size", gauge, AtomicLong::get)
                        .tag("job_id", String.valueOf(id))
                        .description("Job trigger queue size")
                        .register(meterRegistry);
                return gauge;
            }).set(queueSize);
        } catch (Exception e) {
            logger.error("Error updating job queue size metrics", e);
        }
    }

    // ---------------------- Thread Pool Metrics ----------------------

    /**
     * Update thread pool metrics
     */
    public void updateThreadPoolMetrics(int active, int total) {
        if (!isEnabled()) return;
        
        try {
            activeJobThreads.set(active);
            totalJobThreads.set(total);
        } catch (Exception e) {
            logger.error("Error updating thread pool metrics", e);
        }
    }

    /**
     * Increment active threads
     */
    public void incrementActiveThreads() {
        if (!isEnabled()) return;
        activeJobThreads.incrementAndGet();
        totalJobThreads.incrementAndGet();
    }

    /**
     * Decrement active threads
     */
    public void decrementActiveThreads() {
        if (!isEnabled()) return;
        activeJobThreads.decrementAndGet();
    }

    /**
     * Decrement total threads
     */
    public void decrementTotalThreads() {
        if (!isEnabled()) return;
        totalJobThreads.decrementAndGet();
    }

    // ---------------------- Per-Job Metrics Helpers ----------------------

    private Counter getOrCreateJobSuccessCounter(int jobId) {
        return perJobSuccessCounters.computeIfAbsent(jobId, id ->
                Counter.builder("xxl_job_execution_by_id_total")
                        .tag("job_id", String.valueOf(id))
                        .tag("status", "success")
                        .description("Job execution count by job ID")
                        .register(meterRegistry)
        );
    }

    private Counter getOrCreateJobFailureCounter(int jobId) {
        return perJobFailureCounters.computeIfAbsent(jobId, id ->
                Counter.builder("xxl_job_execution_by_id_total")
                        .tag("job_id", String.valueOf(id))
                        .tag("status", "failure")
                        .description("Job execution count by job ID")
                        .register(meterRegistry)
        );
    }

    private Timer getOrCreateJobTimer(int jobId) {
        return perJobTimers.computeIfAbsent(jobId, id ->
                Timer.builder("xxl_job_execution_by_id_duration_seconds")
                        .tag("job_id", String.valueOf(id))
                        .description("Job execution duration by job ID")
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .register(meterRegistry)
        );
    }

    /**
     * Clear metrics for a specific job (e.g., when job is deleted)
     */
    public void clearJobMetrics(int jobId) {
        if (!isEnabled()) return;
        
        try {
            jobQueueSizes.remove(jobId);
            
            Counter successCounter = perJobSuccessCounters.remove(jobId);
            if (successCounter != null) {
                meterRegistry.remove(successCounter.getId());
            }
            
            Counter failureCounter = perJobFailureCounters.remove(jobId);
            if (failureCounter != null) {
                meterRegistry.remove(failureCounter.getId());
            }
            
            Timer timer = perJobTimers.remove(jobId);
            if (timer != null) {
                meterRegistry.remove(timer.getId());
            }
        } catch (Exception e) {
            logger.error("Error clearing job metrics for jobId: " + jobId, e);
        }
    }
}