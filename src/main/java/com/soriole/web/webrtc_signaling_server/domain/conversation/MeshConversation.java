package com.soriole.web.webrtc_signaling_server.domain.conversation;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.soriole.web.webrtc_signaling_server.Names;
import com.soriole.web.webrtc_signaling_server.cases.ExchangeSignalsBetweenMembers;
import com.soriole.web.webrtc_signaling_server.domain.InternalMessage;
import com.soriole.web.webrtc_signaling_server.domain.Signal;
import org.apache.log4j.Logger;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEventBus;
import com.soriole.web.webrtc_signaling_server.domain.Conversation;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Scope("prototype")
public class MeshConversation extends Conversation {
    String TAG = this.getClass().getName();
    @Autowired
    private ExchangeSignalsBetweenMembers exchange;
    Gson gson = new Gson();
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    private Set<Member> members = Sets.newConcurrentHashSet();

    public MeshConversation(String id) {
        super(id);
    }

    @Override
    public void call(Member caller, Member callee, String content) {
        InternalMessage.create()//
                .to(callee)//
                .signal(Signal.CALLED)//
                .addCustom("type", "MESH")
                .content(content)//
                .build()//
                .send();
    }

    @Override
    public synchronized void join(Member sender) {
        logger.info("join: sender: "+sender.getId());
        System.out.println(TAG+" join: sender: "+sender.getId());
        assignSenderToConversation(sender);

        informSenderThatHasBeenJoined(sender);

        informRestAndBeginSignalExchange(sender);

        members.add(sender);
    }

    private void informRestAndBeginSignalExchange(Member sender) {
        System.out.println(TAG+" informRestAndBeginSignalExchange sender: "+sender.getId());
        logger.info("informRestAndBeginSignalExchange sender: "+sender.getId());
        for (Member to : members) {
            System.out.println(TAG+" sendJoinedFrom: sender: "+sender.getId()+" to: "+to.getId());
            logger.info("sendJoinedFrom: sender: "+sender.getId()+" to: "+to.getId());
            sendJoinedFrom(sender, to);
            System.out.println(TAG+" exchange.begin: to: "+to.getId()+" sender: "+sender.getId());
            logger.info("exchange.begin: to: "+to.getId()+" sender: "+sender.getId());
            exchange.begin(to, sender);
        }
    }

    private void informSenderThatHasBeenJoined(Member sender) {
        if (isWithoutMember()) {
            sendJoinedToFirst(sender, id);
        } else {
            sendJoinedToConversation(sender, id);
        }
    }

    public synchronized boolean isWithoutMember() {
        return members.isEmpty();
    }

    public synchronized boolean has(Member member) {
        return member != null && members.contains(member);
    }

    @Override
    public void exchangeSignals(InternalMessage message) {
        exchange.execute(message);
    }

    @Override
    public void broadcast(Member from, InternalMessage message) {
        members.stream()
                .filter(member -> !member.equals(from))
                .forEach(to -> message.copy()
                        .from(from)
                        .to(to)
                        .build()
                        .send());
    }

    @Override
    public synchronized boolean remove(Member leaving) {
        boolean remove = members.remove(leaving);
        if (remove) {
            leaving.unassignConversation(this);
            for (Member member : members) {
                sendLeftMessage(leaving, member);
            }
        }
        return remove;
    }

    private void sendJoinedToFirst(Member sender, String id) {
        InternalMessage.create()//
                .to(sender)//
                .signal(Signal.CREATED)//
                .addCustom("type", "MESH")
                .content(id)//
                .build()//
                .send();
    }

}
