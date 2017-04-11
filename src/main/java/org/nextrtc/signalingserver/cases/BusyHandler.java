package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.Signals;
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
