package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.cases.connection.ConnectionContext;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.RTCConnections;
import org.junit.Test;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.cases.connection.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class ExchangeSignalsBetweenMembersTest extends BaseTest {

    @Autowired
    private ExchangeSignalsBetweenMembers exchange1;

    @Autowired
    private ExchangeSignalsBetweenMembers exchange2;

    @Autowired
    private RTCConnections connections;

    @Test
    public void begin() throws Exception {
        // given
        Member john = mockMember("john");
        Member stan = mockMember("stan");
        Member ed = mockMember("ed");

        // when

        exchange1.begin(john, stan);
        exchange2.begin(john, ed);

        // then
        Optional<ConnectionContext> first = connections.get(john, stan);
        assertThat(first.isPresent(), is(true));
        assertThat(first.get().getState(), is(ConnectionState.OFFER_REQUESTED));
        Optional<ConnectionContext> second = connections.get(ed, john);
        assertThat(second.isPresent(), is(true));
        assertThat(second.get().getState(), is(ConnectionState.OFFER_REQUESTED));
    }

}