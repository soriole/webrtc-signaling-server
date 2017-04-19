package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import org.springframework.stereotype.Component;

@Component(Signals.CANDIDATE_HANDLER)
public class CandidateHandler extends Exchange {

    @Override
    protected void exchange(InternalMessage message, Conversation conversation) {
        conversation.exchangeSignals(message);
    }
}
