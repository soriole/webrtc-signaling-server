package org.nextrtc.signalingserver.domain;

public interface Signals {
    String EMPTY = "";
    String CALL = "call";
    String CALLED = "called";
    String CALL_HANDLER = "nextRTC_CALL";
    String EMPTY_HANDLER = "nextRTC_EMPTY";
    String OFFER_REQUEST = "offerRequest";
    String OFFER_RESPONSE = "offerResponse";
    String OFFER_RESPONSE_HANDLER = "nextRTC_OFFER_RESPONSE";
    String ANSWER_REQUEST = "answerRequest";
    String ANSWER_RESPONSE = "answerResponse";
    String ANSWER_RESPONSE_HANDLER = "nextRTC_ANSWER_RESPONSE";
    String FINALIZE = "finalize";
    String CANDIDATE = "candidate";
    String CANDIDATE_HANDLER = "nextRTC_CANDIDATE";
    String PING = "ping";
    String LEFT = "left";
    String LEFT_HANDLER = "nextRTC_LEFT";
    String REJECT = "reject";
    String REJECTED = "rejected";
    String REJECT_HANDLER = "nextRTC_REJECT";
    String JOIN = "join";
    String JOIN_HANDLER = "nextRTC_JOIN";
    String CREATE = "create";
    String CREATE_HANDLER = "nextRTC_CREATE";
    String JOINED = "joined";
    String CREATED = "created";
    String MEMBER_ID_ASSIGNED = "memberIdAssigned";
    String TEXT = "text";
    String TEXT_HANDLER = "nextRTC_TEXT";
    String NEW_JOINED = "newJoined";
    String ERROR = "error";
    String CONVERSATION_NOT_PRESENT = "conversationNotPresent";
    String CALLEE_NOT_PRESENT = "calleeNotPresent";
    String END = "end";
}
