package com.xxl.job.admin.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;

import javax.imageio.spi.IIOServiceProvider;
import java.lang.management.*;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.Provider;
import java.sql.Driver;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Ice2Faith
 * @date 2022/4/14 18:36
 * @desc 继承 SpringBootServletInitializer 并重写configure方法，使得指向类指向启动类，则可以再war包中启动
 * 在war包中启动，pom.xml需要starter-web排除tomcat
 * 另外打包方式改为war
 */
public class WarBootApplication extends SpringBootServletInitializer {

    protected static Logger log = LoggerFactory.getLogger(WarBootApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        Slf4jPrintStream.redirectSysoutSyserr();
        return builder.sources(this.getClass())
                .listeners(getStartedListener(null,this.getClass()));
    }

    public static void startup(Class mainClass, String[] args) {
        startup(null, mainClass, args);
    }

    public static void startup(WebApplicationType webType, Class mainClass, String[] args) {
        Slf4jPrintStream.redirectSysoutSyserr();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        if (webType != null) {
            builder.web(WebApplicationType.NONE);
        }
        builder.sources(mainClass)
                .listeners(getStartedListener(webType,mainClass))
                .run(args);
    }

    public static ApplicationListener<ApplicationStartedEvent> getStartedListener(WebApplicationType webType, Class mainClass){
        return new ApplicationListener<ApplicationStartedEvent>() {
            @Override
            public void onApplicationEvent(ApplicationStartedEvent event) {
                ConfigurableApplicationContext application = event.getApplicationContext();
                String banner = getBootstrapBanner(webType, application, mainClass);
                log.warn(banner);
            }
        };
    }

    public static String getBootstrapBanner(WebApplicationType webType, ConfigurableApplicationContext application, Class<?> mainClass) {
        Environment env = application.getEnvironment();
        StringBuilder builder = new StringBuilder();
        builder.append("\n----------------------------------------------------------\n")
                .append("\twelcome to this system:\n")
                .append("\tapp    :\t").append(env.getProperty("spring.application.name")).append(" | ").append(env.getProperty("spring.profiles.active")).append("\n")
                .append("\tprocess:\t").append("PID:").append(getPid()).append(" | ").append("User:").append(getStartUser()).append("\n")
                .append("\tversion:\t").append("SpringBoot:").append(SpringBootVersion.getVersion()).append(" | ").append("Spring:").append(SpringVersion.getVersion()).append("\n");
        RuntimeMXBean runtimeMXBean = getRuntimeMXBean();
        if (runtimeMXBean != null) {
            long startTime = runtimeMXBean.getStartTime();
            long uptime = runtimeMXBean.getUptime();
            long currTime = System.currentTimeMillis();
            long diffStart = currTime - startTime;
            builder.append("\ttime   :\t").append("start:").append(diffStart)
                    .append(" | ").append("up:").append(uptime).append("\n");
        }
        if (isDebug()) {
            builder.append("\tdebug  :\t").append(true).append("\n");
        }
        if (isAgent()) {
            builder.append("\tagent  :\t").append(true).append("\n");
        }
        if (isNoVerify()) {
            builder.append("\tverify :\t").append(false).append("\n");
        }
        if (webType != null) {
            builder.append("\tweb    :\t").append(webType).append("\n");
        }
        if (webType != WebApplicationType.NONE) {
            try {

                String ip = InetAddress.getLocalHost().getHostAddress();
                String port = env.getProperty("server.port");
                String contextPath = env.getProperty("server.servlet.context-path");
                if (contextPath == null) {
                    contextPath = "";
                }
                if(contextPath.endsWith("/")){
                    contextPath=contextPath.substring(0,contextPath.length()-1);
                }
                if(!"".equals(contextPath) && !contextPath.startsWith("/")){
                    contextPath="/"+contextPath;
                }


                builder.append("\tlocal  : \thttp://localhost:").append(port).append(contextPath).append("/\n")
                        .append("\tnet    : \thttp://").append(ip).append(":").append(port).append(contextPath).append("/\n");

                Enumeration<NetworkInterface> allInterfaces = NetworkInterface.getNetworkInterfaces();
                while (allInterfaces.hasMoreElements()) {
                    NetworkInterface item = allInterfaces.nextElement();
                    if (!item.isUp() || item.isVirtual() || item.isLoopback()) {
                        continue;
                    }
                    builder.append("\t\t").append(" ").append(item.getName()).append(": ").append(item.getDisplayName()).append("\n");
                    Enumeration<InetAddress> addrs = item.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress addr = addrs.nextElement();
                        if (addr.isLoopbackAddress()) {
                            continue;
                        }
                        String pip = addr.getHostAddress();
                        if (addr instanceof Inet4Address) {
                            builder.append("\t\t\tipv4\thttp://").append(pip).append(":").append(port).append(contextPath).append("/\n");
                        } else if (addr instanceof Inet6Address) {
                            builder.append("\t\t\tipv6\thttp://").append(pip).append(":").append(port).append(contextPath).append("/\n");
                        }

                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        if (classLoadingMXBean != null) {
            builder.append("\tclasses:\t")
                    .append("Loaded:").append(classLoadingMXBean.getLoadedClassCount()).append(" | ")
                    .append("TotalLoaded:").append(classLoadingMXBean.getTotalLoadedClassCount()).append(" | ")
                    .append("Unloaded:").append(classLoadingMXBean.getUnloadedClassCount()).append("\n");
        }
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        if (classLoadingMXBean != null) {
            builder.append("\tcompile:\t")
                    .append("Name:").append(compilationMXBean.getName()).append(" | ")
                    .append("TotalTime:").append(compilationMXBean.getTotalCompilationTime()).append("\n");
        }
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (threadMXBean != null) {
            builder.append("\tthread :\t")
                    .append("All:").append(threadMXBean.getThreadCount()).append(" | ")
                    .append("Daemon:").append(threadMXBean.getDaemonThreadCount()).append(" | ")
                    .append("Peak:").append(threadMXBean.getPeakThreadCount()).append(" | ")
                    .append("Started:").append(threadMXBean.getTotalStartedThreadCount()).append("\n");
        }
        Thread thread = Thread.currentThread();
        if (thread != null) {
            builder.append("\tcurrent:\t")
                    .append("Group:").append(thread.getThreadGroup()).append(" | ")
                    .append("Name:").append(thread.getName()).append(" | ")
                    .append("ID:").append(thread.getId()).append(" | ")
                    .append("Priority:").append(thread.getPriority()).append("\n");
            builder.append("\tloaders:\n");
            ClassLoader classLoader = thread.getContextClassLoader();
            while (classLoader != null) {
                String name = classLoader.getClass().getName();
                builder.append("\t\t").append(name).append("\n");
                classLoader = classLoader.getParent();
            }
        }
        builder.append("\tgc     :\n");
        for (GarbageCollectorMXBean collectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            builder.append("\t\t").append(collectorMXBean.getName())
                    .append(" | ").append("Count:").append(collectorMXBean.getCollectionCount())
                    .append(" | ").append("Time:").append(collectorMXBean.getCollectionTime())
                    .append("\n");
        }
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        if (drivers != null) {
            builder.append("\tjdbc:\n");
            for (Driver driver : drivers) {
                builder.append("\t\t").append(driver.getClass().getName()).append("\n");
            }
        }

        ServiceLoader<Provider> providers = ServiceLoader.load(Provider.class);
        if (providers != null) {
            boolean isFirst=true;
            for (Provider provider : providers) {
                if(isFirst){
                    builder.append("\tjce:\n");
                }
                isFirst=false;
                builder.append("\t\t").append(String.format("%-6s", provider.getName())).append(":").append(provider.getClass().getName()).append("\n");
            }
        }

        ServiceLoader<IIOServiceProvider> ios = ServiceLoader.load(IIOServiceProvider.class);
        if (ios != null) {
            boolean isFirst=true;
            for (IIOServiceProvider io : ios) {
                if(isFirst){
                    builder.append("\tio:\n");
                }
                isFirst=false;
                builder.append("\t\t").append(io.getClass().getName()).append("\n");
            }
        }

        Runtime runtime = Runtime.getRuntime();
        builder.append("\tstartup:\t").append(mainClass.getName()).append(".class\n")
                .append("\tsystem :\t").append(env.getProperty("os.name")).append(" | ").append(env.getProperty("os.arch")).append(" | ").append(env.getProperty("os.version")).append("\n")
                .append("\tenv    :\t").append("core:").append(runtime.availableProcessors()).append(" | ").append("useRate:" + (((int) ((1.0 - (runtime.freeMemory() * 1.0 / runtime.totalMemory())) * 10000)) / 100.0)).append("% | ").append("free:").append(runtime.freeMemory() / 1024 / 1024).append("M").append(" | ").append("total:").append(runtime.totalMemory() / 1024 / 1024).append("M").append(" | ").append("max:").append(runtime.maxMemory() / 1024 / 1024).append("M").append("\n")
                .append("\tjava   :\t").append(env.getProperty("java.version")).append(" | ").append(env.getProperty("java.vendor")).append(" | ").append(env.getProperty("java.home")).append("\n")
                .append("\tjvm    :\t").append(env.getProperty("java.vm.name")).append(" | ").append(env.getProperty("java.vm.version")).append(" | ").append(env.getProperty("java.vm.vendor")).append("\n")
                .append("\ttmpdir :\t").append(env.getProperty("java.io.tmpdir")).append("\n")
                .append("\tuser   :\t").append(env.getProperty("user.name")).append(" | ").append(env.getProperty("user.dir")).append("\n")
                .append("----------------------------------------------------------\n");

        return builder.toString();
    }


    public static RuntimeMXBean getRuntimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    public static String getPid() {
        String name = getRuntimeMXBean().getName();
        String[] arr = name.split("@", 2);
        if (arr.length == 2) {
            return arr[0];
        }
        return "-1";
    }

    public static String getStartUser() {
        String name = getRuntimeMXBean().getName();
        String[] arr = name.split("@", 2);
        if (arr.length == 2) {
            return arr[1];
        }
        return "";
    }

    public static boolean isDebug() {
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if (arg.startsWith("-Xrunjdwp") || arg.startsWith("-agentlib:jdwp")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAgent() {
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if (arg.startsWith("-javaagent:")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoVerify() {
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if ("-noverify".equals(arg) || "-Xverify:none".equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
