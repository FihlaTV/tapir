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

package org.pcap4j.packet.factory;

import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.IpNumber;

/**
 * Created by agafox.
 * <p>
 * Tapir Captain defines it's own StaticIpNumberPacketFactory to handle 'IP in IP' packets.
 * 'IP in IP' is common format used in network interface mirroring.
 */
public class StaticIpNumberPacketFactory extends AbstractStaticPacketFactory<IpNumber> {

    private static final StaticIpNumberPacketFactory INSTANCE
            = new StaticIpNumberPacketFactory();

    private StaticIpNumberPacketFactory() {
        instantiaters.put(
                IpNumber.UDP, new PacketInstantiater() {
                    @Override
                    public Packet newInstance(
                            byte[] rawData, int offset, int length
                    ) throws IllegalRawDataException {
                        return UdpPacket.newPacket(rawData, offset, length);
                    }

                    @Override
                    public Class<UdpPacket> getTargetClass() {
                        return UdpPacket.class;
                    }
                }
        );
        instantiaters.put(
                IpNumber.TCP, new PacketInstantiater() {
                    @Override
                    public Packet newInstance(
                            byte[] rawData, int offset, int length
                    ) throws IllegalRawDataException {
                        return TcpPacket.newPacket(rawData, offset, length);
                    }

                    @Override
                    public Class<TcpPacket> getTargetClass() {
                        return TcpPacket.class;
                    }
                }
        );
        instantiaters.put(
                IpNumber.IPV4, new PacketInstantiater() {
                    @Override
                    public Packet newInstance(
                            byte[] rawData, int offset, int length
                    ) throws IllegalRawDataException {
                        return IpV4Packet.newPacket(rawData, offset, length);
                    }

                    @Override
                    public Class<IpV4Packet> getTargetClass() {
                        return IpV4Packet.class;
                    }
                }
        );
    }

    public static StaticIpNumberPacketFactory getInstance() {
        return INSTANCE;
    }
}