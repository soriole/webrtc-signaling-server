package com.soriole.web.webrtc_signaling_server.domain;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import com.soriole.web.webrtc_signaling_server.cases.RegisterMember;
import com.soriole.web.webrtc_signaling_server.cases.SignalHandler;
import com.soriole.web.webrtc_signaling_server.exception.SignalingException;
import com.soriole.web.webrtc_signaling_server.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import static com.soriole.web.webrtc_signaling_server.exception.Exceptions.MEMBER_NOT_FOUND;

@Component
public class Server {

    private static final Logger log = Logger.getLogger(Server.class);
    @Autowired
    private Members members;

    @Autowired
    private SignalResolver resolver;

    @Autowired
    private RegisterMember register;

    public void register(Session s) {
        doSaveExecution(s, session ->
                register.incoming(session)
        );
    }

    public void handle(Message external, Session s) {
        doSaveExecution(s, session -> {
            Pair<Signal, SignalHandler> resolve = resolver.resolve(external.getSignal());
            InternalMessage internalMessage = buildInternalMessage(external, resolve.getKey(), session);
            processMessage(resolve.getValue(), internalMessage);
        });
    }

    private void processMessage(SignalHandler handler, InternalMessage message) {
        log.info("Incoming: " + message);
        if (handler != null) {
            handler.execute(message);
        }
    }

    private InternalMessage buildInternalMessage(Message message, Signal signal, Session session) {
        InternalMessage.InternalMessageBuilder bld = InternalMessage.create()//
                .from(findMember(session))//
                .content(message.getContent())//
                .signal(signal)//
                .custom(message.getCustom());
        members.findBy(message.getTo()).ifPresent(bld::to);
        return bld.build();
    }

    private Member findMember(Session session) {
        return members.findBy(session.getId()).orElseThrow(() -> new SignalingException(MEMBER_NOT_FOUND));
    }

    public void unregister(Session s, CloseReason reason) {
        doSaveExecution(s, session ->
                members.unregisterBy(session, reason.getReasonPhrase())
        );
    }


    public void handleError(Session s, Throwable exception) {
        doSaveExecution(s, session ->
                members.dropOutAfterException(session, exception.getMessage())
        );
    }

    private void doSaveExecution(Session session, Consumer<Session> action) {
        try {
            action.accept(session);
        } catch (Exception e) {
            log.warn("Server will try to handle this exception and send information as normal message through websocket", e);
            sendErrorOverWebSocket(session, e);
        }
    }

    private void sendErrorOverWebSocket(Session session, Exception e) {
        try {
            InternalMessage.create()
                    .to(new Member(session, null))
                    .signal(Signal.ERROR)
                    .content(e.getMessage())
                    .addCustom("stackTrace", writeStackTraceToString(e))
                    .build()
                    .send();
        } catch (Exception resendException) {
            log.error("Something goes wrong during resend! Exception omitted", resendException);
        }
    }

    private String writeStackTraceToString(Exception e) {
        if (log.isDebugEnabled()) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
        return e.getClass().getSimpleName() + " - " + e.getMessage();
    }

}
