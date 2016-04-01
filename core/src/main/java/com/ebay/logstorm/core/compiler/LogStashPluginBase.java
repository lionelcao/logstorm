package com.ebay.logstorm.core.compiler;

import com.ebay.logstorm.core.PipelineContext;

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
    private String config_name;
    private PipelineContext context;
    private String configStr;
    private String debugInfo;
    private String inspect;
    private String to_s;
    private String plugin_type;
    private long parallelism = 1;

    @Override
    public String getUniqueName() {
        return this.getPluginType()+"_"+this.getConfigName()+"_"+this.getIndex();
    }

    public String getConfigName() {
        return config_name;
    }

    public void setConfigName(String type) {
        this.config_name = type;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PipelineContext getContext() {
        return context;
    }

    public void setContext(PipelineContext context) {
        this.context = context;
    }

    public String getConfigStr() {
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }

    @Override
    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getInspect() {
        return inspect;
    }

    public void setInspect(String inspect) {
        this.inspect = inspect;
    }

    public String getString() {
        return to_s;
    }

    public void setString(String to_s) {
        this.to_s = to_s;
    }

    @Override
    public String toString() {
        return this.getString();
    }


    public String getPluginType() {
        return plugin_type;
    }

    public void setPluginType(String plugin_type) {
        this.plugin_type = plugin_type;
    }

    public long getParallelism() {
        return parallelism > 0 ? parallelism:1;
    }

    public void setParallelism(long parallelism) {
        this.parallelism = parallelism;
    }
}
