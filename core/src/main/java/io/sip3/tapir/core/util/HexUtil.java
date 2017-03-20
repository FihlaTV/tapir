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

import java.nio.ByteBuffer;

/**
 * Created by agafonov on 02/12/15.
 */
public class HexUtil {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(ByteBuffer buffer) {
        char[] hexChars = new char[buffer.limit() * 2];
        for (int i = 0; i < buffer.limit(); i++) {
            int v = buffer.get(i) & 0xff;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0f];
        }
        return new String(hexChars);
    }
}
