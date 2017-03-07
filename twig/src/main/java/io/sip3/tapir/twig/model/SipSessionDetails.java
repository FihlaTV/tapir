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

package io.sip3.tapir.twig.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Created by agafox.
 */
public class SipSessionDetails {

    private State state = State.Unknown;

    @JsonProperty("call_time")
    private long callTime;

    private Collection<Leg> legs;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public Collection<Leg> getLegs() {
        return legs;
    }

    public void setLegs(Collection<Leg> legs) {
        this.legs = legs;
    }

    public static class Leg {

        @JsonProperty("src_host")
        private String srcHost;

        @JsonProperty("dst_host")
        private String dstHost;

        @JsonProperty("call_id")
        private String callId;

        @JsonProperty("from_tag")
        private String fromTag;

        @JsonProperty("to_tag")
        private String toTag;

        @JsonProperty("from_uri")
        private String fromUri;

        @JsonProperty("to_uri")
        private String toUri;

        @JsonProperty("r_uri")
        private String requestUri;

        @JsonProperty("last_response")
        private String lastResponse;

        public String getSrcHost() {
            return srcHost;
        }

        public void setSrcHost(String srcHost) {
            this.srcHost = srcHost;
        }

        public String getDstHost() {
            return dstHost;
        }

        public void setDstHost(String dstHost) {
            this.dstHost = dstHost;
        }

        public String getCallId() {
            return callId;
        }

        public void setCallId(String callId) {
            this.callId = callId;
        }

        public String getFromTag() {
            return fromTag;
        }

        public void setFromTag(String fromTag) {
            this.fromTag = fromTag;
        }

        public String getToTag() {
            return toTag;
        }

        public void setToTag(String toTag) {
            this.toTag = toTag;
        }

        public String getFromUri() {
            return fromUri;
        }

        public void setFromUri(String fromUri) {
            this.fromUri = fromUri;
        }

        public String getToUri() {
            return toUri;
        }

        public void setToUri(String toUri) {
            this.toUri = toUri;
        }

        public String getRequestUri() {
            return requestUri;
        }

        public void setRequestUri(String requestUri) {
            this.requestUri = requestUri;
        }

        public String getLastResponse() {
            return lastResponse;
        }

        public void setLastResponse(String lastResponse) {
            this.lastResponse = lastResponse;
        }
    }
}
