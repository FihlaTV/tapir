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

package io.sip3.tapir.salto;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import io.sip3.tapir.salto.model.ByteBufferContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.TimeUnit;

/**
 * Created by agafox.
 */
@Component
public class Receiver implements AutoCloseable {

    protected static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private final int port;

    private final Disruptor<ByteBufferContainer> disruptor;

    private final RingBuffer<ByteBufferContainer> ringBuffer;

    private boolean isAlive = true;

    @Autowired
    public Receiver(@Value("${udp.port}") int port, Disruptor<ByteBufferContainer> disruptor) {
        this.port = port;
        this.disruptor = disruptor;
        this.ringBuffer = disruptor.getRingBuffer();
    }

    @PostConstruct
    public void init() {
        disruptor.start();
    }

    public void open() {
        SocketAddress address = new InetSocketAddress(port);
        try (
                DatagramChannel channel = DatagramChannel.open()
                        .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                        .bind(address)
        ) {
            channel.configureBlocking(true);
            logger.info("Listening: {}", port);
            while (isAlive) {
                long sequence = ringBuffer.next();
                ByteBufferContainer container = ringBuffer.get(sequence);
                container.clear();
                channel.receive(container.getBuffer());
                container.flip();
                ringBuffer.publish(sequence);
            }
        } catch (Exception e) {
            logger.error("Got exception...", e);
        }
    }

    @Override
    public void close() throws Exception {
        isAlive = false;
        ringBuffer.tryPublishEvent((container, sequence) -> container.clear());
        try {
            disruptor.shutdown(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            logger.warn("Disruptor shutdown timeout....", e);
            disruptor.halt();
        }
    }
}
