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

import com.lmax.disruptor.EventHandler;
import io.sip3.tapir.salto.model.ByteBufferContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;

/**
 * Created by agafox.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ByteBufferEventHandler implements EventHandler<ByteBufferContainer> {

    private static final Logger logger = LoggerFactory.getLogger(ByteBufferEventHandler.class);

    @Autowired
    private List<BatchHandler> handlers;

    private int order;

    private int total;

    public void setOrder(int order) {
        this.order = order;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @PostConstruct
    public void order() {
        handlers.sort(Comparator.comparingInt(BatchHandler::getType));
    }

    @Override
    public void onEvent(ByteBufferContainer container, long sequence, boolean endOfBatch) throws Exception {
        if (container.shouldHandleImmediately()) {
            handlers.forEach(handler -> {
                try {
                    handler.onEvent(container);
                } catch (Exception e) {
                    logger.error("Got exception...", e);
                }
            });
            return;
        }
        if ((sequence % total) != order) {
            return;
        }
        ByteBuffer buffer = container.getBuffer();
        int type = buffer.get();
        try {
            handlers.get(type).onEvent(container);
        } catch (IndexOutOfBoundsException e) {
            logger.error("Unknown protocol type:" + type);
        } catch (Exception e) {
            logger.error("Got exception...", e);
        }
    }
}
