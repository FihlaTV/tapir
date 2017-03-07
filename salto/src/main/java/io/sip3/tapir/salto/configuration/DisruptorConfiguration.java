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

package io.sip3.tapir.salto.configuration;

import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.sip3.tapir.salto.handler.ByteBufferEventHandler;
import io.sip3.tapir.salto.model.ByteBufferContainer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.IntStream;

/**
 * Created by agafox.
 */
@Configuration
public class DisruptorConfiguration {

    @Bean
    public Disruptor disruptor(@Value("${disruptor.capacity}") int capacity,
                               @Value("${disruptor.consumers}") int consumers,
                               ObjectFactory<ByteBufferEventHandler> factory) {
        Disruptor<ByteBufferContainer> disruptor = new Disruptor<>(ByteBufferContainer::new,
                capacity,
                DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE,
                new SleepingWaitStrategy());

        disruptor.handleEventsWith(IntStream.range(0, consumers)
                .mapToObj(i -> {
                    ByteBufferEventHandler handler = factory.getObject();
                    handler.setOrder(i);
                    handler.setTotal(consumers);
                    return handler;
                }).toArray(ByteBufferEventHandler[]::new));

        return disruptor;
    }
}
