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

package io.sip3.tapir.twig.mongo.codec;

import com.mongodb.MongoClient;
import io.sip3.tapir.core.SipMessage;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
public class SipMessageCodec implements Codec<SipMessage> {

    private final Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class);

    @Override
    public Class<SipMessage> getEncoderClass() {
        return SipMessage.class;
    }

    @Override
    public SipMessage decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = codec.decode(reader, decoderContext);
        SipMessage message = new SipMessage();
        message.setId(document.getObjectId("_id").toHexString());
        message.setMillis(document.getLong("millis"));
        Object nanos = document.get("nanos");
        if (nanos != null) {
            message.setNanos((Integer) nanos);
        }
        String srcIp = document.getString("src_ip");
        message.setSrcIp(srcIp);
        message.setSrcPort(document.getInteger("src_port"));
        message.setSrcHost(document.getString("src_host"));
        String dstIp = document.getString("dst_ip");
        message.setDstIp(dstIp);
        message.setDstPort(document.getInteger("dst_port"));
        message.setDstHost(document.getString("dst_host"));
        message.setMethod(document.getString("method"));
        message.setCallId(document.getString("call_id"));
        message.setCaller(document.getString("caller"));
        message.setCallee(document.getString("callee"));
        Object payload = document.get("payload");
        if (payload != null) {
            message.setPayload((String) payload);
        }
        return message;
    }

    @Override
    public void encode(BsonWriter writer, SipMessage sipMessage, EncoderContext encoderContext) {
        throw new UnsupportedOperationException();
    }
}
