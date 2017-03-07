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

package io.sip3.tapir.captain.factory;

import io.sip3.tapir.captain.handler.PacketDecoder;
import io.sip3.tapir.captain.handler.PacketDefragmentator;
import io.sip3.tapir.captain.handler.PacketSender;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
@ConfigurationProperties(prefix = "packet")
public class PacketHandlerFactory {

    private String host;

    private int port;

    private int delay;

    private int ttl;

    public PacketDecoder decoder(int order, int total) throws Exception {
        return new PacketDecoder(order, total);
    }

    public PacketDefragmentator defragmentator(int order, int total) throws Exception {
        return new PacketDefragmentator(order, total, ttl);
    }

    public PacketSender sender(int order, int total) throws Exception {
        return new PacketSender(order, total, host, port, delay);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
