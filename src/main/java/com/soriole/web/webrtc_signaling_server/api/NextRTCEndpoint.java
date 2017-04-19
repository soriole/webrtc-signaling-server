package com.soriole.web.webrtc_signaling_server.api;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import com.soriole.web.webrtc_signaling_server.domain.Message;
import com.soriole.web.webrtc_signaling_server.domain.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.Set;

@Component
public class NextRTCEndpoint {

    private static final Logger log = Logger.getLogger(NextRTCEndpoint.class);
    private Server server;

    private static Set<NextRTCEndpoint> endpoints = Sets.newConcurrentHashSet();

    public NextRTCEndpoint() {
        endpoints.add(this);
        log.info("Created " + this);
        endpoints.stream().filter(e -> e.server != null).findFirst().ifPresent(s -> this.setServer(s.server));
    }

    public void onOpen(Session session, EndpointConfig config) {
        log.info("Opening: " + session.getId());
        server.register(session);
    }

    public void onMessage(Message message, Session session) {
        log.info("Handling message from: " + session.getId());
        server.handle(message, session);
    }

    public void onClose(Session session, CloseReason reason) {
        log.info("Closing: " + session.getId() + " with reason: " + reason.getReasonPhrase());
        server.unregister(session, reason);
    }

    public void onError(Session session, Throwable exception) {
        log.error("Occured exception for session: " + session.getId() + ", reason: " + exception.getMessage());
        log.debug("Endpoint exception: ", exception);
        server.handleError(session, exception);
    }

    @Autowired
    public void setServer(Server server) {
        log.info("Setted server: " + server + " to " + this);
        this.server = server;
    }
}
