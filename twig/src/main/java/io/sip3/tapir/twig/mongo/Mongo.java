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

package io.sip3.tapir.twig.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.sip3.tapir.core.partition.Partition;
import io.sip3.tapir.core.partition.PartitionFactory;
import io.sip3.tapir.twig.mongo.query.Query;
import io.sip3.tapir.twig.util.TimeIntervalIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by agafox.
 */
@Component("m")
public class Mongo {

    private final Partition partition;

    private final int batchSize;

    private final MongoDatabase db;

    @Autowired
    public Mongo(@Value("${mongo.partition}") String partition,
                 @Value("${mongo.batchSize}") int batchSize,
                 MongoDatabase db) {
        this.partition = PartitionFactory.ofPattern(partition);
        this.batchSize = batchSize;
        this.db = db;
    }

    public <T> Iterator<T> find(String prefix, Query query, Class<T> clazz) {
        return new Iterator<T>() {

            private final Iterator<Long> intervals = TimeIntervalIterator.of(query.millis(), partition);

            private MongoCursor<T> cursor;

            @Override
            public boolean hasNext() {
                if (cursor != null && cursor.hasNext()) {
                    return true;
                }

                while (intervals.hasNext()) {
                    long millis = intervals.next();
                    String collection = collection(prefix, partition.define(millis, false));

                    FindIterable<T> fi = db.getCollection(collection, clazz)
                            .find(query.filter())
                            .batchSize(batchSize);

                    if (query.sort() != null) {
                        fi.sort(query.sort());
                    }

                    cursor = fi.iterator();
                    if (cursor.hasNext()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return cursor.next();
            }
        };
    }

    private String collection(String prefix, String suffix) {
        return prefix + "_" + suffix;
    }
}
