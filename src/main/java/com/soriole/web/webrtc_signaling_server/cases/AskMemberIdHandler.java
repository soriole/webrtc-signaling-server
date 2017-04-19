package com.soriole.web.webrtc_signaling_server.cases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import org.apache.log4j.Logger;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.Signal;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
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
