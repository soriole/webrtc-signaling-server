package com.soriole.web.webrtc_signaling_server.api;

import com.soriole.web.webrtc_signaling_server.api.dto.NextRTCEvent;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.EventContext;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.api.dto.NextRTCMember;
import com.soriole.web.webrtc_signaling_server.exception.Exceptions;

import javax.websocket.Session;

public enum NextRTCEvents {
    SESSION_OPENED,
    SESSION_CLOSED,
    CONVERSATION_CREATED,
    CONVERSATION_DESTROYED,
    UNEXPECTED_SITUATION,
    MEMBER_JOINED,
    MEMBER_LEFT,
    MEDIA_LOCAL_STREAM_REQUESTED,
    MEDIA_LOCAL_STREAM_CREATED,
    MEDIA_STREAMING,
    TEXT;

    public NextRTCEvent basedOn(InternalMessage message, Conversation conversation) {
        return EventContext.builder()
                .from(message.getFrom())
                .to(message.getTo())
                .custom(message.getCustom())
                .conversation(conversation)
                .type(this)
                .build();
    }

    public NextRTCEvent basedOn(EventContext.EventContextBuilder builder) {
        return builder
                .type(this)
                .build();
    }

    public NextRTCEvent occurFor(Session session, String reason) {
        return EventContext.builder()
                .from(new InternalMember(session))
                .type(this)
                .reason(reason)
                .build();
    }

    public NextRTCEvent occurFor(Session session) {
        return EventContext.builder()
                .type(this)
                .from(new InternalMember(session))
                .exception(Exceptions.UNKNOWN_ERROR.exception())
                .build();
    }

    public NextRTCEvent basedOn(InternalMessage message) {
        return EventContext.builder()
                .from(message.getFrom())
                .to(message.getTo())
                .custom(message.getCustom())
                .content(message.getContent())
                .type(this)
                .build();
    }

    private static class InternalMember implements NextRTCMember {

        private final Session session;

        InternalMember(Session session) {
            this.session = session;
        }

        @Override
        public Session getSession() {
            return session;
        }

        @Override
        public String getId() {
            if (session == null) {
                return null;
            }
            return session.getId();
        }

        @Override
        public String toString() {
            return getId();
        }
    }
}
