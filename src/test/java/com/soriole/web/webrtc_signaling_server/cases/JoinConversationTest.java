package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.MessageMatcher;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.exception.SignalingException;
import com.soriole.web.webrtc_signaling_server.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

public class JoinConversationTest extends BaseTest {

    @Autowired
    private JoinConversation joinConversation;

    @Autowired
    private Members members;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateNewConversationIfConversationDoesntExists() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

        // then
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getTo(), is("Jan"));
        assertThat(match.getMessage().getContent(), is("new conversation"));
        assertThat(match.getMessage().getCustom().size(), is(1));
        assertThat(match.getMessage().getCustom().get("type"), is("MESH"));
    }

    @Test
    public void shouldCreateNewConversationIfConversationDoesntExists_andHandleType() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .addCustom("type", "BROADCAST")
                .build());

        // then
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getTo(), is("Jan"));
        assertThat(match.getMessage().getContent(), is("new conversation"));
        assertThat(match.getMessage().getCustom().size(), is(1));
        assertThat(match.getMessage().getCustom().get("type"), is("BROADCAST"));
    }

    @Test
    public void shouldJoinMemberToConversation() throws Exception {
        // given
        Member member = mockMember("Jan");
        members.register(member);
        Member stach = mockMember("Stach");
        members.register(stach);
        createConversation("conv", stach);

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("conv")//
                .build());

        // then
        assertTrue(member.getConversation().isPresent());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsInOtherConversation() throws Exception {
        // given
        Member jan = mockMember("Jan");
        members.register(jan);
        Member stach = mockMember("Stach");
        members.register(stach);
        Member stefan = mockMember("Stefan");
        members.register(stefan);
        createConversation("conv", stach);
        createConversation("conv2", stefan);
        joinConversation.execute(InternalMessage.create()//
                .from(jan)//
                .content("conv2")//
                .build());

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(MEMBER_IN_OTHER_CONVERSATION.getErrorCode());

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(jan)//
                .content("conv")//
                .build());
    }

}