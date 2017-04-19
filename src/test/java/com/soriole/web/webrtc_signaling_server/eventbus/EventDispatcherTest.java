package com.soriole.web.webrtc_signaling_server.eventbus;

import com.soriole.web.webrtc_signaling_server.api.dto.NextRTCEvent;
import org.junit.Test;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEvents;
import com.soriole.web.webrtc_signaling_server.api.NextRTCHandler;
import com.soriole.web.webrtc_signaling_server.api.annotation.NextRTCEventListener;
import com.soriole.web.webrtc_signaling_server.domain.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static com.soriole.web.webrtc_signaling_server.api.NextRTCEvents.TEXT;


@ContextConfiguration(classes = {TextHandler.class, SecondHandler.class})
public class EventDispatcherTest extends BaseTest {

    @Autowired
    private EventDispatcher dispatcher;

    @Autowired
    private List<TextHandler> handlers;

    @Test
    public void shouldHandleAllMessagesEvenIfTheyAreThrowingExceptions() {
        // given
        assertThat(handlers.size(), greaterThan(1));
        handlers.forEach(h -> h.used = false);

        // when
        dispatcher.handle(EventContext.builder()
                .type(NextRTCEvents.TEXT)
                .build());

        // then
        handlers.forEach(h -> assertThat(h.used, is(true)));
    }
}

@Component("throwingExceptionHandler")
@NextRTCEventListener(TEXT)
class TextHandler implements NextRTCHandler {

    boolean used;

    @Override
    public void handleEvent(NextRTCEvent event) {
        used = true;
        throw new RuntimeException();
    }
}

@Component("throwingExceptionHandler2")
@NextRTCEventListener(TEXT)
class SecondHandler extends TextHandler {
}