package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import com.soriole.web.webrtc_signaling_server.exception.SignalingException;
import com.soriole.web.webrtc_signaling_server.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@Component(Signals.CREATE_HANDLER)
public class CreateConversation implements SignalHandler {

    @Autowired
    private Conversations conversations;

    public void execute(InternalMessage context) {
        conversations.findBy(context.getFrom())
                .map(Conversation::getId)
                .map(MEMBER_IN_OTHER_CONVERSATION::exception)
                .ifPresent(SignalingException::throwException);


        Conversation conversation = conversations.create(context);

        conversation.join(context.getFrom());
    }

}
