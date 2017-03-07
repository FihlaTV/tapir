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

package io.sip3.tapir.captain.model;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.sql.Timestamp;

/**
 * Created by agafox.
 */
public class PacketContainer {

    private DataLinkType dlt;

    private Timestamp timestamp;

    private byte[] rawPacket;

    private IpV4Packet ipV4Packet;

    public void clear() {
        this.dlt = null;
        this.timestamp = null;
        this.rawPacket = null;
        this.ipV4Packet = null;
    }

    public DataLinkType getDlt() {
        return dlt;
    }

    public void setDlt(DataLinkType dlt) {
        this.dlt = dlt;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getRawPacket() {
        return rawPacket;
    }

    public void setRawPacket(byte[] rawPacket) {
        this.rawPacket = rawPacket;
    }

    public IpV4Packet getIpV4Packet() {
        return ipV4Packet;
    }

    public void setIpV4Packet(IpV4Packet ipV4Packet) {
        this.ipV4Packet = ipV4Packet;
    }
}
