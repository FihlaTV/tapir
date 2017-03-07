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

package io.sip3.tapir.twig.sevice;

import io.sip3.tapir.core.SipMessage;
import io.sip3.tapir.core.util.PcapFormatter;
import io.sip3.tapir.twig.model.SipSessionDetails;
import io.sip3.tapir.twig.model.SipSessionFlow;
import io.sip3.tapir.twig.model.SipSessionRequest;
import io.sip3.tapir.twig.model.State;
import io.sip3.tapir.twig.mongo.Mongo;
import io.sip3.tapir.twig.mongo.query.SipSessionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by agafox.
 */
@Component
public class SipSessionService {

    @Value("${sip.acd}")
    private int acd; // Average call duration

    @Autowired
    private Mongo mongo;

    public SipSessionDetails details(SipSessionRequest request) {
        SipSessionDetails details = new SipSessionDetails();

        long cancelTime = 0, answerTime = 0, byeTime = 0;
        Map<String, SipSessionDetails.Leg> legs = new LinkedHashMap();

        Iterator<SipMessage> iterator = find(request);
        while (iterator.hasNext()) {
            SipMessage message = iterator.next();
            message.parse();

            if (message.isOrigin()) {
                legs.computeIfAbsent(message.getSrcHost() + ":" + message.getDstHost(), k -> {
                    SipSessionDetails.Leg leg = new SipSessionDetails.Leg();
                    leg.setSrcHost(message.getSrcHost());
                    leg.setDstHost(message.getDstHost());
                    leg.setCallId(message.getCallId());
                    leg.setFromTag(message.getFromTag());
                    leg.setFromUri(message.getFromURI());
                    leg.setToUri(message.getToURI());
                    leg.setRequestUri(message.getRequestURI());
                    return leg;
                });
            } else if (cancelTime == 0 && message.isCancel()) {
                cancelTime = message.getMillis();
            } else if (byeTime == 0 && message.isBye()) {
                byeTime = message.getMillis();
            } else if (answerTime == 0 && message.is200OK() && message.hasContent()) {
                answerTime = message.getMillis();
            }

            if (!message.isRequest()) {
                legs.computeIfPresent(message.getDstHost() + ":" + message.getSrcHost(), (k, v) -> {
                    v.setToTag(message.getToTag());
                    v.setLastResponse(message.getMethod() + " " + message.getDescription());
                    return v;
                });
            }
        }

        details.setLegs(legs.values());

        if (cancelTime > 0) {
            details.setState(State.Canceled);
        } else if (answerTime > 0 && byeTime > answerTime) {
            details.setState(State.Answered);
            details.setCallTime((byeTime - answerTime) / 1000);
        } else if (!legs.isEmpty()) {
            SipSessionDetails.Leg leg = details.getLegs().stream().findFirst().get();
            if (leg.getLastResponse().compareTo("400") >= 0) {
                details.setState(State.Failed);
            }
        }

        return details;
    }

    public SipSessionFlow flow(SipSessionRequest request) {
        SipSessionFlow flow = new SipSessionFlow();

        Iterator<SipMessage> iterator = find(request);
        while (iterator.hasNext()) {
            SipMessage message = iterator.next();
            message.parse();

            Collection<String> hosts = flow.getHosts();
            hosts.add(message.getSrcHost());
            hosts.add(message.getDstHost());

            SipSessionFlow.Message m = new SipSessionFlow.Message();
            m.setMillis(message.getMillis());
            m.setSrcHost(message.getSrcHost());
            m.setDstHost(message.getDstHost());
            m.setMethod(message.getMethod());
            m.setDescription(message.getDescription());
            m.setPayload(message.getPayload());
            m.setHighlights(message.getHighLights());

            flow.getMessages().add(m);
        }

        return flow;
    }

    public ByteArrayOutputStream pcap(SipSessionRequest request) throws Exception {
        return PcapFormatter.format(find(request));
    }

    private Iterator<SipMessage> find(SipSessionRequest request) {
        long[] millis = request.getMillis();
        SipSessionQuery query = SipSessionQuery.of(request);
        if (request.isCall()) {
            return mongo.find("raw_c", query.withMillis(new long[]{millis[0], millis[1] + acd}), SipMessage.class);
        }
        return mongo.find("raw_r", query, SipMessage.class);
    }
}
