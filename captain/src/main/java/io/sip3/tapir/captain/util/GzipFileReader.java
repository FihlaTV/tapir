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

import com.sun.javafx.PlatformUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

/**
 * Created by agafox.
 */
public class GzipFileReader {

    private static final Path SHARED_MEMORY = Paths.get("/dev/shm");

    private static final String TEMP_PREFIX = "tapir_captain_";

    public static void read(File file, Consumer<File> handle) throws IOException {
        Path temp;
        if (PlatformUtil.isUnix()) {
            temp = Files.createTempFile(SHARED_MEMORY, TEMP_PREFIX, null);
        } else {
            temp = Files.createTempFile(TEMP_PREFIX, null);
        }

        try (
                InputStream is = new FileInputStream(file);
                GZIPInputStream gis = new GZIPInputStream(is);
                OutputStream os = Files.newOutputStream(temp);
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = gis.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, length);
            }

            handle.accept(temp.toFile());
        } finally {
            Files.delete(temp);
        }
    }
}
