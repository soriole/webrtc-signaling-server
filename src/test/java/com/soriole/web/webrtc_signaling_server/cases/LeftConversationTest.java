package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.Signal;
import com.soriole.web.webrtc_signaling_server.repository.Conversations;
import com.soriole.web.webrtc_signaling_server.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;

public class LeftConversationTest extends BaseTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private Members members;

    @Autowired
    private Conversations conversations;

    @Autowired
    private LeftConversation leftConversation;

    @Test
    public void shouldThrowAnExceptionWhenConversationDoesntExists() throws Exception {
        // given
        Member john = mockMember("Jan");
        members.register(john);

        // then
        exception.expectMessage("CONVERSATION_NOT_FOUND");

        // when
        leftConversation.execute(InternalMessage.create()
                .signal(Signal.LEFT)
                .from(john)
                .build());
    }

    @Test
    public void shouldLeaveConversation() throws Exception {
        // given
        Member john = mockMember("Jan");
        members.register(john);
        createConversation("conversationId", john);

        // when
        leftConversation.execute(InternalMessage.create()
                .from(john)
                .build());

        // then
        assertFalse(john.getConversation().isPresent());
    }

    @Test
    public void shouldRemoveConversationIfLastMemberLeft() throws Exception {
        // given
        Member john = mockMember("Jan");
        Member stan = mockMember("Stan");
        members.register(john);
        createConversation("conversationId", john);
        joinConversation("conversationId", stan);

        // when
        leftConversation.execute(InternalMessage.create()
                .from(john)
                .build());
        leftConversation.execute(InternalMessage.create()
                .from(stan)
                .build());

        // then
        assertFalse(john.getConversation().isPresent());
        assertFalse(stan.getConversation().isPresent());
        assertFalse(conversations.findBy("conversationId").isPresent());
    }
}