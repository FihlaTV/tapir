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

package io.sip3.tapir.core.util;

import io.sip3.tapir.core.SipMessage;
import io.pkts.PcapOutputStream;
import io.pkts.buffer.Buffers;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.PacketFactory;
import io.pkts.packet.TransportPacketFactory;
import io.pkts.packet.UDPPacket;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * Created by agafox.
 */
public class PcapFormatter {

    private static final TransportPacketFactory FACTORY = PacketFactory.getInstance().getTransportFactory();

    public static ByteArrayOutputStream format(Iterator<SipMessage> iterator) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PcapGlobalHeader header = PcapGlobalHeader.createDefaultHeader();
        try (PcapOutputStream pos = PcapOutputStream.create(header, os)) {
            while (iterator.hasNext()) {
                SipMessage message = iterator.next();
                if (message.getPayload() == null) {
                    continue;
                }
                UDPPacket packet = FACTORY.createUDP(message.getMillis(), Buffers.wrap(message.getPayload().getBytes()));
                packet.setDestinationIP(message.getDstIp());
                packet.setSourceIP(message.getSrcIp());
                packet.setDestinationPort(message.getDstPort());
                packet.setSourcePort(message.getSrcPort());
                packet.reCalculateChecksum();
                pos.write(packet);
            }
        }
        return os;
    }
}
