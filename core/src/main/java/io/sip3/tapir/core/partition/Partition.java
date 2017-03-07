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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Created by agafox.
 */
public class Partition {

    private final DateTimeFormatter formatter;

    private final long duration;

    public Partition(DateTimeFormatter formatter, long duration) {
        this.formatter = formatter;
        this.duration = duration;
    }

    public long duration() {
        return duration;
    }

    public long truncate(long millis) {
        return (millis / duration) * duration;
    }

    public String define(long millis) {
        return define(millis, true);
    }

    public String define(long millis, boolean truncate) {
        Instant i = Instant.ofEpochMilli(truncate ? truncate(millis) : millis);
        return formatter.format(LocalDateTime.ofInstant(i, ZoneOffset.UTC));
    }
}
