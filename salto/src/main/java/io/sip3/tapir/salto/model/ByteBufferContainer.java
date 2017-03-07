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

package io.sip3.tapir.salto.model;

import java.nio.ByteBuffer;

/**
 * Created by agafox.
 */
public class ByteBufferContainer {

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(65535);

    private boolean handleImmediately;

    public void clear() {
        handleImmediately = true;
        buffer.clear();
    }

    public void flip() {
        handleImmediately = false;
        buffer.flip();
    }

    public boolean shouldHandleImmediately() {
        return handleImmediately;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
