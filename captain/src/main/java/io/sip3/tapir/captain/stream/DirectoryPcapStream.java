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

package io.sip3.tapir.captain.stream;

import com.lmax.disruptor.dsl.Disruptor;
import io.sip3.tapir.captain.factory.PcapHandleFactory;
import io.sip3.tapir.captain.util.GzipFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;

/**
 * Created by agafox.
 */
@Component
@ConditionalOnProperty("pcap.directory")
@ConditionalOnMissingBean({DevicePcapStream.class})
public class DirectoryPcapStream implements PcapStream {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryPcapStream.class);

    private final FileSystemWatcher watcher = new FileSystemWatcher();

    private final Disruptor disruptor;

    private final PcapHandleFactory handleFactory;

    @Autowired
    public DirectoryPcapStream(@Value("${pcap.directory}") String directory, Disruptor disruptor, PcapHandleFactory handleFactory) {
        this.watcher.addSourceFolder(new File(directory));
        this.watcher.addListener(new Listener());
        this.disruptor = disruptor;
        this.handleFactory = handleFactory;
        logger.info("Listening: {}", directory);
    }

    @Override
    public void open() throws Exception {
        disruptor.start();
        watcher.start();
        Thread.currentThread().join();
    }

    @Override
    public void close() throws Exception {
        watcher.stop();
        disruptor.shutdown();
    }

    private class Listener implements FileChangeListener {

        @Override
        public void onChange(Set<ChangedFiles> directories) {
            directories.stream()
                    .map(ChangedFiles::getFiles)
                    .flatMap(Set::stream)
                    .map(ChangedFile::getFile)
                    .filter(File::exists)
                    .forEach(f -> {
                        if (isGzipFile(f)) {
                            try {
                                GzipFileReader.read(f, this::openFilePcapStream);
                            } catch (Exception e) {
                                logger.error("Got exception...", e);
                            }
                        } else {
                            openFilePcapStream(f);
                        }
                    });
        }

        public boolean isGzipFile(File file) {
            return file.isFile() && file.getName().endsWith(".gz");
        }

        private void openFilePcapStream(File file) {
            try (FilePcapStream fps = new FilePcapStream(file.toString(), disruptor, handleFactory, false)) {
                fps.open();
            } catch (Exception e) {
                logger.error("Got exception...", e);
            }
        }
    }
}
