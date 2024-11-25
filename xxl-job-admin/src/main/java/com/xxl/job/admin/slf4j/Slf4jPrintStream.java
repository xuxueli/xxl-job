package com.xxl.job.admin.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.Locale;

/**
 * @author Ice2Faith
 * @date 2022/6/27 10:38
 * @desc 控制台输出，重定向到SLF4J，以便在logback中能够显示控制台输出
 */
public class Slf4jPrintStream extends PrintStream {
    public enum Slf4jLevel {
        TRACE, DEBUG, INFO, WARN, ERROR;
    }

    // logback配置项名称
    public static final String LOGBACK_CONFIG_PROPERTY_NAME = "logging.config";
    // 初始化时自动获取本类类名
    protected final String selfClassName;
    protected final String selfSimpleName;
    // 被代理的打印流
    protected PrintStream target;
    // 代理流的日志级别
    protected Slf4jLevel level = Slf4jLevel.INFO;
    // 被代理流的名称
    protected String targetName = "";
    protected Logger log = LoggerFactory.getLogger(Slf4jPrintStream.class.getSimpleName());
    protected PerfLogger logger = new PerfLogger(log);
    // 是否依旧输出控制台，为true时，在控制台中将会看到两条输出，一条是slf4j,一条是sys.out/sys.err
    protected boolean keepConsole = false;
    // 是否启用打印堆栈跟踪，启用后将会跟踪堆栈，找出谁调用了本类进行打印，但是会带来一定的性能问题
    // 但是一般不影响，除非，项目中大量使用System.out进行日志打印
    // 但是，如果项目中已经使用了大量的System.out都能够正常运行，那么替换之后的性能代价也可以忽略
    protected boolean useTrace = true;

    protected boolean logbackEnv = false;

    public Slf4jPrintStream(Slf4jLevel level, PrintStream target, String targetName, boolean keepConsole, boolean useTrace) {
        super((OutputStream) new ByteArrayOutputStream());
        selfClassName = getClass().getName();
        selfSimpleName = getClass().getSimpleName();
        this.targetName = targetName;
        this.level = level;
        this.target = target;
        this.keepConsole = keepConsole;
        this.useTrace = useTrace;
        this.log = LoggerFactory.getLogger(getClass().getSimpleName() + "." + targetName);
        // 检测是否启用了logback
        String logback = System.getProperty(LOGBACK_CONFIG_PROPERTY_NAME);
        if (logback != null) {
            logback = logback.trim();
        }
        if (logback != null && !"".equals(logback)) {
            logbackEnv = true;
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        // 如果没有启用logback，需要降拦截的日志重新输出回控制台显示
        // 如果启用了控制台，则直接定向到logback日志中，不再进行回显到控制台
        if (!logbackEnv) {
            if (target != null) {
                target.write(b);
            }
        }
    }

    /**
     * 静态方法，进行控制台的标准输出和错误输出使用slf4j处理
     * 标准输出映射为INFO级别，错误输出映射为ERROR级别
     *
     * @param keepConsole
     * @param useTrace
     */
    public static void redirectSysoutSyserr(boolean keepConsole, boolean useTrace) {
        synchronized (System.class) {
            if (!(System.out instanceof Slf4jPrintStream)) {
                PrintStream out = System.out;
                System.setOut(new Slf4jPrintStream(Slf4jLevel.INFO, out, "sys.out", keepConsole, useTrace));
            }
            if (!(System.err instanceof Slf4jPrintStream)) {
                PrintStream err = System.err;
                System.setErr(new Slf4jPrintStream(Slf4jLevel.ERROR, err, "sys.err", keepConsole, useTrace));
            }
        }
    }

    public static void redirectSysoutSyserr() {
        redirectSysoutSyserr(false, true);
    }

    // 针对特殊数据做转换
    protected static Object stringify(Object val) {
        if (val == null) {
            return val;
        }
        if (val instanceof byte[]) {
            byte[] data = (byte[]) val;
            try {
                String str = new String(data);
                return str;
            } catch (Exception e) {

            }
            try {
                String str = new String(data, "UTF-8");
                return str;
            } catch (Exception e) {

            }
            try {
                String str = new String(data, "GBK");
                return str;
            } catch (Exception e) {

            }
        }
        return val;
    }

    protected void proxy(Object... vals) {
        if (vals.length <= 0) {
            return;
        }
        if (useTrace) {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            int idx = 0;
            for (int i = stacks.length - 1; i >= 0; i--) {
                if (stacks[i].getClassName().equals(selfClassName)) {
                    idx = i + 1;
                    break;
                }
            }
            String traceLocation = stacks[idx].getClassName();
            int didx = traceLocation.lastIndexOf(".");
            if (didx >= 0) {
                traceLocation = traceLocation.substring(didx + 1);
            }
            traceLocation += "." + stacks[idx].getMethodName() + "." + stacks[idx].getLineNumber();

            log = LoggerFactory.getLogger(selfSimpleName + "." + targetName + "." + traceLocation);
            logger = new PerfLogger(log);
        }
        if (level == Slf4jLevel.TRACE) {
            logger.trace(Slf4jPrintStream::serializeVals, vals);
        } else if (level == Slf4jLevel.DEBUG) {
            logger.debug(Slf4jPrintStream::serializeVals, vals);
        } else if (level == Slf4jLevel.INFO) {
            logger.info(Slf4jPrintStream::serializeVals, vals);
        } else if (level == Slf4jLevel.WARN) {
            logger.warn(Slf4jPrintStream::serializeVals, vals);
        } else if (level == Slf4jLevel.ERROR) {
            logger.error(Slf4jPrintStream::serializeVals, vals);
        }
    }

    public static Object serializeVals(Object... vals) {
        String line = null;
        if (vals.length == 1) {
            line = String.valueOf(stringify(vals[0]));
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object item : vals) {
                builder.append(stringify(item));
            }
            line = builder.toString();
        }
        return line;
    }

    @Override
    public void flush() {
        if (target != null) {
            target.flush();
        }
    }

    @Override
    public void close() {
        if (target != null) {
            target.close();
        }
    }

    @Override
    public boolean checkError() {
        if (target != null) {
            target.checkError();
        }
        return false;
    }

    @Override
    protected void setError() {

    }

    @Override
    protected void clearError() {

    }

    @Override
    public void write(int b) {
        proxy(b);
        if (target != null && keepConsole) {
            target.write(b);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = buf[off + i];
        }
        proxy(data);
        if (target != null && keepConsole) {
            target.write(buf, off, len);
        }
    }


    @Override
    public void print(boolean b) {
        proxy(b);
        if (target != null && keepConsole) {
            target.print(b);
        }
    }

    @Override
    public void print(char c) {
        proxy(c);
        if (target != null && keepConsole) {
            target.print(c);
        }
    }

    @Override
    public void print(int i) {
        proxy(i);
        if (target != null && keepConsole) {
            target.print(i);
        }
    }

    @Override
    public void print(long l) {
        proxy(l);
        if (target != null && keepConsole) {
            target.print(l);
        }
    }

    @Override
    public void print(float f) {
        proxy(f);
        if (target != null && keepConsole) {
            target.print(f);
        }
    }

    @Override
    public void print(double d) {
        proxy(d);
        if (target != null && keepConsole) {
            target.print(d);
        }
    }

    @Override
    public void print(char[] s) {
        proxy(s);
        if (target != null && keepConsole) {
            target.print(s);
        }
    }

    @Override
    public void print(String s) {
        proxy(s);
        if (target != null && keepConsole) {
            target.print(s);
        }
    }

    @Override
    public void print(Object obj) {
        proxy(obj);
        if (target != null && keepConsole) {
            target.print(obj);
        }
    }

    @Override
    public void println() {
        proxy("\n");
        if (target != null && keepConsole) {
            target.println();
        }
    }

    @Override
    public void println(boolean x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(char x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(int x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(long x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(float x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(double x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(char[] x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(String x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public void println(Object x) {
        proxy(x);
        if (target != null && keepConsole) {
            target.println(x);
        }
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter((Appendable) builder);
        formatter.format(Locale.getDefault(), format, args);
        proxy(builder.toString());
        if (target != null && keepConsole) {
            target.printf(format, args);
        }
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter((Appendable) builder);
        formatter.format(l, format, args);
        proxy(builder.toString());
        if (target != null && keepConsole) {
            target.printf(l, format, args);
        }
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter((Appendable) builder);
        formatter.format(Locale.getDefault(), format, args);
        proxy(builder.toString());
        if (target != null && keepConsole) {
            target.format(format, args);
        }
        return this;
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter((Appendable) builder);
        formatter.format(l, format, args);
        proxy(builder.toString());
        if (target != null && keepConsole) {
            target.format(l, format, args);
        }
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq) {
        proxy(csq);
        if (target != null && keepConsole) {
            target.append(csq);
        }
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        StringBuilder builder = new StringBuilder();
        builder.append(csq, start, end);
        proxy(builder.toString());
        if (target != null && keepConsole) {
            target.append(csq, start, end);
        }
        return this;
    }

    @Override
    public PrintStream append(char c) {
        proxy(c);
        if (target != null && keepConsole) {
            target.append(c);
        }
        return this;
    }


}
