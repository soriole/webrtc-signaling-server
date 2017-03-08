package org.nextrtc.signalingserver.domain;

import com.google.gson.annotations.Expose;

/**
 * Created by robik on 3/8/17.
 */
public class CallRequest {
    @Expose
    private String callerMemberId;
    @Expose
    private String calleeMemberId;
    @Expose
    private String convId;

    public String getCallerMemberId() {
        return callerMemberId;
    }

    public void setCallerMemberId(String callerMemberId) {
        this.callerMemberId = callerMemberId;
    }

    public String getCalleeMemberId() {
        return calleeMemberId;
    }

    public void setCalleeMemberId(String calleeMemberId) {
        this.calleeMemberId = calleeMemberId;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }
}
