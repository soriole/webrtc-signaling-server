package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import com.soriole.web.webrtc_signaling_server.exception.SignalingException;
import com.soriole.web.webrtc_signaling_server.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@Component(Signals.JOIN_HANDLER)
public class JoinConversation implements SignalHandler {

    @Autowired
    private Conversations conversations;

    @Autowired
    @Qualifier(Signals.CREATE_HANDLER)
    private CreateConversation createConversation;

    public void execute(InternalMessage context) {
        conversations.findBy(context.getFrom())
                .map(Conversation::getId)
                .map(MEMBER_IN_OTHER_CONVERSATION::exception)
                .ifPresent(SignalingException::throwException);

        Optional<Conversation> conversation = findConversationToJoin(context);
        if (conversation.isPresent()) {
            conversation.get().join(context.getFrom());
        } else {
            createConversation.execute(context);
        }
    }

    private Optional<Conversation> findConversationToJoin(InternalMessage message) {
        return conversations.findBy(message.getContent());
    }

}
