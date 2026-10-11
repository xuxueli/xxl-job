package com.xxl.job.core.glue;

import com.xxl.job.core.handler.IJobHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * verify glue class cache keeps only the latest version per jobId
 */
public class GlueFactoryTest {

    @BeforeEach
    void setUp() {
        GlueFactory.refreshInstance(0);
    }

    @Test
    void sameScript_reusesCachedClass() throws Exception {
        GlueFactory factory = GlueFactory.getInstance();
        String source = glueSource("SameScriptHandler", "v1");

        IJobHandler first = factory.loadNewInstance(1001, source);
        ClassLoader loader = factory.cachedClassLoader(1001);
        Class<?> clazz = factory.cachedClass(1001);

        IJobHandler second = factory.loadNewInstance(1001, source);

        assertNotNull(first);
        assertNotNull(second);
        assertSame(clazz, factory.cachedClass(1001));
        assertSame(loader, factory.cachedClassLoader(1001));
        assertEquals(1, factory.cacheSize());
    }

    @Test
    void updatedScript_replacesCacheAndKeepsOnlyLatest() throws Exception {
        GlueFactory factory = GlueFactory.getInstance();
        int jobId = 1002;

        IJobHandler oldHandler = factory.loadNewInstance(jobId, glueSource("UpdateHandler", "v1"));
        ClassLoader oldLoader = factory.cachedClassLoader(jobId);
        Class<?> oldClass = factory.cachedClass(jobId);

        IJobHandler newHandler = factory.loadNewInstance(jobId, glueSource("UpdateHandler", "v2"));
        ClassLoader newLoader = factory.cachedClassLoader(jobId);
        Class<?> newClass = factory.cachedClass(jobId);

        assertNotNull(oldHandler);
        assertNotNull(newHandler);
        assertNotEquals(oldLoader, newLoader);
        assertNotEquals(oldClass, newClass);
        assertEquals(1, factory.cacheSize());
        // Groovy may wrap with InnerLoader; parent is the version-scoped GroovyClassLoader retained by the factory
        assertSame(oldLoader, oldHandler.getClass().getClassLoader().getParent());
        assertSame(newLoader, newHandler.getClass().getClassLoader().getParent());
        assertSame(newLoader, factory.cachedClassLoader(jobId));
    }

    @Test
    void differentJobIds_doNotOverwriteEachOther() throws Exception {
        GlueFactory factory = GlueFactory.getInstance();

        factory.loadNewInstance(2001, glueSource("JobAHandler", "a"));
        factory.loadNewInstance(2002, glueSource("JobBHandler", "b"));

        assertEquals(2, factory.cacheSize());
        assertNotEquals(factory.cachedClassLoader(2001), factory.cachedClassLoader(2002));
    }

    @Test
    void clear_removesJobCacheEntry() throws Exception {
        GlueFactory factory = GlueFactory.getInstance();
        int jobId = 3001;

        factory.loadNewInstance(jobId, glueSource("ClearHandler", "v1"));
        assertEquals(1, factory.cacheSize());

        factory.clear(jobId);

        assertEquals(0, factory.cacheSize());
        assertNull(factory.cachedClassLoader(jobId));
    }

    private static String glueSource(String className, String versionTag) {
        return "package com.xxl.job.service.handler;\n"
                + "\n"
                + "import com.xxl.job.core.handler.IJobHandler;\n"
                + "\n"
                + "public class " + className + " extends IJobHandler {\n"
                + "    // version: " + versionTag + "\n"
                + "    @Override\n"
                + "    public void execute() throws Exception {\n"
                + "    }\n"
                + "}\n";
    }
}
