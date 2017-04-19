package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.Names;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEventBus;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEvents;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(Signals.TEXT_HANDLER)
public class TextMessage implements SignalHandler {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Override
    public void execute(InternalMessage message) {
        Member from = message.getFrom();
        if (message.getTo() == null && from.getConversation().isPresent()) {
            Conversation conversation = from.getConversation().get();
            conversation.broadcast(from, message);
            eventBus.post(NextRTCEvents.TEXT.basedOn(message));
        } else if (from.hasSameConversation(message.getTo())) {
            message.send();
            eventBus.post(NextRTCEvents.TEXT.basedOn(message));
        }

    }
}
