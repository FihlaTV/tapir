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

package io.sip3.teapir.twig.util;

import io.sip3.tapir.core.partition.Partition;
import io.sip3.tapir.core.partition.PartitionFactory;
import io.sip3.tapir.twig.util.Iterators;
import io.sip3.tapir.twig.util.TimeIntervalIterator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertTrue;

/**
 * Created by agafox.
 */
@RunWith(JUnitParamsRunner.class)
public class TimeIntervalIteratorTest {

    private final static Partition _1H = PartitionFactory.ofPattern("1h");

    private final static Comparator<Long> COMPARATOR = Long::compareTo;

    @Test
    @Parameters
    public void checkTimeIntervalIterator(long start, long end, Iterator<Long> expected) {
        TimeIntervalIterator i = TimeIntervalIterator.of(new long[]{start, end}, _1H);
        assertTrue(Iterators.compare(expected, i, COMPARATOR));
    }

    public Object[] parametersForCheckTimeIntervalIterator() {
        return $(
                // Params 1: start.compare(end) > 0
                $(1488484390000l,
                        1488495640000l,
                        Arrays.asList(
                                1488481200000l,
                                1488484800000l,
                                1488488400000l,
                                1488492000000l,
                                1488495600000l
                        ).iterator()),
                // Params 2: start.compare(end) == 0
                $(1488484390000l,
                        1488484390000l,
                        Arrays.asList(
                                1488484390000l
                        ).iterator()),
                // Params 3: start.compare(end) < 0
                $(1488495640000l,
                        1488484390000l,
                        Collections.emptyIterator())
        );
    }
}
