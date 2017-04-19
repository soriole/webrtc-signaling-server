package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Signals;
import org.springframework.stereotype.Component;

@Component(Signals.BUSY_HANDLER)
public class BusyHandler implements SignalHandler {

    public void execute(InternalMessage context) {
        InternalMessage.create()//
                .to(context.getTo())//
                .from(context.getFrom())//
                .content(context.getContent())//
                .custom(context.getCustom())//
                .signal(context.getSignal())
                .build()//
                .send();
    }
}
