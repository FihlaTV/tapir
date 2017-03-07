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

package io.sip3.tapir.salto.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by agafox.
 */
@Configuration
public class MongoConfiguration {

    @Bean
    public MongoClientURI uri(@Value("${mongo.uri}") String uri) {
        return new MongoClientURI(uri,
                MongoClientOptions.builder().writeConcern(WriteConcern.UNACKNOWLEDGED));
    }

    @Bean
    public MongoDatabase db(MongoClientURI uri) {
        MongoClient client = new MongoClient(uri);
        return client.getDatabase(uri.getDatabase());
    }
}
