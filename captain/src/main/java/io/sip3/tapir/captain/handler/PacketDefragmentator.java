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
import io.sip3.tapir.captain.util.FragmentedPackets;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.util.IpV4Helper;

import java.util.Map;

/**
 * Created by agafox.
 */
public class PacketDefragmentator extends PacketHandler {

    private final int ttl;

    private final Map<Integer, FragmentedPackets> accumulator;

    private long nextCheck;

    public PacketDefragmentator(int order, int total, int ttl) throws Exception {
        super(order, total);
        this.ttl = ttl;
        this.accumulator = new PassiveExpiringMap<>(ttl);
    }

    @Override
    public void onEvent(PacketContainer container, long sequence, boolean endOfBatch) throws Exception {
        IpV4Packet ipV4Packet = container.getIpV4Packet();
        if (ipV4Packet == null) {
            return;
        }

        IpV4Packet.IpV4Header ipV4Header = ipV4Packet.getHeader();
        if (!ipV4Header.getMoreFragmentFlag() && ipV4Header.getFragmentOffset() == 0) {
            return;
        }

        int identification = ipV4Header.getIdentificationAsInt();
        if (shouldSkipEvent(identification, order, total)) {
            return;
        }

        // It seems to be the best place to call 'checkExpired()'...
        checkExpired();

        FragmentedPackets fragmentedPackets = accumulator.computeIfAbsent(identification, i -> new FragmentedPackets());
        fragmentedPackets.addPacket(ipV4Packet);
        if (fragmentedPackets.isAllPacketsReceived()) {
            try {
                container.setIpV4Packet(IpV4Helper.defragment(fragmentedPackets.getPackets()));
            } finally {
                accumulator.remove(identification);
            }
        } else {
            container.setIpV4Packet(null);
        }
    }

    private void checkExpired() {
        long now = System.currentTimeMillis();
        if (nextCheck < now) {
            accumulator.size();
            nextCheck = now + ttl;
        }
    }
}
