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

package io.sip3.tapir.captain.stream;

import com.lmax.disruptor.dsl.Disruptor;
import io.sip3.tapir.captain.factory.PcapHandleFactory;
import org.pcap4j.core.PcapHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
@ConditionalOnProperty("pcap.file")
@ConditionalOnMissingBean({DevicePcapStream.class, DirectoryPcapStream.class})
public class FilePcapStream implements PcapStream {

    private static final Logger logger = LoggerFactory.getLogger(FilePcapStream.class);

    private final Disruptor disruptor;

    private final PcapHandle handle;

    private final boolean isAutowired;

    @Autowired
    public FilePcapStream(@Value("${pcap.file}") String file, Disruptor disruptor, PcapHandleFactory handleFactory) throws Exception {
        this(file, disruptor, handleFactory, true);
    }

    public FilePcapStream(String file, Disruptor disruptor, PcapHandleFactory handleFactory, boolean isAutowired) throws Exception {
        this.disruptor = disruptor;
        this.isAutowired = isAutowired;
        this.handle = handleFactory.offline(file);
        logger.info("Reading: {}", file);
    }

    @Override
    public void open() throws Exception {
        if (isAutowired) {
            disruptor.start();
        }
        loop(disruptor, handle);
    }

    @Override
    public void close() throws Exception {
        handle.breakLoop();
        if (isAutowired) {
            disruptor.shutdown();
        }
    }
}
