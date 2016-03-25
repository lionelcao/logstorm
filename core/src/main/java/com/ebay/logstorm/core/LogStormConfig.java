/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ebay.logstorm.core;

import com.ebay.logstorm.core.serializer.JavaObjectSnappySerializer;
import com.ebay.logstorm.core.serializer.Serializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 *
 */
public class LogStormConfig implements Serializable{
    private final Config config;

    public LogStormConfig(Properties properties){
        this.config = ConfigFactory.parseProperties(properties);
    }

    public LogStormConfig(){
        this.config = ConfigFactory.load();
    }

    public Serializer getSerializer(){
        return DEFAULT_SERIALIZER;
    }

    private static final Serializer DEFAULT_SERIALIZER = new JavaObjectSnappySerializer();

    public int getInputQueueCapacity(){
        return 10000;
    }

    public int getInputBatchSize(){
        return 50;
    }

    public String getPipelineName(){
        return "defaultPipeline";
    }

    public Config getConfig() {
        return config;
    }
}