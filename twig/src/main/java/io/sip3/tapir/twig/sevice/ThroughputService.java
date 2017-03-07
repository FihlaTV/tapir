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
import io.sip3.tapir.twig.model.Prefix;
import io.sip3.tapir.twig.model.Throughput;
import io.sip3.tapir.twig.model.ThroughputRequest;
import io.sip3.tapir.twig.redis.Redis;
import io.sip3.tapir.twig.util.TimeIntervalIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agafox.
 */
@Component
public class ThroughputService {

    private final Partition partition;

    private final Redis redis;

    @Autowired
    public ThroughputService(@Value("${statistic.partition}") String partition, Redis redis) {
        this.partition = PartitionFactory.ofPattern(partition);
        this.redis = redis;
    }

    public List<Throughput> getThroughput(ThroughputRequest request) {
        List<Throughput> throughput = new ArrayList<>();

        TimeIntervalIterator.of(request.getMillis(), partition).forEachRemaining(millis -> {
            Throughput th = new Throughput(millis(millis));

            String suffix = partition.define(millis, false);
            th.setIncoming(caps(millis, redis.count(Prefix._INC, suffix)));
            th.setOutgoing(caps(millis, redis.count(Prefix._OUT, suffix)));

            throughput.add(th);
        });

        return throughput;
    }

    private long millis(long millis) {
        long m = System.currentTimeMillis() - millis;

        if (m < partition.duration()) {
            return millis + m;
        }
        return millis + partition.duration() / 2;
    }

    private long caps(long millis, long value) {
        long duration = Math.min(partition.duration(), System.currentTimeMillis() - millis);
        return value * 1000 / duration;
    }
}

