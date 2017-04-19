package com.soriole.web.webrtc_signaling_server;

import com.google.common.collect.Lists;
import com.soriole.web.webrtc_signaling_server.api.dto.NextRTCEvent;
import com.soriole.web.webrtc_signaling_server.api.NextRTCHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class EventChecker implements NextRTCHandler {

    List<NextRTCEvent> events = Lists.newArrayList();

    @Override
    public void handleEvent(NextRTCEvent event) {
        events.add(event);
    }

    public void reset() {
        events.clear();
    }

    public NextRTCEvent get(int index) {
        return events.get(index);
    }

    public List<NextRTCEvent> getEvents() {
        return this.events;
    }
}