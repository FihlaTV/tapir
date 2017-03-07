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

package io.sip3.tapir.captain.configuration;

import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.sip3.tapir.captain.factory.PacketHandlerFactory;
import io.sip3.tapir.captain.handler.PacketExceptionHandler;
import io.sip3.tapir.captain.handler.PacketHandler;
import io.sip3.tapir.captain.model.PacketContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * Created by agafox.
 */
@Component
public class DisruptorConfiguration {

    @Autowired
    private PacketHandlerFactory factory;

    @Bean
    public Disruptor disruptor(@Value("${disruptor.capacity}") int capacity,
                               @Value("${disruptor.consumers}") int consumers) {
        Disruptor<PacketContainer> disruptor = new Disruptor<>(PacketContainer::new,
                capacity,
                DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE,
                new SleepingWaitStrategy());

        disruptor.setDefaultExceptionHandler(new PacketExceptionHandler());

        disruptor.handleEventsWith(handlers(consumers, factory::decoder))
                .then(handlers(consumers, factory::defragmentator))
                .then(handlers(consumers, factory::sender));

        return disruptor;
    }

    private PacketHandler[] handlers(int total, Factory factory) {
        return IntStream.range(0, total)
                .mapToObj(i -> {
                    try {
                        return factory.create(i, total);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(PacketHandler[]::new);
    }

    @FunctionalInterface
    private interface Factory {

        PacketHandler create(int order, int total) throws Exception;
    }
}
