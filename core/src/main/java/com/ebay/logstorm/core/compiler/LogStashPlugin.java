package com.ebay.logstorm.core.compiler;

import java.io.Serializable;

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
public interface LogStashPlugin extends Serializable {
    int getIndex();
    String getConfigName();
    String getUniqueName();
    String getDebugInfo();
    String getPipeline();
    String getPluginType();

    long getParallelism();

    /**
     * LogStash::Plugin#initialize()
     *
     * Prepare object deserialization, resource, connection and so on.
     */
    void initialize() throws Exception;

    /**
     * Register plugin, and start to run
     */
    void register() throws Exception;

    /**
     * Shutdown plugin
     */
    void close() throws Exception;
}