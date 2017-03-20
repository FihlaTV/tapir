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

import io.sip3.tapir.salto.model.ByteBufferContainer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static io.sip3.tapir.core.util.ByteBufferUtil.getIPv4String;
import static io.sip3.tapir.core.util.ByteBufferUtil.getUnsignedShort;
import static org.junit.Assert.assertEquals;

/**
 * Created by agafox.
 */
public class ByteBufferHepHandlerTest {

    private static final byte[] HEP_MESSAGE = {
            0x48, 0x45, 0x50, 0x33, 0x00, 0x71,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x07, 0x02,
            0x00, 0x00, 0x00, 0x02, 0x00, 0x07, 0x11,
            0x00, 0x00, 0x00, 0x03, 0x00, 0x0a, (byte) 0xd4, (byte) 0xca, 0x00, 0x01,
            0x00, 0x00, 0x00, 0x04, 0x00, 0x0a, 0x52, 0x74, 0x00, (byte) 0xd3,
            0x00, 0x00, 0x00, 0x07, 0x00, 0x08, 0x2e, (byte) 0xea,
            0x00, 0x00, 0x00, 0x08, 0x00, 0x08, 0x13, (byte) 0xc4,
            0x00, 0x00, 0x00, 0x09, 0x00, 0x0a, 0x4e, 0x49, (byte) 0x82, (byte) 0xcb,
            0x00, 0x00, 0x00, 0x0a, 0x00, 0x0a, 0x00, 0x01, (byte) 0xe2, (byte) 0x40,
            0x00, 0x00, 0x00, 0x0b, 0x00, 0x07, 0x01,
            0x00, 0x00, 0x00, 0x0c, 0x00, 0x0a, 0x00, 0x00, 0x00, (byte) 0xe4,
            0x00, 0x00, 0x00, 0x0f, 0x00, 0x14, 0x49, 0x4e, 0x56, 0x49, 0x54, 0x45, 0x20, 0x73, 0x69, 0x70, 0x3a, 0x62, 0x6f, 0x62,
    };

    @Test
    public void checkOnEvent() throws Exception {
        ByteBufferContainer container = new ByteBufferContainer();

        ByteBuffer buffer = container.getBuffer();
        buffer.put(HEP_MESSAGE);
        buffer.flip();

        new ByteBufferHepHandler().onEvent(container);

        assertEquals(0, buffer.get());                       // type
        assertEquals(1, buffer.get());                       // version
        assertEquals(1313440459123l, buffer.getLong());      // millis
        assertEquals(456, buffer.getInt());                  // nanos
        assertEquals(17, buffer.get());                      // IP number
        assertEquals("212.202.0.1", getIPv4String(buffer));  // source IP
        assertEquals(12010, getUnsignedShort(buffer));       // source port
        assertEquals("82.116.0.211", getIPv4String(buffer)); // destination IP
        assertEquals(5060, getUnsignedShort(buffer));        // destination port
        short length = buffer.getShort();
        assertEquals(14, length);                            // length
        byte[] payload = new byte[length];
        buffer.get(payload);
        assertEquals("INVITE sip:bob", new String(payload)); // payload
    }
}
