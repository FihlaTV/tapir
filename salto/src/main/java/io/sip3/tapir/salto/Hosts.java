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

import io.sip3.tapir.salto.configuration.HostConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by agafox.
 */
@Component
public class Hosts {

    @Autowired(required = false)
    public HostConfiguration configuration;

    private final Map<String, String> hosts = new HashMap<>();

    @PostConstruct
    public void init() {
        if (configuration != null) {
            configuration.getHosts().forEach(host -> {
                String name = host.getName();
                host.getAddr().forEach(addr -> hosts.put(addr, name));
            });
        }
    }

    public String resolve(String addr) {
        return hosts.getOrDefault(addr, addr);
    }
}
