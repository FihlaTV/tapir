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

import io.sip3.tapir.twig.model.SipSession;
import io.sip3.tapir.twig.model.SipSessionDetails;
import io.sip3.tapir.twig.model.SipSessionFlow;
import io.sip3.tapir.twig.sevice.SipSearchService;
import io.sip3.tapir.twig.sevice.SipSessionService;
import io.sip3.tapir.twig.model.SipSearchRequest;
import io.sip3.tapir.twig.model.SipSessionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by agafox.
 */
@RestController
public class SipSessionController {

    @Autowired
    private SipSearchService search;

    @Autowired
    private SipSessionService session;

    @RequestMapping(value = "/api/session/search", method = RequestMethod.POST)
    public List<SipSession> search(@Valid @RequestBody SipSearchRequest request) {
        return search.findSessions(request);
    }

    @RequestMapping(value = "/api/session/details", method = RequestMethod.POST)
    public SipSessionDetails details(@Valid @RequestBody SipSessionRequest request) {
        return session.details(request);
    }

    @RequestMapping(value = "/api/session/flow", method = RequestMethod.POST)
    public SipSessionFlow flow(@Valid @RequestBody SipSessionRequest request) {
        return session.flow(request);
    }

    @RequestMapping(value = "/api/session/pcap", method = RequestMethod.POST)
    public void pcap(@Valid @RequestBody SipSessionRequest request,
                     HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.tcpdump.pcapOutputStream");
        response.setHeader("Content-Disposition", "attachment; filename=\"Tapir" + String.join("-", request.getCallIds() + ".pcapOutputStream\""));
        try (
                ByteArrayOutputStream os = session.pcap(request);
                ServletOutputStream sos = response.getOutputStream()
        ) {
            os.writeTo(sos);
        }
    }
}
