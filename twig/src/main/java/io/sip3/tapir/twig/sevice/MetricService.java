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

package io.sip3.tapir.twig.sevice;

import io.sip3.tapir.core.partition.Partition;
import io.sip3.tapir.core.partition.PartitionFactory;
import io.sip3.tapir.twig.model.Metric;
import io.sip3.tapir.twig.model.Prefix;
import io.sip3.tapir.twig.redis.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
public class MetricService {

    private static final int MAX_DEPTH = 10;

    private final Partition partition;

    private final int offset;

    private final Redis redis;

    @Autowired
    public MetricService(@Value("${statistic.partition}") String partition, @Value("${statistic.offset}") int offset, Redis redis) {
        this.partition = PartitionFactory.ofPattern(partition);
        this.offset = offset;
        this.redis = redis;
    }

    public Metric get4xx() {
        return getMetricByName(Prefix._4xx);
    }

    public Metric get5xx() {
        return getMetricByName(Prefix._5xx);
    }

    public Metric get6xx() {
        return getMetricByName(Prefix._6xx);
    }

    private Metric getMetricByName(String name) {
        long time = closestTime(name);
        if (time == 0) {
            return null;
        }
        long current = redis.count(name, partition.define(time, false));
        long previous = redis.count(name, partition.define(time - 1));

        Metric metric = new Metric(name, time);
        metric.setValue(String.valueOf(current));
        int percentage = previous > 0 ? percentage(current, previous) : 100;
        metric.setPercentage(Math.abs(percentage) + "%");
        metric.setSign(Integer.compare(percentage, 0));
        metric.setWarning(Integer.compare(percentage, 0));
        return metric;
    }

    public Metric getASR() {
        long time = closestTime(Prefix._INC);
        if (time == 0) {
            return null;
        }
        String suffix = partition.define(time, false);
        long incoming = redis.count(Prefix._INC, suffix);
        float current = incoming > 0 ? (float) redis.count(Prefix._2xx, suffix) / incoming : 0;

        suffix = partition.define(time - 1);
        incoming = redis.count(Prefix._INC, suffix);
        float previous = incoming > 0 ? (float) redis.count(Prefix._2xx, suffix) / incoming : 0;

        Metric metric = new Metric("ASR", time);
        metric.setValue(String.format("%.2f", current * 100) + "%");
        int percentage = previous > 0 ? percentage(current, previous) : 100;
        metric.setPercentage(Math.abs(percentage) + "%");
        metric.setSign(Integer.compare(percentage, 0));
        metric.setWarning(Integer.compare(0, percentage));
        return metric;
    }

    private long closestTime(String prefix) {
        int depth = 0;
        long time = partition.truncate(System.currentTimeMillis());
        while (depth < MAX_DEPTH) {
            if (redis.hasKey(prefix, partition.define(time, false))) {
                return time - offset * partition.duration(); // taking with offset...
            }
            time = time - partition.duration();
            depth++;
        }
        return 0;
    }

    private int percentage(float current, float previous) {
        float percentage = 100 * (current - previous) / previous;
        return Math.round(percentage);
    }
}
