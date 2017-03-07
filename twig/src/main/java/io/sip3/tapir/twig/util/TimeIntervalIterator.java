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

package io.sip3.tapir.twig.util;

import io.sip3.tapir.core.partition.Partition;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by agafox.
 */
public class TimeIntervalIterator implements Iterator<Long> {

    private long position;

    private long limit;

    private long duration;

    private TimeIntervalIterator(long[] millis, Partition partition) {
        this.position = partition.truncate(millis[0]);
        this.limit = partition.truncate(millis[1]) + partition.duration();
        this.duration = partition.duration();
    }

    public static TimeIntervalIterator of(long[] millis, Partition partition) {
        return new TimeIntervalIterator(millis, partition);
    }

    @Override
    public boolean hasNext() {
        return position < limit;
    }

    @Override
    public Long next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        long millis = position;
        position += duration;
        return millis;
    }
}
