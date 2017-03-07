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

package io.sip3.tapir.twig.mongo.query;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.sip3.tapir.twig.model.SipSessionRequest;
import org.bson.conversions.Bson;

import java.util.Collection;

/**
 * Created by agafox.
 */
public class SipSessionQuery implements Query {

    private long[] millis;

    private Collection<String> callIds;

    private SipSessionQuery(long[] millis, Collection<String> callIds) {
        this.millis = millis;
        this.callIds = callIds;
    }

    public static SipSessionQuery of(SipSessionRequest request) {
        return new SipSessionQuery(request.getMillis(), request.getCallIds());
    }

    public SipSessionQuery withMillis(long[] millis) {
        this.millis = millis;
        return this;
    }

    @Override
    public long[] millis() {
        return millis;
    }

    @Override
    public Bson filter() {
        return Filters.and(
                between(),
                Filters.in("call_id", callIds)
        );
    }

    @Override
    public Bson sort() {
        return Sorts.ascending("millis", "nanos");
    }
}
