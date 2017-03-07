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

import com.lmax.disruptor.ExceptionHandler;
import io.sip3.tapir.captain.model.PacketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agafox.
 */
public class PacketExceptionHandler implements ExceptionHandler<PacketContainer> {

    private static final Logger logger = LoggerFactory.getLogger(PacketExceptionHandler.class);

    @Override
    public void handleEventException(Throwable ex, long sequence, PacketContainer container) {
        logger.error("Got exception...", ex);
        container.clear();
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        logger.error("Got exception...", ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        logger.error("Got exception...", ex);
    }
}
