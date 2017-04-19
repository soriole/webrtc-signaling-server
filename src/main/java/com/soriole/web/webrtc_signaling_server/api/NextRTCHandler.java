package com.soriole.web.webrtc_signaling_server.api;

import com.soriole.web.webrtc_signaling_server.api.dto.NextRTCEvent;

public interface NextRTCHandler {

    void handleEvent(NextRTCEvent event);

}
