package org.nextrtc.signalingserver.cases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(Signals.ASK_MEMBER_ID_HANDLER)
public class AskMemberIdHandler implements SignalHandler {
    private static final Logger log = Logger.getLogger(AskMemberIdHandler.class);
    private Gson gson = new GsonBuilder().create();

    public void execute(InternalMessage context) {
        sendMemberIdAssigned(context.getFrom());
    }

    private void sendMemberIdAssigned(Member from) {
        InternalMessage.create()//
                .to(from)//
                .signal(Signal.MEMBER_ID_ASSIGNED)//
                .addCustom("type", "MESH")
                .content(from.getId())//
                .build()//
                .send();
    }
}
