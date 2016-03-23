package com.ebay.logstorm.core.compiler;

import com.ebay.logstorm.core.LogStashContext;

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
public abstract class LogStashPluginBase implements LogStashPlugin {
    private int index;
    private String name;
    private String configName;
    private LogStashContext context;
    private String configContent;
    private String debugInfo;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String type) {
        this.configName = type;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getUniqueName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogStashContext getContext() {
        return context;
    }

    public void setContext(LogStashContext context) {
        this.context = context;
    }

    public String getConfig() {
        return configContent;
    }

    public void getConfig(String configContent) {
        this.configContent = configContent;
    }

    @Override
    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }
}
