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

package io.sip3.tapir.salto.cron;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.sip3.tapir.core.partition.Partition;
import io.sip3.tapir.core.partition.PartitionFactory;
import io.sip3.tapir.salto.model.Field;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by agafox.
 */
@Component
public class SipMessageMongoCron implements Cron {

    private final Partition partition;

    private final long ttlRegisters;

    private final long ttlCalls;

    private final MongoDatabase db;

    @Autowired
    public SipMessageMongoCron(@Value("${mongo.partition}") String partition,
                               @Value("${ttl.registers}") String ttlRegisters,
                               @Value("${ttl.calls}") String ttlCalls,
                               MongoDatabase db) {
        this.partition = PartitionFactory.ofPattern(partition);
        this.ttlRegisters = PartitionFactory.ofPattern(ttlRegisters).duration();
        this.ttlCalls = PartitionFactory.ofPattern(ttlCalls).duration();
        this.db = db;
    }

    @PostConstruct
    @Override
    @Scheduled(cron = "${ttl.cron}")
    public void run() {
        long now = System.currentTimeMillis();
        String suffix = partition.define(now);
        ensureIndexes("r_" + suffix);
        ensureIndexes("c_" + suffix);
        suffix = partition.define(now + partition.duration());
        ensureIndexes("r_" + suffix);
        ensureIndexes("c_" + suffix);

        drop("r_", partition.define(now - ttlRegisters));
        drop("c_", partition.define(now - ttlCalls));
    }

    private void ensureIndexes(String suffix) {
        MongoCollection index = db.getCollection("index_" + suffix);
        index.createIndex(new Document(Field.millis, 1));
        index.createIndex(new Document(Field.call_id, "hashed"));
        index.createIndex(new Document(Field.caller, "hashed"));
        index.createIndex(new Document(Field.callee, "hashed"));

        MongoCollection raw = db.getCollection("raw_" + suffix);
        raw.createIndex(new Document(Field.call_id, "hashed"));
    }

    private void drop(String prefix, String suffix) {
        for (String collection : db.listCollectionNames()) {
            if (shouldDrop(collection, "index_" + prefix, suffix) || shouldDrop(collection, "raw_" + prefix, suffix)) {
                db.getCollection(collection).drop();
            }
        }
    }

    private boolean shouldDrop(String name, String prefix, String suffix) {
        return name.startsWith(prefix) && name.compareTo(prefix + suffix) < 0;
    }
}
