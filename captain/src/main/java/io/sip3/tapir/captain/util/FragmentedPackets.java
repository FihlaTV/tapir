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

package io.sip3.tapir.captain.util;

import org.pcap4j.packet.IpV4Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by agafox.
 */
public class FragmentedPackets {

    private final Map<Integer, IpV4Packet> packets = new TreeMap<>();

    private boolean isLastPacketReceived;

    public void addPacket(IpV4Packet p) {
        packets.put(8 * p.getHeader().getFragmentOffset(), p);
        isLastPacketReceived = !p.getHeader().getMoreFragmentFlag();
    }

    public boolean isAllPacketsReceived() {
        if (!isLastPacketReceived) {
            return false;
        }
        int expectedOffset = 0;
        for (Map.Entry<Integer, IpV4Packet> entry : packets.entrySet()) {
            int offset = entry.getKey();
            if (offset != expectedOffset) {
                return false;
            }
            IpV4Packet p = entry.getValue();
            expectedOffset = offset + p.getHeader().getTotalLength() - 4 * p.getHeader().getIhl();
        }
        return true;
    }

    public List<IpV4Packet> getPackets() {
        return new ArrayList<>(packets.values());
    }
}
