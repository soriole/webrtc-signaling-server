package org.nextrtc.signalingserver.domain.conversation;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Scope("prototype")
public class MeshConversation extends Conversation {
    @Autowired
    private ExchangeSignalsBetweenMembers exchange;
    Gson gson = new Gson();

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
                .content(gson.toJson(content))//
                .build()//
                .send();
    }

    @Override
    public synchronized void join(Member sender) {
        assignSenderToConversation(sender);

        informSenderThatHasBeenJoined(sender);

        informRestAndBeginSignalExchange(sender);

        members.add(sender);
    }

    private void informRestAndBeginSignalExchange(Member sender) {
        for (Member to : members) {
            sendJoinedFrom(sender, to);
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
