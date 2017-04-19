package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.MessageMatcher;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.ServerEventCheck;
import com.soriole.web.webrtc_signaling_server.repository.Conversations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.EventChecker;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEvents;
import com.soriole.web.webrtc_signaling_server.api.annotation.NextRTCEventListener;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.exception.SignalingException;
import com.soriole.web.webrtc_signaling_server.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static com.soriole.web.webrtc_signaling_server.api.NextRTCEvents.CONVERSATION_CREATED;
import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;
import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@ContextConfiguration(classes = ServerEventCheck.class)
public class CreateConversationTest extends BaseTest {

    @Component
    @NextRTCEventListener(CONVERSATION_CREATED)
    public static class ServerEventCheck extends EventChecker {

    }

    @Autowired
    private CreateConversation create;

    @Autowired
    private Conversations conversations;

    @Autowired
    private Members members;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private ServerEventCheck eventCall;

    @Test
    public void shouldCreateConversation() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        Conversation conv = optional.get();
        assertThat(conv.has(member), is(true));
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getCustom().get("type"), is("MESH"));
        assertThat(eventCall.getEvents().size(), is(1));
        assertThat(eventCall.getEvents().get(0).type(), is(NextRTCEvents.CONVERSATION_CREATED));
    }

    @Test
    public void shouldCreateConversation_BROADCAST() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .addCustom("type", "BROADCAST")
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        Conversation conv = optional.get();
        assertThat(conv.has(member), is(true));
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getCustom().get("type"), is("BROADCAST"));
        assertThat(eventCall.getEvents().size(), is(1));
        assertThat(eventCall.getEvents().get(0).type(), is(NextRTCEvents.CONVERSATION_CREATED));
    }

    @Test
    public void shouldThrowExceptionWhenConversationExists() throws Exception {
        // given
        Member other = mockMember("Other");
        members.register(other);
        create.execute(InternalMessage.create()//
                .from(other)//
                .content("new conversation")//
                .build());

        Member member = mockMember("Jan");
        members.register(member);

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(CONVERSATION_NAME_OCCUPIED.getErrorCode());

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsInOtherConversation() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(MEMBER_IN_OTHER_CONVERSATION.getErrorCode());

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("second conversation")//
                .build());
    }
}
