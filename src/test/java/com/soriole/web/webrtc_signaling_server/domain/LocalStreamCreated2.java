package com.soriole.web.webrtc_signaling_server.domain;

import com.soriole.web.webrtc_signaling_server.EventChecker;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEvents;
import com.soriole.web.webrtc_signaling_server.api.annotation.NextRTCEventListener;

@NextRTCEventListener({NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED})
public class LocalStreamCreated2 extends EventChecker {

}
