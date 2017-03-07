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

/**
 * Created by agafox.
 */
public interface Field {

    String millis = "millis";
    String nanos = "nanos";
    String src_ip = "src_ip";
    String src_port = "src_port";
    String src_host = "src_host";
    String dst_ip = "dst_ip";
    String dst_port = "dst_port";
    String dst_host = "dst_host";
    String call_id = "call_id";
    String method = "method";
    String caller = "caller";
    String callee = "callee";
    String payload = "payload";
}
