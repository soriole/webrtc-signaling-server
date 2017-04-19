package com.soriole.web.webrtc_signaling_server.repository;

import com.google.common.collect.Maps;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.conversation.BroadcastConversation;
import com.soriole.web.webrtc_signaling_server.domain.conversation.MeshConversation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;

public class ConversationsTest extends BaseTest {

    @Autowired
    private Conversations conversations;

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void shouldCreateConversation() throws Exception {
        // given

        // when
        Conversation createdConversation = conversations.create(InternalMessage.create()
                .content(null)
                .build());

        // then
        assertNotNull(createdConversation);
        assertNotNull(createdConversation.getId());
    }

    @Test
    public void shouldFindExistingConversation() throws Exception {
        // given
        Conversation conversation = conversations.create(InternalMessage.create()
                .content("new")
                .build());

        // when
        Optional<Conversation> found = conversations.findBy("new");

        // then
        assertTrue(found.isPresent());
        Conversation actual = found.get();
        assertEquals(conversation, actual);
        assertEquals(conversation.getId(), actual.getId());
    }

    @Test
    public void shouldCreateConversationWithRandomNameOnEmptyConversationName() throws Exception {
        // given

        // then
        final Conversation conversation = conversations.create(InternalMessage.create()
                .content("")
                .build());

        // when
        assertNotNull(conversation);
        assertThat(conversation.getId(), not(nullValue()));

    }

    @Test
    public void shouldThrowExceptionWhenConversationNameIsOccupied() throws Exception {
        // given
        conversations.create(InternalMessage.create()
                .content("aaaa")
                .build());

        // then
        expect.expectMessage(containsString(CONVERSATION_NAME_OCCUPIED.getErrorCode()));

        // when
        conversations.create(InternalMessage.create()
                .content("aaaa")
                .build());
    }

    @Test
    public void shouldCreateBroadcastConversationWhenInCustomPayloadTypeIsBroadcast() throws Exception {
        // given
        Map<String, String> custom = Maps.newHashMap();
        custom.put("type", "BROADCAST");

        // when
        conversations.create(InternalMessage.create()//
                .content("new conversation")//
                .custom(custom)
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        assertTrue(optional.get() instanceof BroadcastConversation);
    }

    @Test
    public void shouldCreateMeshConversationWhenInCustomPayloadTypeIsMesh() throws Exception {
        // given
        Map<String, String> custom = Maps.newHashMap();
        custom.put("type", "MESH");

        // when
        conversations.create(InternalMessage.create()//
                .content("new conversation")//
                .custom(custom)
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        assertTrue(optional.get() instanceof MeshConversation);
    }

    @Test
    public void shouldCreateMeshConversationByDefault() throws Exception {
        // given

        // when
        conversations.create(InternalMessage.create()//
                .content("new conversation")//
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        assertTrue(optional.get() instanceof MeshConversation);
    }


}
