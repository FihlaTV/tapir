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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by agafox.
 */
public class SipSessionFlow {

    private final Set<String> hosts = new LinkedHashSet();

    private final List<Message> messages = new ArrayList<>();

    public Set<String> getHosts() {
        return hosts;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public static class Message {

        @JsonProperty("millis")
        private long millis;

        @JsonProperty("src_host")
        private String srcHost;

        @JsonProperty("dst_host")
        private String dstHost;

        @JsonProperty("method")
        private String method;

        @JsonProperty("description")
        private String description;

        @JsonProperty("payload")
        private String payload;

        @JsonProperty("highlights")
        private List<String> highlights;

        public long getMillis() {
            return millis;
        }

        public void setMillis(long millis) {
            this.millis = millis;
        }

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

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

        public List<String> getHighlights() {
            return highlights;
        }

        public void setHighlights(List<String> highlights) {
            this.highlights = highlights;
        }
    }
}
