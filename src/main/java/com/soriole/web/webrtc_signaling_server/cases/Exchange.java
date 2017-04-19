package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;

import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.INVALID_RECIPIENT;

public abstract class Exchange implements SignalHandler {

    @Override
    public final void execute(InternalMessage message) {
        Conversation conversation = checkPrecondition(message.getFrom());
        exchange(message, conversation);
    }

    protected abstract void exchange(InternalMessage message, Conversation conversation);

    private Conversation checkPrecondition(Member from) {
        if (!from.getConversation().isPresent()) {
            throw INVALID_RECIPIENT.exception();
        }
        return from.getConversation().get();
    }
}
