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

package io.sip3.tapir.core.partition;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by agafox.
 */
@RunWith(JUnitParamsRunner.class)
public class PartitionTest {

    @Test
    @Parameters({
            "5m, 300000",
            "1h, 3600000",
            "1d, 86400000",

    })
    public void checkDuration(String pattern, long duration) {
        Partition partition = PartitionFactory.ofPattern(pattern);
        assertEquals(duration, partition.duration());
    }

    @Test
    @Parameters({
            "5m, 1488481075000, 1488480900000",
            "1h, 1488481075000, 1488477600000",
            "1d, 1488481075000, 1488412800000",

    })
    public void checkTruncate(String pattern, long given, long expected) {
        Partition partition = PartitionFactory.ofPattern(pattern);
        assertEquals(expected, partition.truncate(given));
    }

    @Test
    @Parameters({
            "5m, 1488481075000, 201703021855",
            "1h, 1488481075000, 2017030218",
            "1d, 1488481075000, 20170302",

    })
    public void checkDefine(String pattern, long given, String expected) {
        Partition partition = PartitionFactory.ofPattern(pattern);
        assertEquals(expected, partition.define(given));
    }
}
