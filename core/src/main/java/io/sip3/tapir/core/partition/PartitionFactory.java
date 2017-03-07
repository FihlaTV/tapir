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

import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agafox.
 */
public class PartitionFactory {

    private static Pattern REGEX = Pattern.compile("(\\d{1,2})([m|h|d])");

    public static Partition ofPattern(String pattern) {
        Matcher m = REGEX.matcher(pattern);
        if (m.matches()) {
            DateTimeFormatter formatter = null;
            long duration = Long.parseLong(m.group(1));
            switch (m.group(2)) {
                case "m":
                    formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                    duration = java.util.concurrent.TimeUnit.MINUTES.toMillis(duration);
                    break;
                case "h":
                    formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
                    duration = java.util.concurrent.TimeUnit.HOURS.toMillis(duration);
                    break;
                case "d":
                    formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    duration = java.util.concurrent.TimeUnit.DAYS.toMillis(duration);
                    break;
            }
            return new Partition(formatter, duration);
        }
        throw new RuntimeException("Pattern mismatch - regex: " + REGEX + ", pattern: " + pattern);
    }
}
