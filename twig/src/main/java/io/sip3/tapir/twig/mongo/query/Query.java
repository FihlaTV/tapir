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
import org.bson.conversions.Bson;

/**
 * Created by agafox.
 */
public interface Query {

    long[] millis();

    Bson filter();

    Bson sort();

    default Bson between() {
        return Filters.and(
                Filters.gte("millis", millis()[0]),
                Filters.lte("millis", millis()[1])
        );
    }
}
