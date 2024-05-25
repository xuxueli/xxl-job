package com.xxl.job.admin.slf4j;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @author Ice2Faith
 * @date 2022/10/2 15:08
 * @desc 性能优化的Slf4j
 * Performance Logger
 * 借助lambda表达式特性和预检查，减少非必要的日志拼接或计算
 * 因为即时slf4j底层会判断日志级别再输出日志
 * 但是对于如下调用，则依旧会涉及到拼接日志带来的开销
 * logger.info(()->"错误信息："+e.getMessage());
 * 等价写法：
 * logger.info(()->"错误信息："+e.getMessage());
 * logger.error((e)->"错误信息："+e.getMessage(),e);
 * 其他常见使用方式：
 * logger.info(()->"a"+2);
 * logger.warn(()->"w"+3);
 * logger.info(()->"a"+0);
 * logger.error((ex)->"错误信息："+ex.getMessage(), new RuntimeException("测试异常"));
 * logger.warnOne((val)->1+val,2);
 * logger.warnArgs((args)->"w"+args[0],1);
 */
public class PerfLogger {

    /**
     * 提供多参数
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface PerfSupplier<T> {
        T get(Object... args);
    }


    /**
     * 提供异常情况下的多参数
     *
     * @param <T>
     * @param <E>
     */
    @FunctionalInterface
    public interface PerfExceptionSupplier<T, E extends Throwable> {
        T get(E e, Object... args);
    }

    /**
     * 提供单参数的泛型支持
     *
     * @param <T>
     * @param <V>
     */
    @FunctionalInterface
    public interface PerfOneSupplier<T, V> {
        T get(V val);
    }

    /**
     * 提供双参数的泛型支持
     *
     * @param <T>
     * @param <V1>
     * @param <V2>
     */
    @FunctionalInterface
    public interface PerfTwoSupplier<T, V1, V2> {
        T get(V1 v1, V2 v2);
    }

    /**
     * 提供三参数的泛型支持
     *
     * @param <T>
     * @param <V1>
     * @param <V2>
     * @param <V3>
     */
    @FunctionalInterface
    public interface PerfThreeSupplier<T, V1, V2, V3> {
        T get(V1 v1, V2 v2, V3 v3);
    }

    private Logger logger;

    public PerfLogger(Logger logger) {
        this.logger = logger;
    }

    public static PerfLogger of(Logger log) {
        return new PerfLogger(log);
    }

    ////////////////////////////////////////////////////////////////////////
    public void info(Supplier<Object> src) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get()));
        }
    }

    public void warn(Supplier<Object> src) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get()));
        }
    }

    public void error(Supplier<Object> src) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get()));
        }
    }

    public void debug(Supplier<Object> src) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get()));
        }
    }

    public void trace(Supplier<Object> src) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get()));
        }
    }

    ////////////////////////////////////////////////////////////////////////
    public <T extends Throwable> void info(PerfOneSupplier<Object, T> src, T e) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get(e)), e);
        }
    }

    public <T extends Throwable> void warn(PerfOneSupplier<Object, T> src, T e) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get(e)), e);
        }
    }

    public <T extends Throwable> void error(PerfOneSupplier<Object, T> src, T e) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get(e)), e);
        }
    }

    public <T extends Throwable> void debug(PerfOneSupplier<Object, T> src, T e) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get(e)), e);
        }
    }

    public <T extends Throwable> void trace(PerfOneSupplier<Object, T> src, T e) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get(e)), e);
        }
    }


    ////////////////////////////////////////////////////////////////////////
    public <T> void info(PerfOneSupplier<Object, T> src, T val) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get(val)));
        }
    }

    public <T> void warn(PerfOneSupplier<Object, T> src, T val) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get(val)));
        }
    }

    public <T> void error(PerfOneSupplier<Object, T> src, T val) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get(val)));
        }
    }

    public <T> void debug(PerfOneSupplier<Object, T> src, T val) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get(val)));
        }
    }

    public <T> void trace(PerfOneSupplier<Object, T> src, T val) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get(val)));
        }
    }

    ////////////////////////////////////////////////////////////////////////
    public <V1, V2> void info(PerfTwoSupplier<Object, V1, V2> src, V1 v1, V2 v2) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get(v1, v2)));
        }
    }

    public <V1, V2> void warn(PerfTwoSupplier<Object, V1, V2> src, V1 v1, V2 v2) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get(v1, v2)));
        }
    }

    public <V1, V2> void error(PerfTwoSupplier<Object, V1, V2> src, V1 v1, V2 v2) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get(v1, v2)));
        }
    }

    public <V1, V2> void debug(PerfTwoSupplier<Object, V1, V2> src, V1 v1, V2 v2) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get(v1, v2)));
        }
    }

    public <V1, V2> void trace(PerfTwoSupplier<Object, V1, V2> src, V1 v1, V2 v2) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get(v1, v2)));
        }
    }

    ////////////////////////////////////////////////////////////////////////
    public <V1, V2, V3> void info(PerfThreeSupplier<Object, V1, V2, V3> src, V1 v1, V2 v2, V3 v3) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get(v1, v2, v3)));
        }
    }

    public <V1, V2, V3> void warn(PerfThreeSupplier<Object, V1, V2, V3> src, V1 v1, V2 v2, V3 v3) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get(v1, v2, v3)));
        }
    }

    public <V1, V2, V3> void error(PerfThreeSupplier<Object, V1, V2, V3> src, V1 v1, V2 v2, V3 v3) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get(v1, v2, v3)));
        }
    }

    public <V1, V2, V3> void debug(PerfThreeSupplier<Object, V1, V2, V3> src, V1 v1, V2 v2, V3 v3) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get(v1, v2, v3)));
        }
    }

    public <V1, V2, V3> void trace(PerfThreeSupplier<Object, V1, V2, V3> src, V1 v1, V2 v2, V3 v3) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get(v1, v2, v3)));
        }
    }

    ////////////////////////////////////////////////////////////////////////
    public void infoArgs(PerfSupplier<Object> src, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get(args)));
        }
    }

    public void warnArgs(PerfSupplier<Object> src, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get(args)));
        }
    }

    public void errorArgs(PerfSupplier<Object> src, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get(args)));
        }
    }

    public void debugArgs(PerfSupplier<Object> src, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get(args)));
        }
    }

    public void traceArgs(PerfSupplier<Object> src, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get(args)));
        }
    }

    ////////////////////////////////////////////////////////////////////////
    public <T extends Throwable> void infoEx(PerfExceptionSupplier<Object, T> src, T e, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(src.get(e, args)), e);
        }
    }

    public <T extends Throwable> void warnEx(PerfExceptionSupplier<Object, T> src, T e, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(src.get(e, args)), e);
        }
    }

    public <T extends Throwable> void errorEx(PerfExceptionSupplier<Object, T> src, T e, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(src.get(e, args)), e);
        }
    }

    public <T extends Throwable> void debugEx(PerfExceptionSupplier<Object, T> src, T e, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(src.get(e, args)), e);
        }
    }

    public <T extends Throwable> void traceEx(PerfExceptionSupplier<Object, T> src, T e, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(src.get(e, args)), e);
        }
    }
}
