package com.ebay.logstorm.server.platform;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
public class ExecutionPlatformFactory {
    private final static Map<Class<? extends ExecutionPlatform>,ExecutionPlatform> platformInstanceCache = new HashMap<>();
    public static ExecutionPlatform getPlatformInstance(Class<? extends ExecutionPlatform> platformClass){
        Preconditions.checkNotNull(platformClass,"Platform class is null");
        if(platformInstanceCache.containsKey(platformClass)){
            return platformInstanceCache.get(platformClass);
        }else{
            try {
                ExecutionPlatform instance = platformClass.newInstance();
                platformInstanceCache.put(platformClass,instance);
                return instance;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ExecutionPlatform getPlatformInstance(String platformClassName) throws ClassNotFoundException {
        Preconditions.checkNotNull(platformClassName,"Platform class name is null");
        return getPlatformInstance((Class<? extends ExecutionPlatform>) Class.forName(platformClassName));
    }

    public static ExecutionPlatform newPlatformInstance(String adapterClassName, Properties properties) {
        try {
            ExecutionPlatform instance = ((Class<? extends ExecutionPlatform>) Class.forName(adapterClassName)).newInstance();
            instance.prepare(properties);
            return instance;
        } catch (InstantiationException | IllegalAccessException |ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
