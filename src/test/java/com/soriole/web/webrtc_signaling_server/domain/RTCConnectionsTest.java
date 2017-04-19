package com.soriole.web.webrtc_signaling_server.domain;

import com.soriole.web.webrtc_signaling_server.cases.connection.ConnectionContext;
import org.junit.Test;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RTCConnectionsTest extends BaseTest {

    @Autowired
    private RTCConnections connections;

    @Test
    public void shouldInitBiDirectionalConnectionBetweenTwoMembers() {
        // given
        Member john = mockMember("John");
        Member stan = mockMember("Stan");
        ConnectionContext context = mock(ConnectionContext.class);

        // when
        connections.put(john, stan, context);

        // then
        Optional<ConnectionContext> result = connections.get(john, stan);
        assertTrue(result.isPresent());
        Optional<ConnectionContext> reverse = connections.get(stan, john);
        assertTrue(reverse.isPresent());
        assertThat(result.get(), is(context));
        assertThat(reverse.get(), is(result.get()));
    }

    @Test
    public void shouldRemoveOldConnections() throws Exception {
        // given
        Member john = mockMember("John");
        Member stan = mockMember("Stan");
        ConnectionContext context = mock(ConnectionContext.class);
        when(context.getMaster()).thenReturn(john);
        when(context.getSlave()).thenReturn(stan);
        when(context.isCurrent()).thenReturn(false);
        connections.put(john, stan, context);

        // when
        connections.removeOldConnections();

        // then
        Optional<ConnectionContext> result = connections.get(john, stan);
        assertFalse(result.isPresent());
    }

}