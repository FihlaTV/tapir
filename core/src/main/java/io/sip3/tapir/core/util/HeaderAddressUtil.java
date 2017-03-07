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

package io.sip3.tapir.core.util;

import gov.nist.javax.sip.address.SipUri;

import javax.sip.address.Address;
import javax.sip.address.TelURL;
import javax.sip.address.URI;
import javax.sip.header.HeaderAddress;
import java.util.function.Function;

/**
 * Created by agafox.
 */
public class HeaderAddressUtil {

    public static String parseUser(HeaderAddress header) {
        return get(header, SipUri::getUser);
    }

    public static String parseUserAtHostPort(HeaderAddress header) {
        return get(header, SipUri::getUserAtHostPort);
    }

    public static String parseURI(HeaderAddress header) {
        URI uri = getURI(header);
        return uri != null ? uri.toString() : null;
    }

    private static String get(HeaderAddress header, Function<SipUri, String> getter) {
        URI uri = getURI(header);
        if (uri instanceof SipUri) {
            return getter.apply((SipUri) uri);
        } else if (uri instanceof TelURL) {
            return ((TelURL) uri).getPhoneNumber();
        }
        return null;
    }

    private static URI getURI(HeaderAddress header) {
        if (header == null) {
            return null;
        }
        Address address = header.getAddress();
        if (address == null) {
            return null;
        }
        return address.getURI();
    }
}
