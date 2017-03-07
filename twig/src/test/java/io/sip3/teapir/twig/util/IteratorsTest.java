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

import io.sip3.tapir.twig.util.Iterators;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by agafox.
 */
public class IteratorsTest {

    private final static Comparator<Integer> COMPARATOR = Integer::compareTo;

    @Test
    public void checkNonEmptyIterators() {
        Iterator<Integer> i1 = Arrays.asList(
                1, 3, 4
        ).iterator();
        Iterator<Integer> i2 = Arrays.asList(
                2, 5
        ).iterator();
        Iterator<Integer> expected = Arrays.asList(
                1, 2, 3, 4, 5
        ).iterator();
        assertTrue(Iterators.compare(expected, Iterators.merge(i1, i2, COMPARATOR), COMPARATOR));
    }

    @Test
    public void checkEmptyIterators() {
        Iterator<Integer> empty = Collections.emptyIterator();
        assertTrue(Iterators.compare(empty, Iterators.merge(empty, empty, COMPARATOR), COMPARATOR));
    }

    @Test
    public void checkNonEmptyAndEmptyIterators() {
        List<Integer> expected = Arrays.asList(
                1, 2, 3, 4
        );
        Iterator<Integer> empty = Collections.emptyIterator();
        assertTrue(Iterators.compare(expected.iterator(), Iterators.merge(expected.iterator(), empty, COMPARATOR), COMPARATOR));
        assertTrue(Iterators.compare(expected.iterator(), Iterators.merge(empty, expected.iterator(), COMPARATOR), COMPARATOR));
    }
}
