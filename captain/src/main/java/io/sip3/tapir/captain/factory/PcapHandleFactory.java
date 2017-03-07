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

package io.sip3.tapir.captain.factory;

import org.pcap4j.core.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
@ConfigurationProperties(prefix = "pcap")
public class PcapHandleFactory {

    private String bpfFilter;

    private int spanLength;

    private int timeoutMillis;

    public PcapHandle online(String source) throws Exception {
        PcapNetworkInterface nic = Pcaps.getDevByName(source);
        if (nic == null) {
            throw new NotOpenException("Unknown network interface: " + source);
        }
        PcapHandle handle = nic
                .openLive(spanLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, timeoutMillis);
        return withBpfFilter(handle);
    }

    public PcapHandle offline(String source) throws Exception {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(source, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException e) {
            handle = Pcaps.openOffline(source);
        }
        return withBpfFilter(handle);
    }

    private PcapHandle withBpfFilter(PcapHandle handle) throws Exception {
        if (bpfFilter != null) {
            handle.setFilter(bpfFilter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }
        return handle;
    }

    public void setBpfFilter(String bpfFilter) {
        this.bpfFilter = bpfFilter;
    }

    public void setSpanLength(int spanLength) {
        this.spanLength = spanLength;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}
