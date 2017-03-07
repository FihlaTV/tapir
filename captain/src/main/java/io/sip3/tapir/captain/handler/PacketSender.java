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

import com.lmax.disruptor.LifecycleAware;
import io.sip3.tapir.captain.model.PacketContainer;
import io.sip3.tapir.captain.util.SipContentReader;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * Created by agafox.
 */
public class PacketSender extends PacketHandler implements LifecycleAware {

    private static final byte HEADER_LENGTH = 27;

    private static final byte TYPE = 0;

    private static final byte VERSION = 1;

    private final DatagramChannel channel;

    private final int delay;

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(65535);

    public PacketSender(int order, int total, String host, int port, int delay) throws Exception {
        super(order, total);
        this.channel = DatagramChannel.open()
                .connect(new InetSocketAddress(host, port));
        this.delay = delay;
    }

    @Override
    public void onStart() {
        // Do nothing...
    }

    @Override
    public void onEvent(PacketContainer container, long sequence, boolean endOfBatch) throws Exception {
        if (shouldSkipEvent(sequence)) {
            return;
        }
        IpV4Packet ipV4Packet = container.getIpV4Packet();
        if (ipV4Packet == null) {
            return;
        }
        buffer.clear();
        buffer.put(TYPE);
        buffer.put(VERSION);

        Timestamp timestamp = container.getTimestamp();
        buffer.putLong(timestamp.getTime());
        buffer.putInt(timestamp.getNanos());

        IpV4Packet.IpV4Header ipV4Header = ipV4Packet.getHeader();
        buffer.put(ipV4Header.getProtocol().value());

        Packet transportLayerPacket = ipV4Packet.getPayload();
        if (transportLayerPacket.getPayload() == null) {
            return;
        }

        if (transportLayerPacket instanceof UdpPacket) {
            UdpPacket.UdpHeader udpHeader = (UdpPacket.UdpHeader) transportLayerPacket.getHeader();
            buffer.put(ipV4Header.getSrcAddr().getAddress());
            buffer.putShort(udpHeader.getSrcPort().value());
            buffer.put(ipV4Header.getDstAddr().getAddress());
            buffer.putShort(udpHeader.getDstPort().value());
        } else {
            TcpPacket.TcpHeader tcpHeader = (TcpPacket.TcpHeader) transportLayerPacket.getHeader();
            buffer.put(ipV4Header.getSrcAddr().getAddress());
            buffer.putShort(tcpHeader.getSrcPort().value());
            buffer.put(ipV4Header.getDstAddr().getAddress());
            buffer.putShort(tcpHeader.getDstPort().value());
        }

        byte[] content = transportLayerPacket.getPayload().getRawData();
        SipContentReader.read(content, (i1, i2) -> {
            buffer.position(HEADER_LENGTH);
            buffer.limit(buffer.capacity());
            int length = i2 - i1;
            buffer.putShort((short) length);
            buffer.put(content, i1, length);
            buffer.flip();
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
                channel.write(buffer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onShutdown() {
        try {
            channel.close();
        } catch (IOException e) {
            // Do nothing...
        }
    }
}
