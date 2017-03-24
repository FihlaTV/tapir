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
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by agafox.
 */
public class HostsTest {

    private static final HostConfiguration CONFIGURATION = new HostConfiguration();

    static {
        HostConfiguration.Host host = new HostConfiguration.Host();
        host.setName("Test");
        host.setAddr(Arrays.asList("127.0.0.1/24", "198.0.0.1"));
        CONFIGURATION.getHosts().add(host);
    }

    private static final Hosts HOSTS = new Hosts(CONFIGURATION);

    @Test
    public void checkInAddr() {
        assertEquals("Test", HOSTS.resolve("198.0.0.1"));
    }

    @Test
    public void checkInCidr() {
        assertEquals("Test", HOSTS.resolve("127.0.0.1"));
        assertEquals("Test", HOSTS.resolve("127.0.0.127"));
        assertEquals("Test", HOSTS.resolve("127.0.0.254"));
    }

    @Test
    public void checkOutAddr() {
        assertEquals("198.0.1.1", HOSTS.resolve("198.0.1.1"));
    }
}
