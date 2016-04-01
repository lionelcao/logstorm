package com.ebay.logstorm.core.event;

import com.ebay.logstorm.core.compiler.proxy.LogStashEventProxy;
import org.jruby.RubyHash;
import org.jruby.RubyObject;

import java.util.HashMap;
import java.util.Map;

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
public class Event extends LogStashEventProxy {

    private String  streamId = "default";
    private int partitionKey;

    /**
     * Environment related context, no need to pass through the pipeline
     */
    private Map<String,Object> context;

    public Event(RubyObject internal){
        super(internal);
        this.setPartitionKey(internal.hashCode());
    }

    public Event(RubyHash hash){
        super(hash);
        this.setPartitionKey(internal.hashCode());
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public void setContext(String key, Object value){
        ensureContext();
        this.context.put(key,value);
    }

    private void ensureContext(){
        if(this.context == null){
            this.context = new HashMap<>();
        }
    }

    public int getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(int partitionKey) {
        this.partitionKey = partitionKey;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}