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

import io.sip3.tapir.captain.stream.PcapStream;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by agafox.
 * <p>
 * Integration test...
 * As long as Tapir Captain functionality is too small...
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest
public class CaptainTest {

    //******************** Spring Context ****************************//
    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();

    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();
    //***************************************************************//

    private static DatagramChannel receiver;

    @Autowired
    private ResourceLoader loader;

    @Autowired
    private PcapStreamFactory factory;

    @BeforeClass
    public static void init() throws Exception {
        receiver = DatagramChannel.open()
                .bind(new InetSocketAddress("127.0.0.1", 15060));
        receiver.configureBlocking(true);
    }

    @Test
    @Parameters
    public void compare(String input, String[] outputs) throws Exception {
        try (PcapStream stream = factory.file(loader.getResource(input).getFile().getAbsolutePath())) {
            stream.open();
        }
        ByteBuffer received = ByteBuffer.allocate(65535);
        ByteBuffer expected = ByteBuffer.allocate(65535);
        for (String output : outputs) {
            // receive
            received.clear();
            try {
                CompletableFuture.runAsync(() -> {
                    try {
                        receiver.receive(received);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                // Do nothing...
            } finally {
                assertTrue("Missed output: " + output, received.position() > 0);
            }
            // read
            expected.clear();
            expected.put(Files.readAllBytes(loader.getResource(output).getFile().toPath()));
            // assert
            assertArrayEquals(expected.array(), received.array());
        }
    }

    @AfterClass
    public static void close() throws Exception {
        if (receiver != null) {
            receiver.close();
        }
    }

    public Object[] parametersForCompare() {
        return $(
                $("invite-simple/in.pcap", new String[]{"invite-simple/out.bin"}),
                $("register-2in1/in.pcap", new String[]{"register-2in1/out1.bin", "register-2in1/out2.bin"})
        );
    }
}
