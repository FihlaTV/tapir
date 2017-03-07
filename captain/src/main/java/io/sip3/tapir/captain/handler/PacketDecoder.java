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

package io.sip3.tapir.captain.handler;

import io.sip3.tapir.captain.model.PacketContainer;
import org.pcap4j.packet.*;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.packet.namednumber.IpNumber;

import java.util.function.Function;

/**
 * Created by agafox.
 */
public class PacketDecoder extends PacketHandler {

    public PacketDecoder(int order, int total) throws Exception {
        super(order, total);
    }

    @Override
    public void onEvent(PacketContainer container, long sequence, boolean endOfBatch) throws Exception {
        if (shouldSkipEvent(sequence)) {
            return;
        }

        DataLinkType dlt = container.getDlt();
        byte[] data = container.getRawPacket();

        Packet packet = PacketFactories.getFactory(Packet.class, DataLinkType.class)
                .newInstance(data, 0, data.length, dlt);

        // Don't use packet.get(IpV4Packet.class).
        // In case of 'IP in IP' packets it doesn't work properly.
        IpV4Packet ipV4Packet = extractIpV4Packet(packet,
                pkt -> pkt.getOuterOf(UdpPacket.class),
                pkt -> pkt.getOuterOf(TcpPacket.class),
                pkt -> pkt.getOuterOf(FragmentedPacket.class));

        if (ipV4Packet != null) {
            IpNumber protocol = ipV4Packet.getHeader().getProtocol();
            if (protocol.compareTo(IpNumber.UDP) == 0 || protocol.compareTo(IpNumber.TCP) == 0) {
                container.setIpV4Packet(ipV4Packet);
            }
        }
    }

    private IpV4Packet extractIpV4Packet(Packet packet, Function<Packet, Packet>... filters) {
        for (Function<Packet, Packet> filter : filters) {
            Packet p = filter.apply(packet);
            if (p != null && (p instanceof IpV4Packet)) {
                return (IpV4Packet) p;
            }
        }
        return null;
    }
}
