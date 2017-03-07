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
import io.sip3.tapir.twig.model.SipSearchRequest;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by agafox.
 */
public class SipSearchQuery implements Query {

    private long[] millis;

    private String caller;

    private String callee;

    private SipSearchQuery(long[] millis, String caller, String callee) {
        this.millis = millis;
        this.caller = caller;
        this.callee = callee;
    }

    public static SipSearchQuery of(SipSearchRequest request) {
        return new SipSearchQuery(request.getMillis(), request.getCaller(), request.getCallee());
    }

    public SipSearchQuery withMillis(long[] millis) {
        this.millis = millis;
        return this;
    }

    @Override
    public long[] millis() {
        return millis;
    }

    @Override
    public Bson filter() {
        List<Bson> filters = Stream.of(
                between(),
                filter("caller", caller),
                filter("callee", callee)
        ).filter(Objects::nonNull).collect(Collectors.toList());

        return Filters.and(filters);
    }

    @Override
    public Bson sort() {
        if ((isBlank(caller) || isRegex(caller)) && (isBlank(callee) || isRegex(callee))) {
            return null;
        }
        return Sorts.ascending("millis");
    }

    private Bson filter(String field, String value) {
        if (isBlank(value)) {
            return null;
        }
        if (isRegex(value)) {
            return Filters.regex(field, value.replaceAll("\\*", "\\.\\*"));
        }
        return Filters.eq(field, value);
    }

    private boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isRegex(String str) {
        return str.contains(".") || str.contains("*");
    }
}
