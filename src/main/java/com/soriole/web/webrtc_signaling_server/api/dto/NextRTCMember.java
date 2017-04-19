package com.soriole.web.webrtc_signaling_server.api.dto;

import javax.websocket.Session;

public interface NextRTCMember {
    default String getId() {
        return getSession().getId();
    }

    Session getSession();
}
