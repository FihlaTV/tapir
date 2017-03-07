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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Created by agafox.
 */
public class SipSessionRequest {

    @NotNull
    @Size(min = 2, max = 2)
    private long[] millis;

    @NotNull
    @Size(min = 1)
    @JsonProperty("call_ids")
    private Set<String> callIds;

    private boolean call;

    public long[] getMillis() {
        return millis;
    }

    public void setMillis(long[] millis) {
        this.millis = millis;
    }

    public Set<String> getCallIds() {
        return callIds;
    }

    public void setCallIds(Set<String> callIds) {
        this.callIds = callIds;
    }

    public boolean isCall() {
        return call;
    }

    public void setCall(boolean call) {
        this.call = call;
    }
}
