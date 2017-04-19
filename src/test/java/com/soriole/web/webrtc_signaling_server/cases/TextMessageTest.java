package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.BaseTest;
import com.soriole.web.webrtc_signaling_server.MessageMatcher;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.Signal;
import com.soriole.web.webrtc_signaling_server.repository.Members;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TextMessageTest extends BaseTest {

    @Autowired
    private TextMessage textMessage;

    @Autowired
    private Members members;

    @Test
    public void shouldSendMessageFromOneToAnother() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        MessageMatcher stanMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        Member stan = mockMember("Stefan", stanMatcher);
        members.register(john);
        members.register(stan);
        createConversation("c", john);
        joinConversation("c", stan);
        johnMatcher.reset();
        stanMatcher.reset();

        // when
        textMessage.execute(InternalMessage.create()
                .from(john)
                .to(stan)
                .signal(Signal.TEXT)
                .content("Hello!")
                .addCustom("type", "Greeting")
                .build());

        // then
        assertThat(johnMatcher.getMessages(), hasSize(0));
        assertThat(stanMatcher.getMessages(), hasSize(1));
        assertThat(stanMatcher.getMessage().getContent(), is("Hello!"));
        assertThat(stanMatcher.getMessage().getFrom(), is("Jan"));
        assertThat(stanMatcher.getMessage().getTo(), is("Stefan"));
        assertThat(stanMatcher.getMessage().getSignal(), is("text"));
        assertThat(stanMatcher.getMessage().getCustom().get("type"), is("Greeting"));
    }

    @Test
    public void shouldSendMessageToAllMemberOfConversationIfToIsEmpty() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        MessageMatcher stanMatcher = new MessageMatcher();
        MessageMatcher markMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        Member stan = mockMember("Stefan", stanMatcher);
        Member mark = mockMember("Marek", markMatcher);
        members.register(john);
        members.register(stan);
        members.register(mark);
        createConversation("c", john);
        joinConversation("c", stan);
        joinConversation("c", mark);
        johnMatcher.reset();
        stanMatcher.reset();
        markMatcher.reset();

        // when
        textMessage.execute(InternalMessage.create()
                .from(john)
                .signal(Signal.TEXT)
                .content("Hello!")
                .addCustom("type", "Greeting")
                .build());

        // then
        assertThat(johnMatcher.getMessages(), hasSize(0));
        assertThat(stanMatcher.getMessages(), hasSize(1));
        assertThat(markMatcher.getMessages(), hasSize(1));
        assertMessage(stanMatcher, 0, "Jan", "Stefan", "text", "Hello!");
        assertThat(stanMatcher.getMessage().getCustom().get("type"), is("Greeting"));
        assertMessage(markMatcher, 0, "Jan", "Marek", "text", "Hello!");
        assertThat(stanMatcher.getMessage().getCustom().get("type"), is("Greeting"));

    }

    @Test
    public void shouldSendMessageFromOneToAnotherBuInSameConversation() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        MessageMatcher stanMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        Member stan = mockMember("Stefan", stanMatcher);
        members.register(john);
        members.register(stan);
        createConversation("d", john);
        johnMatcher.reset();

        // when
        textMessage.execute(InternalMessage.create()
                .from(john)
                .to(stan)
                .signal(Signal.TEXT)
                .content("Hello!")
                .addCustom("type", "Greeting")
                .build());

        // then
        assertThat(johnMatcher.getMessages(), hasSize(0));
        assertThat(stanMatcher.getMessages(), hasSize(0));
    }

}