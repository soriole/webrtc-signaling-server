package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.cases.connection.ConnectionContext;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.RTCConnections;
import org.apache.log4j.Logger;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ExchangeSignalsBetweenMembers {
    String TAG = this.getClass().getName();
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    @Autowired
    private RTCConnections connections;

    @Autowired
    private ApplicationContext context;

    public synchronized void begin(Member from, Member to) {
        System.out.println(TAG+" begin: from: " + from.getId() + " to: " + to.getId());
        logger.info("begin: from: " + from.getId() + " to: " + to.getId());
        connections.put(from, to, context.getBean(ConnectionContext.class, from, to));
        if (connections.get(from, to).isPresent()) {
            connections.get(from, to).ifPresent(ConnectionContext::begin);
        } else {
            System.out.println(TAG + "There is no connection from: " + from.getId() + " to: " + to.getId());
            logger.info("There is no connection from: " + from.getId() + " to: " + to.getId());
        }
    }

    public synchronized void execute(InternalMessage message) {
        connections.get(message.getFrom(), message.getTo()).ifPresent(context -> context.process(message));
    }
}
