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
import io.sip3.tapir.twig.model.SipSearchRequest;
import io.sip3.tapir.twig.model.SipSession;
import io.sip3.tapir.twig.mongo.Mongo;
import io.sip3.tapir.twig.mongo.query.SipSearchQuery;
import io.sip3.tapir.twig.util.Iterators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by agafox.
 */
@Component
public class SipSearchService {

    private static final int DEFAULT_LIMIT = 200;

    @Value("${sip.att}")
    private int att; // Average transmission time

    @Value("${sip.art}")
    private int art; // Average retransmission time

    @Autowired
    private Mongo mongo;

    public List<SipSession> findSessions(SipSearchRequest request) {
        // Get iterator by messages sorted by millis...
        Iterator<SipMessage> iterator = find(request);
        // ... accumulate messages in session containers...
        Map<String, SipSessionContainer> accumulator = accumulate(iterator, request);
        // ... update containers with additional session data...
        charge(accumulator, iterator, request);
        // ... extract sessions in accordance with request data.
        return extract(accumulator, request);
    }

    private Iterator<SipMessage> find(SipSearchRequest request) {
        long[] millis = request.getMillis();
        // Time interval modification with average retransmission time to define each call leg...
        // ... it helps to exclude sessions which were started before search interval.
        SipSearchQuery query = SipSearchQuery.of(request)
                .withMillis(new long[]{millis[0] - art, millis[1] + art});
        if (request.isCall()) {
            return mongo.find("index_c", query, SipMessage.class);
        }
        return Iterators.merge(
                mongo.find("index_c", query, SipMessage.class),
                mongo.find("index_r", query.withMillis(millis), SipMessage.class),
                Comparator.comparingLong(SipMessage::getMillis)
        );
    }

    private Map<String, SipSessionContainer> accumulate(Iterator<SipMessage> iterator, SipSearchRequest request) {
        Map<String, SipSessionContainer> accumulator = new HashMap();

        int counter = 0;
        int limit = request.getLimit() == 0 ? DEFAULT_LIMIT : request.getLimit();

        while (iterator.hasNext() && counter < limit) {
            SipMessage message = iterator.next();
            String key = key(message);
            SipSessionContainer container = accumulator.get(key);
            if (container == null || !container.update(message)) {
                accumulator.put(key, new SipSessionContainer(message));
                if (checkMillis(message.getMillis(), request.getMillis())) {
                    counter++;
                }
                if (container != null && checkMillis(container.session.getMillis(), request.getMillis())) {
                    // 'message.getId()' looks like perfect unique identifier...
                    accumulator.put(message.getId(), container);
                }
            }
        }
        return accumulator;
    }

    private void charge(Map<String, SipSessionContainer> accumulator, Iterator<SipMessage> iterator, SipSearchRequest request) {
        long lastUpdated = accumulator.values().stream()
                .mapToLong(container -> container.lastUpdated)
                .max()
                .orElse(0);

        while (iterator.hasNext()) {
            SipMessage message = iterator.next();
            if (message.getMillis() - lastUpdated > art) {
                break;
            }
            String key = key(message);
            SipSessionContainer container = accumulator.get(key);
            if (container != null) {
                if (!container.update(message)) {
                    accumulator.remove(key);
                    if (checkMillis(container.session.getMillis(), request.getMillis())) {
                        // 'message.getId()' looks like perfect unique identifier...
                        accumulator.put(message.getId(), container);
                    }
                } else {
                    lastUpdated = message.getMillis();
                }
            }
        }
    }

    private List<SipSession> extract(Map<String, SipSessionContainer> accumulator, SipSearchRequest request) {
        return accumulator.values().stream()
                .map(container -> container.session)
                .filter(session -> checkMillis(session.getMillis(), request.getMillis()))
                .sorted(Comparator.comparing(SipSession::getMillis))
                .collect(Collectors.toList());
    }

    private String key(SipMessage message) {
        return message.getCaller() + ":" + message.getCallee();
    }

    private boolean checkMillis(long millis, long[] interval) {
        return millis >= interval[0] && millis <= interval[1];
    }

    private class SipSessionContainer {

        long lastUpdated;

        SipSession session;

        SipSessionContainer(SipMessage message) {
            SipSession session = new SipSession();
            session.setMillis(message.getMillis());
            session.setMethod(message.getMethod());
            session.setCaller(message.getCaller());
            session.setCallee(message.getCallee());
            session.getCallIds().add(message.getCallId());

            this.lastUpdated = session.getMillis();
            this.session = session;
        }

        boolean update(SipMessage message) {
            long millis = message.getMillis();
            String callId = message.getCallId();
            if ((millis - lastUpdated < att) || session.getCallIds().contains(callId)) {
                session.getCallIds().add(callId);
                lastUpdated = millis;
                return true;
            }
            return false;
        }
    }
}
