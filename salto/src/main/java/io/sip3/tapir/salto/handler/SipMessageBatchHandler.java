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

import io.sip3.tapir.core.SipMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SipMessageBatchHandler extends BatchHandler<SipMessage> {

    @Value("${mongo.batchSize}")
    private int batchSize;

    @Override
    public int getType() {
        return 0;
    }

    @Override
    protected int getBatchSize() {
        return batchSize;
    }
}
