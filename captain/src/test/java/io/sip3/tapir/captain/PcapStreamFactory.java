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

package io.sip3.tapir.captain;

import com.lmax.disruptor.dsl.Disruptor;
import io.sip3.tapir.captain.factory.PcapHandleFactory;
import io.sip3.tapir.captain.stream.FilePcapStream;
import io.sip3.tapir.captain.stream.PcapStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by agafox.
 */
@Component
public class PcapStreamFactory {

    @Autowired
    private Disruptor disruptor;

    @Autowired
    private PcapHandleFactory handleFactory;

    @PostConstruct
    public void init() {
        disruptor.start();
    }

    public PcapStream file(String file) throws Exception {
        return new FilePcapStream(file, disruptor, handleFactory, false);
    }

    @PreDestroy
    public void close() {
        disruptor.shutdown();
    }
}
