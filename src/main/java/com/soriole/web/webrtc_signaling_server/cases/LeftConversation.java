package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.CONVERSATION_NOT_FOUND;

@Component(Signals.LEFT_HANDLER)
public class LeftConversation implements SignalHandler {

    public void execute(InternalMessage context) {
        final Member leaving = context.getFrom();
        Conversation conversation = checkPrecondition(leaving.getConversation());

        conversation.left(leaving);
    }

    private Conversation checkPrecondition(Optional<Conversation> conversation) {
        if (!conversation.isPresent()) {
            throw CONVERSATION_NOT_FOUND.exception();
        }
        return conversation.get();
    }

}
