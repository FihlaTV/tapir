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

package io.sip3.tapir.twig.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by agafox.
 */
@Component
public class Redis {

    private final RedisTemplate redis;

    private final HyperLogLogOperations hll;

    @Autowired
    public Redis(StringRedisTemplate redis) {
        this.redis = redis;
        this.hll = redis.opsForHyperLogLog();
    }

    public boolean hasKey(String prefix, String suffix) {
        String key = key(prefix, suffix);
        return redis.hasKey(key);
    }

    public long count(String prefix, String suffix) {
        String key = key(prefix, suffix);
        return hll.size(key);
    }

    private String key(String prefix, String suffix) {
        return prefix + "_" + suffix;
    }
}
