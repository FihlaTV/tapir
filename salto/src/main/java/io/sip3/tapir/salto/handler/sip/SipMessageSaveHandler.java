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

package io.sip3.tapir.salto.handler.sip;

import com.lmax.disruptor.WorkHandler;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import io.sip3.tapir.core.SipMessage;
import io.sip3.tapir.core.partition.Partition;
import io.sip3.tapir.core.partition.PartitionFactory;
import io.sip3.tapir.salto.model.Field;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by agafox.
 */
@Component
public class SipMessageSaveHandler implements WorkHandler<List<SipMessage>> {

    private final Partition partition;

    private final MongoDatabase db;

    @Autowired
    public SipMessageSaveHandler(@Value("${mongo.partition}") String partition, MongoDatabase db) {
        this.partition = PartitionFactory.ofPattern(partition);
        this.db = db;
    }

    @Override
    public void onEvent(List<SipMessage> messages) throws Exception {
        if (messages.isEmpty()) {
            return;
        }

        Map<String, List> documents = new HashMap<>();

        for (SipMessage message : messages) {
            String suffix = defineSuffix(message);
            if (message.isOrigin()) {
                Document document = convertToIndex(message);
                documents.computeIfAbsent("index_" + suffix, k -> new ArrayList()).add(new InsertOneModel<>(document));
            }
            Document document = convertToRaw(message);
            documents.computeIfAbsent("raw_" + suffix, k -> new ArrayList()).add(new InsertOneModel<>(document));
        }

        documents.forEach((k, v) -> db.getCollection(k).bulkWrite(v));
    }

    private String defineSuffix(SipMessage message) {
        String suffix = partition.define(message.getMillis());
        return message.isRegister() || message.isCseqRegister() ? "r_" + suffix : "c_" + suffix;
    }

    private Document convertToIndex(SipMessage message) {
        return new Document()
                .append(Field.millis, message.getMillis())
                .append(Field.src_ip, message.getSrcIp())
                .append(Field.src_port, message.getSrcPort())
                .append(Field.src_host, message.getSrcHost())
                .append(Field.dst_ip, message.getDstIp())
                .append(Field.dst_port, message.getDstPort())
                .append(Field.dst_host, message.getDstHost())
                .append(Field.call_id, message.getCallId())
                .append(Field.method, message.getMethod())
                .append(Field.caller, message.getCaller())
                .append(Field.callee, message.getCallee());
    }

    private Document convertToRaw(SipMessage message) {
        return convertToIndex(message)
                .append(Field.nanos, message.getNanos())
                .append(Field.payload, message.getPayload());
    }
}
