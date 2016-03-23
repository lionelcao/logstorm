package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.LogstashContext;
import com.ebay.logstorm.core.compiler.LogStashOutput;
import com.ebay.logstorm.core.event.Event;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.List;

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
public class LogStashOutputProxy implements LogStashOutput {

    private transient IRubyObject rubyProxy;
    private final LogstashContext context;
    private int outputId;

    public LogStashOutputProxy(IRubyObject rubyProxy, int outputId, LogstashContext context){
        this.rubyProxy = rubyProxy;
        this.outputId = outputId;
        this.context = context;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void register() {

    }

    @Override
    public void close() {

    }

    @Override
    public void receive(Event event) {

    }

    @Override
    public void receive(List<Event> events) {

    }
}