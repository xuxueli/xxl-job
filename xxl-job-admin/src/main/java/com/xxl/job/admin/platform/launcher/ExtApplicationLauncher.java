package com.xxl.job.admin.platform.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author Ice2Faith
 * @date 2025/7/24 8:49
 */
public class ExtApplicationLauncher {
    public static final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS");
    public static final String LOG_LOCATION = ExtApplicationLauncher.class.getSimpleName();
    public static final String MAIN_CLASS_PROPERTY = "ext.main.class";
    public static final String MAIN_CLASS_MANIFEST = "Ext-Main-Class";

    public static final String EXT_PATH_PROPERTY = "ext.path";
    public static final String EXT_PATH_MANIFEST = "Ext-Path";

    protected String[] args;

    public ExtApplicationLauncher(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) throws Exception {
        ExtApplicationLauncher launcher = new ExtApplicationLauncher(args);
        launcher.launch();
    }

    public void log(Object obj) {
        System.out.println(String.format("%s [INFO ] [%s] [launcher] - %s", LOG_FMT.format(LocalDateTime.now()), LOG_LOCATION, String.valueOf(obj)));
    }

    public void launch() throws Exception {
        ClassLoader loader = createClassLoader();
        String mainClass = getMainClass();
        if (mainClass == null || "".equals(mainClass)) {
            throw new IllegalArgumentException(String.format("missing jvm argument -D%s= or %s defined in Manifest", MAIN_CLASS_PROPERTY, MAIN_CLASS_MANIFEST));
        }

        log(MAIN_CLASS_PROPERTY+": " + mainClass);

        Thread.currentThread().setContextClassLoader(loader);

        Class<?> clazz = Class.forName(mainClass, false, Thread.currentThread().getContextClassLoader());
        Method method = clazz.getDeclaredMethod("main", String[].class);
        method.setAccessible(true);
        method.invoke(null, (Object) args);
    }


    public ClassLoader createClassLoader() {
        List<String> extPaths = getExtPaths();
        for (String extPath : extPaths) {
            log(EXT_PATH_PROPERTY + ": " + extPath);
        }
        List<URL> urls = getPathUrls(extPaths);
        for (URL url : urls) {
            log("ext-resource: " + url);
        }
        return new ExtClasspathClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
    }

    public List<URL> getPathUrls(List<String> paths) {
        Set<URL> ret = new LinkedHashSet<>();
        for (String path : paths) {
            if (path == null || path.isEmpty()) {
                continue;
            }
            resolvePathUrls(true,new File(path), ret);
        }
        return new ArrayList<>(ret);
    }

    public void resolvePathUrls(boolean isRoot,File file, Set<URL> ret) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            if(isRoot) {
                try {
                    URL url = file.toURI().toURL();
                    ret.add(url);
                } catch (MalformedURLException e) {

                }
            }
            try {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File item : files) {
                        try {
                            resolvePathUrls(false,item, ret);
                        } catch (Exception e) {

                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (file.isFile()) {
            String name = file.getName();
            String suffix = getSuffix(name);

            if(isRoot || ".jar".equals(suffix)) {
                try {
                    URL url = file.toURI().toURL();
                    ret.add(url);
                } catch (MalformedURLException e) {

                }
                try {
                    File parentFile = file.getParentFile();
                    URL url = parentFile.toURI().toURL();
                    ret.add(url);
                } catch (MalformedURLException e) {

                }
            }



            if (".jar".equals(suffix)) {
                try {
                    resolveJarEmbedUrls(file, ret);
                } catch (Exception e) {

                }
            }
        }
    }

    public String getSuffix(String name) {
        int idx = name.lastIndexOf(".");
        String suffix = "";
        if (idx >= 0) {
            suffix = name.substring(idx);
        }
        suffix = suffix.toLowerCase();
        return suffix;
    }

    public void resolveJarEmbedUrls(File file, Set<URL> ret) throws Exception {
        if (file == null) {
            return;
        }
        try (JarFile jarFile = new JarFile(file)) {
            String baseUrl = file.toURI().toURL().toString();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry == null || entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (name == null || "".equals(name)) {
                    continue;
                }
                String suffix = getSuffix(name);
                if (".jar".equals(suffix)) {
                    try {
                        String url = "jar:" + baseUrl + "!" + name;
                        ret.add(new URL(url));
                    } catch (MalformedURLException e) {

                    }
                }
            }
        }
    }

    public List<String> getExtPaths() {
        List<String> ret = new ArrayList<>();
        String prop = getProperty(EXT_PATH_PROPERTY,
                EXT_PATH_MANIFEST,
                "lib,libs,plugin,plugins,ext-lib,ext-libs");
        String[] arr = prop.split(",|;|:");
        for (String item : arr) {
            item = item.trim();
            if (item.isEmpty()) {
                continue;
            }
            item = item.replace("\\", "/");
            File file = new File(item);
            file = new File(file.getAbsolutePath());
            String path = file.getAbsolutePath();
            path = path.replace("\\", "/");
            ret.add(path);
        }
        return ret;
    }

    public String getProperty(String systemKey, String manifestKey, String defaultValue) {
        String prop = null;

        try {
            prop = getSystemProperty(systemKey);
            if (prop != null && !prop.isEmpty()) {
                return prop;
            }
        } catch (Exception e) {

        }

        try {
            prop = getManifestProperty(manifestKey);
            if (prop != null && !prop.isEmpty()) {
                return prop;
            }
        } catch (Exception e) {

        }

        return defaultValue;
    }

    public String getSystemProperty(String key) {
        String property = null;
        for (String item : args) {
            if (item.startsWith(key + "=")) {
                property = item.substring((key + "=").length());
                if (property != null && !property.isEmpty()) {
                    return property;
                }
            }
        }

        property = System.getProperty(key);
        if (property != null && !property.isEmpty()) {
            return property;
        }
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = runtimeMXBean.getInputArguments();
        for (String item : inputArguments) {
            if (item.startsWith(key + "=")) {
                property = item.substring((key + "=").length());
                if (property != null && !property.isEmpty()) {
                    return property;
                }
            }
            if (item.startsWith("-D" + key + "=")) {
                property = item.substring(("-D" + key + "=").length());
                if (property != null && !property.isEmpty()) {
                    return property;
                }
            }
            if (item.startsWith("--" + key + "=")) {
                return item.substring(("--" + key + "=").length());
            }
        }
        return null;
    }

    public String getManifestProperty(String key) throws Exception {
        ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URI uri = codeSource.getLocation().toURI();
        if (uri == null) {
            return null;
        }
        String path = uri.getSchemeSpecificPart();
        if (path == null) {
            return null;
        }
        File file = new File(path);
        Manifest manifest = getManifest(file);
        if (manifest == null) {
            return null;
        }
        Attributes attributes = manifest.getMainAttributes();
        return attributes.getValue(key);
    }

    public Manifest getManifest(File file) {
        if (file.isDirectory()) {
            File manifestFile = new File(file, "META-INF/MANIFEST.MF");
            if (!manifestFile.isFile()) {
                return null;
            }
            try (InputStream is = new FileInputStream(manifestFile)) {
                return new Manifest(is);
            } catch (IOException e) {

            }
        }
        String name = file.getName();
        String suffix = getSuffix(name);
        if (".jar".equals(suffix)) {
            try (JarFile jarFile = new JarFile(file)) {
                return jarFile.getManifest();
            } catch (Exception e) {

            }
        }
        return null;
    }

    public String getMainClass() throws Exception {
        String prop = getProperty(MAIN_CLASS_PROPERTY, MAIN_CLASS_MANIFEST, null);
        if (prop != null) {
            prop = prop.trim();
        }
        return prop;

    }
}
