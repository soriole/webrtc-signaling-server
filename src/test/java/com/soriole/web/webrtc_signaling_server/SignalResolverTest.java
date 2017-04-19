package com.soriole.web.webrtc_signaling_server;

import com.soriole.web.webrtc_signaling_server.domain.Signal;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import com.soriole.web.webrtc_signaling_server.cases.SignalHandler;
import com.soriole.web.webrtc_signaling_server.domain.SignalResolver;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SignalResolverTest extends BaseTest {

    @Autowired
    private SignalResolver signals;

    @Test
    public void shouldCheckResolvingSignalBasedOnString() throws Exception {
        // given

        // when
        Pair<Signal, SignalHandler> existing = signals.resolve(Signals.FINALIZE);

        // then
        assertNotNull(existing);
        assertThat(existing.getKey(), is(Signal.FINALIZE));
    }

    @Test
    public void shouldReturnDefaultImplementationOnNotExistingSignal() throws Exception {
        // given

        // when
        Pair<Signal, SignalHandler> notExisting = signals.resolve("not existing");

        // then
        assertNotNull(notExisting);
        assertThat(notExisting.getKey(), is(Signal.EMPTY));
    }
}
