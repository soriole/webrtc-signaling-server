package com.soriole.web.webrtc_signaling_server.domain;

import com.soriole.web.webrtc_signaling_server.EventChecker;
import com.soriole.web.webrtc_signaling_server.api.annotation.NextRTCEventListener;

import static com.soriole.web.webrtc_signaling_server.api.NextRTCEvents.CONVERSATION_CREATED;
import static com.soriole.web.webrtc_signaling_server.api.NextRTCEvents.SESSION_OPENED;

@NextRTCEventListener({SESSION_OPENED, CONVERSATION_CREATED})
public class ServerEventCheck extends EventChecker {

}
