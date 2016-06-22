/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.ebay.logstorm.server.services;

import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstorm.server.platform.ExecutionPlatformFactory;
import com.ebay.logstorm.server.platform.spark.SparkExecutionPlatform;
import com.ebay.logstorm.server.platform.storm.StormExecutionPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PlatformEntityRepository {
    private final static Logger LOG = LoggerFactory.getLogger(PlatformEntityRepository.class);

    private final static Map<String,Class<? extends ExecutionPlatform>> platformClassMapping = new HashMap<>();

    public static void register(String platformType, Class<? extends ExecutionPlatform> platformClass){
        platformClassMapping.put(platformType, platformClass);
    }

    public static ExecutionPlatform getExecutonPlatform(String className) {
        if (platformClassMapping.containsKey(className)) {
            return ExecutionPlatformFactory.getPlatformInstance(platformClassMapping.get(className));
        } else {
            LOG.error(String.format("Unrecognized platform %s", className));
            return null;
        }
    }

    public static Set<String> getAllPlatforms() {
        return platformClassMapping.keySet();
    }

    static {
        register(StormExecutionPlatform.class.getCanonicalName(), StormExecutionPlatform.class);
        register(SparkExecutionPlatform.class.getCanonicalName(), SparkExecutionPlatform.class);
    }
}
