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

package io.sip3.tapir.salto.handler.sip;

import com.lmax.disruptor.WorkHandler;
import io.sip3.tapir.core.SipMessage;
import io.sip3.tapir.core.partition.Partition;
import io.sip3.tapir.core.partition.PartitionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by agafox.
 */
@Component
@ConditionalOnProperty(value = "statistic.enabled", havingValue = "true")
public class SipMessageStatisticHandler implements WorkHandler<List<SipMessage>> {

    private final Partition partition;

    private final long ttl;

    private final List<String> hosts;

    private final List<String> inclusions;

    private final List<String> exclusions;

    private final RedisTemplate redis;

    @Autowired
    public SipMessageStatisticHandler(@Value("${statistic.partition}") String partition,
                                      @Value("${ttl.statistic}") String ttl,
                                      @Value("${statistic.host:#{null}}") String hosts,
                                      @Value("${statistic.inclusions:#{null}}") String inclusions,
                                      @Value("${statistic.exclusions:#{null}}") String exclusions,
                                      StringRedisTemplate redis) {
        this.partition = PartitionFactory.ofPattern(partition);
        this.ttl = PartitionFactory.ofPattern(ttl).duration();
        this.hosts = split(hosts);
        this.inclusions = split(inclusions);
        this.exclusions = split(exclusions);
        this.redis = redis;
    }

    @Override
    public void onEvent(List<SipMessage> messages) throws Exception {
        if (messages.isEmpty()) {
            return;
        }

        Map<String, List<String>> statistic = new HashMap<>();

        messages.forEach(message -> {
            String suffix = partition.define(message.getMillis());
            if (checkHosts(message.getDstHost())) {
                if (message.isInvite()) {
                    addToStatistic(statistic, "inc_" + suffix, message);
                }
            } else if (checkHosts(message.getSrcHost())) {
                if (message.isInvite()) {
                    addToStatistic(statistic, "out_" + suffix, message);
                } else if (message.isMethod("200") && message.hasContent()) {
                    addToStatistic(statistic, "2xx_" + suffix, message);
                } else if (checkInclusions(message) || checkExclusions(message)) {
                    if (message.isMethod("4\\d\\d")) {
                        addToStatistic(statistic, "4xx_" + suffix, message);
                    } else if (message.isMethod("5\\d\\d")) {
                        addToStatistic(statistic, "5xx_" + suffix, message);
                    } else if (message.isMethod("6\\d\\d")) {
                        addToStatistic(statistic, "6xx_" + suffix, message);
                    }
                }
            }
        });

        statistic.forEach((k, v) -> {
            redis.opsForHyperLogLog().add(k, v.toArray());
            redis.expire(k, ttl, java.util.concurrent.TimeUnit.MILLISECONDS);
        });
    }

    private List<String> split(String str) {
        return str != null ? Stream.of(str.split(",")).map(String::trim).collect(Collectors.toList()) : new ArrayList<>();
    }

    private boolean checkHosts(String host) {
        return hosts.stream().anyMatch(h -> h.equals(host));
    }

    private boolean checkInclusions(SipMessage message) {
        return inclusions.stream().anyMatch(inclusion -> inclusion.equals(message.getMethod()));
    }

    private boolean checkExclusions(SipMessage message) {
        return inclusions.isEmpty() && exclusions.stream().noneMatch(exclusion -> exclusion.equals(message.getMethod()));
    }

    private void addToStatistic(Map<String, List<String>> statistic, String key, SipMessage message) {
        statistic.computeIfAbsent(key, k -> new ArrayList<>()).add(message.getCallId());
    }
}
