package com.soriole.web.webrtc_signaling_server.eventbus;

import com.soriole.web.webrtc_signaling_server.Names;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEventBus;
import com.soriole.web.webrtc_signaling_server.api.annotation.NextRTCEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("nextRTCEventBusSetup")
@Scope("singleton")
public class EventBusSetup {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void setupHandlers() {
        context.getBeansWithAnnotation(NextRTCEventListener.class).values()
                .forEach(eventBus::register);
    }
}
