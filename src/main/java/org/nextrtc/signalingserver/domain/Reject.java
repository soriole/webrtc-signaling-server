package org.nextrtc.signalingserver.domain;

import com.google.gson.annotations.Expose;

/**
 * Created by robik on 3/8/17.
 */
public class Reject {
    @Expose
    private String rejectorMemberId;
    @Expose
    private String rejectedMemberId;
    @Expose
    private String convId;

    public String getRejectorMemberId() {
        return rejectorMemberId;
    }

    public void setRejectorMemberId(String rejectorMemberId) {
        this.rejectorMemberId = rejectorMemberId;
    }

    public String getRejectedMemberId() {
        return rejectedMemberId;
    }

    public void setRejectedMemberId(String rejectedMemberId) {
        this.rejectedMemberId = rejectedMemberId;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }
}
