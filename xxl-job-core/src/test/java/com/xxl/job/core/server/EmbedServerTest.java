package com.xxl.job.core.server;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmbedServerTest {

    @Test
    void shouldReturnNullWhenBindIpIsBlank() {
        assertNull(EmbedServer.resolveBindAddress(null, 9999));
        assertNull(EmbedServer.resolveBindAddress("", 9999));
        assertNull(EmbedServer.resolveBindAddress("   ", 9999));
    }

    @Test
    void shouldTrimBindIpWhenResolveBindAddress() {
        InetSocketAddress bindAddress = EmbedServer.resolveBindAddress(" 127.0.0.1 ", 9999);

        assertEquals("127.0.0.1", bindAddress.getHostString());
        assertEquals(9999, bindAddress.getPort());
    }
}
