package org.nextrtc.signalingserver.cases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.nextrtc.signalingserver.domain.*;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(Signals.CALL_HANDLER)
public class CallHandler implements SignalHandler {
    private static final Logger log = Logger.getLogger(CallHandler.class);
    private Gson gson = new GsonBuilder().create();

    @Autowired
    private Conversations conversations;
    @Autowired
    private Members members;

    public void execute(InternalMessage context) {
        String content = context.getContent();
        CallRequest callRequest = gson.fromJson(content, CallRequest.class);
        Optional<Conversation> conversation = findConversationCreatedByCaller(callRequest.getConvId());
        if (!conversation.isPresent()) {
            sendConversationNotPresent(context.getFrom(), callRequest.getConvId());
            return;
        }
        Optional<Member> calleeMember = members.findBy(callRequest.getCalleeMemberId());
        if (!calleeMember.isPresent()) {
            sendCalleeNotPresent(context.getFrom(), callRequest.getCalleeMemberId());
            return;
        }

        Optional<Conversation> existingConversation = conversations.findBy(context.getTo());
        if (existingConversation.isPresent()) {
            sendBusySignal(callRequest, context.getFrom());
        }

        conversation.get().call(context.getFrom(), calleeMember.get(), context.getContent());
    }

    private void sendBusySignal(CallRequest callRequest, Member from) {
        InternalMessage.create()//
                .to(from)//
                .signal(Signal.BUSY)//
                .content(callRequest.getConvId())
                .build()
                .send();
    }

    private void sendConversationNotPresent(Member from, String convId) {
        InternalMessage.create()//
                .to(from)//
                .signal(Signal.CONVERSATION_NOT_PRESENT)//
                .addCustom("type", "MESH")
                .content(convId)//
                .build()//
                .send();
    }

    private void sendCalleeNotPresent(Member from, String calleeMemberId) {
        InternalMessage.create()//
                .to(from)//
                .signal(Signal.CALLEE_NOT_PRESENT)//
                .addCustom("type", "MESH")
                .content(calleeMemberId)//
                .build()//
                .send();
    }

    private Optional<Conversation> findConversationCreatedByCaller(String id) {
        return conversations.findBy(id);
    }

}
