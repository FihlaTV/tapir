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

package io.sip3.tapir.salto.serializer;

import io.sip3.tapir.core.SipMessage;
import io.sip3.tapir.salto.Hosts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

import static io.sip3.tapir.core.util.ByteBufferUtil.*;

/**
 * Created by agafox.
 */
@Component
public class SipMessageSerializer implements Serializer<ByteBuffer, SipMessage> {

    @Autowired
    private Hosts hosts;

    @Override
    public SipMessage serialize(ByteBuffer buffer) throws Exception {
        int version = buffer.get();
        switch (version) {
            case 1:
                SipMessage message = new SipMessage();
                message.setMillis(buffer.getLong());
                message.setNanos(buffer.getInt());

                buffer.get(); // Skip 'IP Number'...

                String srcIp = getIPv4String(buffer);
                message.setSrcIp(srcIp);
                message.setSrcHost(hosts.resolve(srcIp));
                message.setSrcPort(getUnsignedShort(buffer));

                String dstIp = getIPv4String(buffer);
                message.setDstIp(dstIp);
                message.setDstHost(hosts.resolve(dstIp));
                message.setDstPort(getUnsignedShort(buffer));

                message.setPayload(getString(buffer, buffer.getShort()));
                return message.parse() ? message : null;
            default:
                throw new Exception("Unknown version: " + version);
        }
    }
}
