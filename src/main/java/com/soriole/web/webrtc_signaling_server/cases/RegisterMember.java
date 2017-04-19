package com.soriole.web.webrtc_signaling_server.cases;

import com.soriole.web.webrtc_signaling_server.Names;
import com.soriole.web.webrtc_signaling_server.domain.Member;
import com.soriole.web.webrtc_signaling_server.domain.PingTask;
import com.soriole.web.webrtc_signaling_server.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component()
public class RegisterMember {

    @Value(Names.SCHEDULED_PERIOD)
    private int period;

    @Autowired
    private Members members;

    @Autowired
    @Qualifier(Names.SCHEDULER_NAME)
    private ScheduledExecutorService scheduler;

    @Autowired
    private ApplicationContext context;

    public void incoming(Session session) {
        members.register(context.getBean(Member.class, session, ping(session)));
    }

    private ScheduledFuture<?> ping(Session session) {
        return scheduler.scheduleAtFixedRate(new PingTask(session), period, period, TimeUnit.SECONDS);
    }

}
