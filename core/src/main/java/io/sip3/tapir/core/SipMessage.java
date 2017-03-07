/*
 *    Copyright 2017 SIP3.IO CORP.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.sip3.tapir.core;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.sip3.tapir.core.util.HeaderAddressUtil;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by agafox.
 */
public class SipMessage {

    private String id;

    private long millis;

    private int nanos;

    private String srcIp;

    private int srcPort;

    private String srcHost;

    private String dstIp;

    private int dstPort;

    private String dstHost;

    private String callId;

    private String method;

    private String caller;

    private String callee;

    private String payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public int getNanos() {
        return nanos;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public String getDstHost() {
        return dstHost;
    }

    public void setDstHost(String dstHost) {
        this.dstHost = dstHost;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isOrigin() {
        return isInvite() || isRegister();
    }

    public boolean isInvite() {
        return isMethod(SIPRequest.INVITE);
    }

    public boolean isCancel() {
        return isMethod(SIPRequest.CANCEL);
    }

    public boolean isBye() {
        return isMethod(SIPRequest.BYE);
    }

    public boolean is200OK() {
        return isMethod("200");
    }

    public boolean isRegister() {
        return isMethod(SIPRequest.REGISTER);
    }

    public boolean isMethod(String matcher) {
        return method != null ? method.matches(matcher) : false;
    }

    //
    // External 'jain-sip' framework specific
    //
    static {
        StringMsgParser.setComputeContentLengthFromMessage(true);
    }

    private SIPMessage msg;

    private boolean isRequest;

    public boolean parse() {
        try {
            this.msg = new StringMsgParser().parseSIPMessage(payload.getBytes(), true, false, null);
        } catch (ParseException e) {
            return false;
        }
        if (msg == null) {
            return false;
        }
        try {
            this.callId = msg.getCallId().getCallId();
            this.caller = HeaderAddressUtil.parseUser(msg.getFromHeader());
            this.callee = HeaderAddressUtil.parseUser(msg.getToHeader());
            if (msg instanceof SIPRequest) {
                SIPRequest r = (SIPRequest) msg;
                this.method = r.getMethod();
                this.isRequest = true;
            } else {
                SIPResponse r = (SIPResponse) msg;
                this.method = String.valueOf(r.getStatusCode());
                this.isRequest = false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public boolean hasContent() {
        return msg != null ? msg.hasContent() : false;
    }

    public boolean isCseqRegister() {
        return isCseqMethod(SIPRequest.REGISTER);
    }

    public boolean isCseqMethod(String matcher) {
        if (msg == null || msg.getCSeq() == null || msg.getCSeq().getMethod() == null) {
            return false;
        }
        return msg.getCSeq().getMethod().matches(matcher);
    }

    public String getRequestLine() {
        return isRequest ? ((SIPRequest) msg).getRequestLine().toString() : null;
    }

    public String getRequestURI() {
        return isRequest ? ((SIPRequest) msg).getRequestURI().toString() : null;
    }

    public String getFromURI() {
        return msg != null ? HeaderAddressUtil.parseURI(msg.getFromHeader()) : null;
    }

    public String getToURI() {
        return msg != null ? HeaderAddressUtil.parseURI(msg.getToHeader()) : null;
    }

    public String getFromTag() {
        return msg != null ? msg.getFromTag() : null;
    }

    public String getToTag() {
        return msg != null ? msg.getToTag() : null;
    }

    public String getContactUserAtHostPort() {
        return msg != null ? HeaderAddressUtil.parseUserAtHostPort(msg.getContactHeader()) : null;
    }

    public String getFromUserAtHostPort() {
        return msg != null ? HeaderAddressUtil.parseUserAtHostPort(msg.getFromHeader()) : null;
    }

    public String getToUserAtHostPort() {
        return msg != null ? HeaderAddressUtil.parseUserAtHostPort(msg.getToHeader()) : null;
    }

    public String getDescription() {
        return msg != null && !isRequest ? SIPResponse.getReasonPhrase(Integer.parseInt(method)) : null;
    }

    public List<String> getHighLights() {
        if (msg == null) {
            return null;
        }
        return Stream.of(
                getRequestLine(),
                getToUserAtHostPort(),
                getFromUserAtHostPort(),
                getContactUserAtHostPort()
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
