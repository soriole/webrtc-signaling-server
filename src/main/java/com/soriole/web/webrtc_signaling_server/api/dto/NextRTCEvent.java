package com.soriole.web.webrtc_signaling_server.api.dto;

import org.joda.time.DateTime;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEvents;
import com.soriole.web.webrtc_signaling_server.exception.SignalingException;

import java.util.Map;
import java.util.Optional;

public interface NextRTCEvent {

    NextRTCEvents type();

    DateTime published();

    Optional<NextRTCMember> from();

    Optional<NextRTCMember> to();

    Optional<NextRTCConversation> conversation();

    Optional<SignalingException> exception();

    Map<String, String> custom();

    Optional<String> content();

    Optional<String> reason();

}
