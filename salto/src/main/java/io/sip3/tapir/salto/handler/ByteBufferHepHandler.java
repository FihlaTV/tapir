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

package io.sip3.tapir.salto.handler;

import com.lmax.disruptor.WorkHandler;
import io.sip3.tapir.salto.model.ByteBufferContainer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

import static io.sip3.tapir.core.util.ByteBufferUtil.*;

/**
 * Created by agafox.
 */
@Component
public class ByteBufferHepHandler implements WorkHandler<ByteBufferContainer> {

    private static final int HEP_ID = 0x48455033;

    private static final int HEP_HEADER_LENGTH = 6;

    private static final byte HEP_SIP_TYPE = 1;

    private static final byte TAPIR_SIP_TYPE = 0;

    private static final byte TAPIR_SIP_VERSION = 1;

    @Override
    public void onEvent(ByteBufferContainer container) throws Exception {
        ByteBuffer buffer = container.getBuffer();

        if (buffer.remaining() < HEP_HEADER_LENGTH
                || buffer.getInt(0) != HEP_ID) {
            return;
        }

        byte protocolType = -1;
        byte mark = 0;

        long millis = 0;
        int nanos = 0;
        byte ipProtocolId = 0;
        int srcIp = 0;
        int srcPort = 0;
        int dstIp = 0;
        int dstPort = 0;
        byte[] payload = new byte[0];

        shift(buffer, HEP_HEADER_LENGTH);
        while (shift(buffer, 2)) {
            int type = buffer.getShort();
            int length = buffer.getShort() - 6;
            switch (type) {
                case 2:
                    ipProtocolId = buffer.get();
                    mark++;
                    break;
                case 3:
                    srcIp = buffer.getInt();
                    mark++;
                    break;
                case 4:
                    dstIp = buffer.getInt();
                    mark++;
                    break;
                case 7:
                    srcPort = getUnsignedShort(buffer);
                    mark++;
                    break;
                case 8:
                    dstPort = getUnsignedShort(buffer);
                    mark++;
                    break;
                case 9:
                    long seconds = getUnsignedInt(buffer);
                    millis = seconds * 1000;
                    mark++;
                    break;
                case 10:
                    long uSeconds = getUnsignedInt(buffer);
                    millis = millis + uSeconds / 1000;
                    nanos = (int) (uSeconds % 1000);
                    mark++;
                    break;
                case 11:
                    protocolType = buffer.get();
                    break;
                case 15:
                    payload = new byte[length];
                    buffer.get(payload);
                    mark++;
                    break;
                default:
                    shift(buffer, length);
                    break;
            }
        }

        if (mark < 8) {
            container.clear();
            return;
        }

        // It is not necessary to do code decomposition as long as Tapir Salto works only with SIP messages...
        switch (protocolType) {
            case HEP_SIP_TYPE:
                container.clear();
                buffer.put(TAPIR_SIP_TYPE);
                buffer.put(TAPIR_SIP_VERSION);
                buffer.putLong(millis);
                buffer.putInt(nanos);
                buffer.put(ipProtocolId);
                buffer.putInt(srcIp);
                buffer.putShort((short) srcPort);
                buffer.putInt(dstIp);
                buffer.putShort((short) dstPort);
                buffer.putShort((short) payload.length);
                buffer.put(payload);
                container.flip();
                break;
            default:
                container.clear();
                break;
        }
    }
}
