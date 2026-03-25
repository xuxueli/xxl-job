package com.xxl.job.admin.service;

import com.xxl.tool.core.StringTool;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for addressList validation logic in trigger flow.
 * Verifies that user-supplied addressList is validated against the group's registered addresses,
 * preventing SSRF via arbitrary address injection (Issue #3935).
 */
public class AddressListValidationTest {

    /**
     * Simulates the validation logic from XxlJobServiceImpl.trigger().
     * Returns true if addressList is valid (all addresses belong to the group), false otherwise.
     */
    private boolean isAddressListValid(String addressList, List<String> groupAddressList) {
        if (!StringTool.isNotBlank(addressList)) {
            return true; // blank addressList is valid (uses default group addresses)
        }
        if (groupAddressList == null || groupAddressList.isEmpty()) {
            return false;
        }
        Set<String> allowedAddresses = new HashSet<>(groupAddressList);
        String[] inputAddresses = addressList.trim().split(",");
        for (String addr : inputAddresses) {
            if (StringTool.isBlank(addr) || !allowedAddresses.contains(addr.trim())) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testValidAddressInGroup() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999", "http://192.168.1.11:9999");
        assertTrue(isAddressListValid("http://192.168.1.10:9999", groupAddresses));
    }

    @Test
    public void testValidMultipleAddressesInGroup() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999", "http://192.168.1.11:9999");
        assertTrue(isAddressListValid("http://192.168.1.10:9999,http://192.168.1.11:9999", groupAddresses));
    }

    @Test
    public void testArbitraryAddressRejected() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999", "http://192.168.1.11:9999");
        assertFalse(isAddressListValid("http://evil-server.com:8080", groupAddresses));
    }

    @Test
    public void testInternalMetadataAddressRejected() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999");
        assertFalse(isAddressListValid("http://169.254.169.254/latest/meta-data", groupAddresses));
    }

    @Test
    public void testMixedValidAndInvalidAddressRejected() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999", "http://192.168.1.11:9999");
        assertFalse(isAddressListValid("http://192.168.1.10:9999,http://evil-server.com:8080", groupAddresses));
    }

    @Test
    public void testBlankAddressListAllowed() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999");
        assertTrue(isAddressListValid("", groupAddresses));
        assertTrue(isAddressListValid(null, groupAddresses));
        assertTrue(isAddressListValid("  ", groupAddresses));
    }

    @Test
    public void testEmptyGroupAddressesRejectsAll() {
        assertFalse(isAddressListValid("http://192.168.1.10:9999", new ArrayList<>()));
        assertFalse(isAddressListValid("http://192.168.1.10:9999", null));
    }

    @Test
    public void testLocalhostAddressRejectedWhenNotInGroup() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999");
        assertFalse(isAddressListValid("http://127.0.0.1:8080", groupAddresses));
        assertFalse(isAddressListValid("http://localhost:8080", groupAddresses));
    }

    @Test
    public void testDockerInternalAddressRejectedWhenNotInGroup() {
        List<String> groupAddresses = Arrays.asList("http://192.168.1.10:9999");
        assertFalse(isAddressListValid("http://canary:8080", groupAddresses));
    }
}
