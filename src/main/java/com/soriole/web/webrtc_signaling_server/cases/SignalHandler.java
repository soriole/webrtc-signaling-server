package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;

public interface SignalHandler {
    void execute(InternalMessage message);
}
