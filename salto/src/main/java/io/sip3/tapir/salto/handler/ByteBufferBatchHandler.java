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

package io.sip3.tapir.salto.handler;

import com.lmax.disruptor.WorkHandler;
import io.sip3.tapir.salto.customizer.Customizer;
import io.sip3.tapir.salto.model.ByteBufferContainer;
import io.sip3.tapir.salto.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by agafox.
 */
public abstract class ByteBufferBatchHandler<T> implements WorkHandler<ByteBufferContainer> {

    private static final Logger logger = LoggerFactory.getLogger(ByteBufferBatchHandler.class);

    @Value("${disruptor.batchSize}")
    private int batchSize;

    @Autowired
    private Serializer<ByteBuffer, T> serializer;

    @Autowired(required = false)
    private Customizer<T> customizer;

    @Autowired(required = false)
    private List<WorkHandler<List<T>>> handlers = new ArrayList<>();

    private final List<T> entities = new ArrayList<>();

    public abstract int getType();

    @Override
    public void onEvent(ByteBufferContainer container) throws Exception {
        if (container.shouldIgnoreContent()) {
            handle();
            return;
        }
        T entity = serializer.serialize(container.getBuffer());
        if (entity == null) {
            return;
        }
        if (customizer != null) {
            customizer.customize(entity);
        }
        entities.add(entity);
        if (entities.size() >= batchSize) {
            handle();
        }
    }

    private void handle() {
        handlers.forEach(handler -> {
            try {
                handler.onEvent(entities);
            } catch (Exception e) {
                logger.error("Got exception...", e);
            }
        });
        entities.clear();
    }
}
