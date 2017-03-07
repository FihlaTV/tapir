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
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
public class HealthCheck {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    private static final long _1_MINUTE = 60 * 1000;

    private final RingBuffer ringBuffer;

    @Autowired
    public HealthCheck(Disruptor disruptor) {
        this.ringBuffer = disruptor.getRingBuffer();
    }

    @Scheduled(initialDelay = _1_MINUTE, fixedDelay = _1_MINUTE)
    public void healthCheck() {
        logger.info("Tapir Salto is running... Remaining capacity: '{}'", ringBuffer.remainingCapacity());
    }
}