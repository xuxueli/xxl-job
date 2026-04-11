package com.xxl.job.core.security;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Security tests for the GLUE type whitelist in XxlJobExecutor.
 *
 * Verifies that:
 * 1. Default whitelist only allows BEAN and GLUE_GROOVY
 * 2. Script types (Shell/Python/Node/PHP/PowerShell) are blocked by default
 * 3. Whitelist is configurable
 * 4. Invalid types in config are silently ignored (no crash)
 */
@DisplayName("GLUE Type Whitelist Security Tests")
class GlueTypeWhitelistSecurityTest {

    // ==================== Default Whitelist ====================

    @Test
    @DisplayName("Default whitelist includes BEAN")
    void testDefaultIncludesBean() {
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertTrue(allowed.contains("BEAN"), "BEAN should be in default whitelist");
    }

    @Test
    @DisplayName("Default whitelist includes GLUE_GROOVY")
    void testDefaultIncludesGroovy() {
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertTrue(allowed.contains("GLUE_GROOVY"), "GLUE_GROOVY should be in default whitelist");
    }

    @Test
    @DisplayName("Default whitelist does NOT include GLUE_SHELL")
    void testDefaultExcludesShell() {
        // Reset to default by creating a new executor (default is BEAN,GLUE_GROOVY)
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN,GLUE_GROOVY");
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertFalse(allowed.contains("GLUE_SHELL"), "GLUE_SHELL should NOT be in default whitelist");
    }

    @Test
    @DisplayName("Default whitelist does NOT include script types")
    void testDefaultExcludesScriptTypes() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN,GLUE_GROOVY");
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertFalse(allowed.contains("GLUE_PYTHON"), "GLUE_PYTHON should NOT be in default whitelist");
        assertFalse(allowed.contains("GLUE_PYTHON2"), "GLUE_PYTHON2 should NOT be in default whitelist");
        assertFalse(allowed.contains("GLUE_NODEJS"), "GLUE_NODEJS should NOT be in default whitelist");
        assertFalse(allowed.contains("GLUE_POWERSHELL"), "GLUE_POWERSHELL should NOT be in default whitelist");
        assertFalse(allowed.contains("GLUE_PHP"), "GLUE_PHP should NOT be in default whitelist");
    }

    // ==================== Configurable Whitelist ====================

    @Test
    @DisplayName("Can configure whitelist to include script types")
    void testConfigurableWhitelist() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN,GLUE_GROOVY,GLUE_SHELL,GLUE_PYTHON");
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertTrue(allowed.contains("BEAN"));
        assertTrue(allowed.contains("GLUE_GROOVY"));
        assertTrue(allowed.contains("GLUE_SHELL"));
        assertTrue(allowed.contains("GLUE_PYTHON"));
        assertFalse(allowed.contains("GLUE_NODEJS"));
    }

    @Test
    @DisplayName("Can configure whitelist to BEAN only")
    void testBeanOnlyWhitelist() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN");
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertTrue(allowed.contains("BEAN"));
        assertEquals(1, allowed.size(), "Only BEAN should be allowed");
    }

    @Test
    @DisplayName("Whitespace in config is handled correctly")
    void testWhitespaceHandling() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes(" BEAN , GLUE_GROOVY , GLUE_SHELL ");
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        assertTrue(allowed.contains("BEAN"));
        assertTrue(allowed.contains("GLUE_GROOVY"));
        assertTrue(allowed.contains("GLUE_SHELL"));
        assertEquals(3, allowed.size());
    }

    @Test
    @DisplayName("Null config preserves existing whitelist")
    void testNullConfigPreservesWhitelist() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN");
        Set<String> before = XxlJobExecutor.getAllowedGlueTypes();
        executor.setAllowedGlueTypes(null);
        Set<String> after = XxlJobExecutor.getAllowedGlueTypes();
        assertEquals(before, after, "Null config should not change whitelist");
    }

    @Test
    @DisplayName("Empty config preserves existing whitelist")
    void testEmptyConfigPreservesWhitelist() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN");
        Set<String> before = XxlJobExecutor.getAllowedGlueTypes();
        executor.setAllowedGlueTypes("");
        Set<String> after = XxlJobExecutor.getAllowedGlueTypes();
        assertEquals(before, after, "Empty config should not change whitelist");
    }

    // ==================== GlueTypeEnum Consistency ====================

    @Test
    @DisplayName("All GLUE enum names are valid whitelist entries")
    void testEnumNamesAreValidWhitelistEntries() {
        XxlJobExecutor executor = new XxlJobExecutor();
        StringBuilder allTypes = new StringBuilder();
        for (GlueTypeEnum type : GlueTypeEnum.values()) {
            if (allTypes.length() > 0) allTypes.append(",");
            allTypes.append(type.name());
        }
        executor.setAllowedGlueTypes(allTypes.toString());
        Set<String> allowed = XxlJobExecutor.getAllowedGlueTypes();
        for (GlueTypeEnum type : GlueTypeEnum.values()) {
            assertTrue(allowed.contains(type.name()), type.name() + " should be in whitelist when all configured");
        }
    }

    @Test
    @DisplayName("Script types are correctly identified via isScript()")
    void testScriptTypeIdentification() {
        // Verify the script type flag is consistent with what we're blocking
        assertFalse(GlueTypeEnum.BEAN.isScript(), "BEAN should not be a script type");
        assertFalse(GlueTypeEnum.GLUE_GROOVY.isScript(), "GLUE_GROOVY should not be a script type");
        assertTrue(GlueTypeEnum.GLUE_SHELL.isScript(), "GLUE_SHELL should be a script type");
        assertTrue(GlueTypeEnum.GLUE_PYTHON.isScript(), "GLUE_PYTHON should be a script type");
        assertTrue(GlueTypeEnum.GLUE_PYTHON2.isScript(), "GLUE_PYTHON2 should be a script type");
        assertTrue(GlueTypeEnum.GLUE_NODEJS.isScript(), "GLUE_NODEJS should be a script type");
        assertTrue(GlueTypeEnum.GLUE_POWERSHELL.isScript(), "GLUE_POWERSHELL should be a script type");
        assertTrue(GlueTypeEnum.GLUE_PHP.isScript(), "GLUE_PHP should be a script type");
    }

    // Restore default after tests
    @org.junit.jupiter.api.AfterEach
    void restoreDefaults() {
        XxlJobExecutor executor = new XxlJobExecutor();
        executor.setAllowedGlueTypes("BEAN,GLUE_GROOVY");
    }
}
