package com.soriole.web.webrtc_signaling_server.repository;

import com.google.common.collect.Maps;
import com.soriole.web.webrtc_signaling_server.Names;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import org.apache.commons.lang3.StringUtils;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEventBus;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.conversation.BroadcastConversation;
import com.soriole.web.webrtc_signaling_server.domain.conversation.MeshConversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static com.soriole.web.webrtc_signaling_server.api.NextRTCEvents.CONVERSATION_CREATED;
import static com.soriole.web.webrtc_signaling_server.api.NextRTCEvents.CONVERSATION_DESTROYED;
import static com.soriole.web.webrtc_signaling_server.domain.EventContext.builder;
import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;
import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.INVALID_CONVERSATION_NAME;

@Repository
public class Conversations {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    private ApplicationContext context;

    private Map<String, Conversation> conversations = Maps.newConcurrentMap();

    public Optional<Conversation> findBy(String id) {
        if (isEmpty(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(conversations.get(id));
    }

    public Optional<Conversation> findBy(Member from) {
        return conversations.values().stream().filter(conversation -> conversation.has(from)).findAny();
    }

    public void remove(String id, Member sender) {
        eventBus.post(CONVERSATION_DESTROYED.basedOn(
                builder()
                        .conversation(conversations.remove(id))
                        .from(sender)));
    }

    public Conversation create(InternalMessage message) {
        String conversationName = getConversationName(message.getContent());
        final Conversation conversation = create(conversationName, Optional.ofNullable(message.getCustom().get("type")));
        postEvent(message, conversation);
        return conversation;
    }

    private void postEvent(InternalMessage message, Conversation conversation) {
        eventBus.post(CONVERSATION_CREATED.basedOn(message, conversation));
    }

    private Conversation create(String conversationName, Optional<String> optionalType) {
        String type = optionalType.orElse("MESH");
        Conversation conversation = null;
        if (type.equalsIgnoreCase("BROADCAST")) {
            conversation = context.getBean(BroadcastConversation.class, conversationName);
        } else if (type.equalsIgnoreCase("MESH")) {
            conversation = context.getBean(MeshConversation.class, conversationName);
        }
        registerInContainer(conversation);
        return conversation;
    }

    private String getConversationName(String name) {
        final String conversationName = StringUtils.isBlank(name) ? UUID.randomUUID().toString() : name;
        validate(conversationName);
        return conversationName;
    }

    private void registerInContainer(Conversation conversation) {
        conversations.put(conversation.getId(), conversation);
    }

    private void validate(String name) {
        if (isEmpty(name)) {
            throw INVALID_CONVERSATION_NAME.exception();
        }
        if (conversations.containsKey(name)) {
            throw CONVERSATION_NAME_OCCUPIED.exception();
        }
    }

    public Collection<String> getAllIds() {
        return conversations.keySet();
    }
}
