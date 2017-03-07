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

package io.sip3.tapir.twig.controller;

import io.sip3.tapir.twig.model.Metric;
import io.sip3.tapir.twig.model.Throughput;
import io.sip3.tapir.twig.model.ThroughputRequest;
import io.sip3.tapir.twig.sevice.MetricService;
import io.sip3.tapir.twig.sevice.ThroughputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by agafox.
 */
@RestController
public class DashboardController {

    @Autowired
    private MetricService metrics;

    @Autowired
    private ThroughputService throughput;

    @RequestMapping(value = "/api/dashboard/metrics", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Metric> metrics() {
        return Stream.of(
                metrics.getASR(),
                metrics.get4xx(),
                metrics.get5xx(),
                metrics.get6xx()
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/dashboard/throughput", method = RequestMethod.POST)
    public List<Throughput> throughput(@Valid @RequestBody ThroughputRequest request) {
        return throughput.getThroughput(request);
    }
}
