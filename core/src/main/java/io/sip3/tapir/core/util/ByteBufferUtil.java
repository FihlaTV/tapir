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
 * Created by agafox.
 */
public class ByteBufferUtil {

    public static String getIPv4String(ByteBuffer buffer) {
        return (buffer.get() & 0xff) + "." + (buffer.get() & 0xff) + "." + (buffer.get() & 0xff) + "." + (buffer.get() & 0xff);
    }

    public static int getUnsignedShort(ByteBuffer buffer) {
        return buffer.getShort() & 0xffff;
    }

    public static String getString(ByteBuffer buffer, int size) {
        return new String(getBytes(buffer, size));
    }

    public static byte[] getBytes(ByteBuffer buffer, int size) {
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }
}
