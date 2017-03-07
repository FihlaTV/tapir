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

package io.sip3.tapir.captain.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by agafox.
 */
public class SipContentReader {

    private static final List<byte[]> MARKERS = Stream.of(
            // RFC 3261
            "SIP/2.0 ", "INVITE ", "REGISTER ", "ACK ", "CANCEL ", "BYE ", "OPTIONS ",
            // RFC 3262
            "PRACK ",
            // RFC 3428
            "MESSAGE ",
            // RFC 6665
            "SUBSCRIBE ", "NOTIFY "
    )
            .map(String::getBytes)
            .collect(Collectors.toList());

    private static final byte CR = 0x0D;

    private static final byte LF = 0x0A;

    public static void read(byte[] content, BiConsumer<Integer, Integer> handle) {
        int markerIndex = -1;
        int lineIndex = 0;
        boolean lastByteIsCR = false;
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
                case LF:
                    if (lastByteIsCR) {
                        int newLineIndex = i + 1;
                        if (checkMarker(content, lineIndex, newLineIndex - lineIndex)) {
                            if (markerIndex >= 0) {
                                handle.accept(markerIndex, lineIndex);
                            }
                            markerIndex = lineIndex;
                        }
                        lineIndex = newLineIndex;
                    }
                    lastByteIsCR = false;
                    break;
                case CR:
                    lastByteIsCR = true;
                    break;
                default:
                    lastByteIsCR = false;
                    break;
            }
        }
        if (markerIndex >= 0) {
            handle.accept(markerIndex, content.length);
        }
    }

    public static boolean checkMarker(byte[] content, int offset, int length) {
        return MARKERS.stream()
                .anyMatch(marker -> {
                    if (length < marker.length) {
                        return false;
                    }

                    boolean matches = false;

                    for (int i = 0; i < marker.length; i++) {
                        matches = marker[i] == content[i + offset];
                        if (!matches) {
                            break;
                        }
                    }
                    return matches;
                });
    }
}
