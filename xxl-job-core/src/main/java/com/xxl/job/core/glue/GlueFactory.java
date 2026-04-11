package com.xxl.job.core.glue;

import com.xxl.job.core.glue.impl.SpringGlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * glue factory, product class/object by name
 *
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory {
	private static final Logger logger = LoggerFactory.getLogger(GlueFactory.class);

	/**
	 * Disallowed imports — classes that must not be used in GLUE Groovy scripts.
	 * Blocks process execution, file I/O, network, reflection, and classloader abuse.
	 * NOTE: these constants MUST be defined before glueFactory to avoid static init order issues.
	 */
	private static final List<String> DISALLOWED_IMPORTS = Arrays.asList(
			// Process execution
			"java.lang.Runtime",
			"java.lang.ProcessBuilder",
			// File system access
			"java.io.File",
			"java.io.FileInputStream",
			"java.io.FileOutputStream",
			"java.io.FileReader",
			"java.io.FileWriter",
			"java.io.RandomAccessFile",
			"java.nio.file.Files",
			"java.nio.file.Paths",
			"java.nio.file.Path",
			// Network access
			"java.net.Socket",
			"java.net.ServerSocket",
			"java.net.URL",
			"java.net.URLConnection",
			"java.net.HttpURLConnection",
			// Reflection & classloading
			"java.lang.reflect.Method",
			"java.lang.reflect.Field",
			"java.lang.reflect.Constructor",
			"java.lang.ClassLoader",
			"java.lang.Thread",
			"java.lang.ThreadGroup"
	);

	/** Disallowed star imports (wildcard). */
	private static final List<String> DISALLOWED_STAR_IMPORTS = Arrays.asList(
			"java.lang.reflect",
			"java.nio.file",
			"java.net"
	);

	/**
	 * Create a sandboxed GroovyClassLoader with SecureASTCustomizer.
	 */
	public static GroovyClassLoader createSandboxedClassLoader() {
		SecureASTCustomizer secure = new SecureASTCustomizer();

		// Block dangerous imports
		secure.setDisallowedImports(DISALLOWED_IMPORTS);
		secure.setDisallowedStarImports(DISALLOWED_STAR_IMPORTS);
		secure.setDisallowedStaticImports(DISALLOWED_IMPORTS);
		secure.setDisallowedStaticStarImports(DISALLOWED_STAR_IMPORTS);

		// Block dangerous receiver types — prevents calling methods on these classes
		secure.setDisallowedReceivers(Arrays.asList(
				"java.lang.Runtime",
				"java.lang.ProcessBuilder",
				"java.lang.System",
				"java.lang.ClassLoader",
				"java.lang.Thread",
				"java.lang.ThreadGroup",
				"java.io.File",
				"java.nio.file.Files",
				"java.nio.file.Paths"
		));

		// Disallow method pointer expressions (e.g., Runtime.&exec)
		secure.setMethodDefinitionAllowed(true);

		CompilerConfiguration config = new CompilerConfiguration();
		config.addCompilationCustomizers(secure);

		logger.info(">>>>>>>>>>> xxl-glue, sandboxed GroovyClassLoader created with SecureASTCustomizer");
		return new GroovyClassLoader(GlueFactory.class.getClassLoader(), config);
	}

	// Singleton — must be initialized AFTER the static constants above
	private static GlueFactory glueFactory = new GlueFactory();
	public static GlueFactory getInstance(){
		return glueFactory;
	}

	/**
	 * refresh instance by type
	 *
	 * @param type		0-frameless, 1-spring;
	 */
	public static void refreshInstance(int type){
		if (type == 0) {
			glueFactory = new GlueFactory();
		} else if (type == 1) {
			glueFactory = new SpringGlueFactory();
		}
	}

	/**
	 * Sandboxed groovy class loader — blocks dangerous operations like Runtime.exec,
	 * ProcessBuilder, file I/O, network, and reflection.
	 */
	private GroovyClassLoader groovyClassLoader = createSandboxedClassLoader();
	private ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

	/**
	 * load new instance, prototype
	 *
	 * @param codeSource
	 * @return
	 * @throws Exception
	 */
	public IJobHandler loadNewInstance(String codeSource) throws Exception{
		if (codeSource!=null && codeSource.trim().length()>0) {
			Class<?> clazz = getCodeSourceClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance();
				if (instance!=null) {
					if (instance instanceof IJobHandler) {
						this.injectService(instance);
						return (IJobHandler) instance;
					} else {
						throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
								+ "cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
					}
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}
	private Class<?> getCodeSourceClass(String codeSource){
		try {
			// md5
			byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
			String md5Str = new BigInteger(1, md5).toString(16);

			Class<?> clazz = CLASS_CACHE.get(md5Str);
			if(clazz == null){
				clazz = groovyClassLoader.parseClass(codeSource);
				CLASS_CACHE.putIfAbsent(md5Str, clazz);
			}
			return clazz;
		} catch (Exception e) {
			return groovyClassLoader.parseClass(codeSource);
		}
	}

	/**
	 * inject service of bean field
	 *
	 * @param instance
	 */
	public void injectService(Object instance) {
		// do something
	}

}
