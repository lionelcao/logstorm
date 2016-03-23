package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.LogstashContext;
import com.ebay.logstorm.core.compiler.LogStashInput;
import com.ebay.logstorm.core.event.Collector;
import org.jruby.runtime.builtin.IRubyObject;

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
public class LogStashInputProxy implements LogStashInput {
    private transient IRubyObject rubyProxy;
    private final LogstashContext context;
    private int inputId;

    public LogStashInputProxy(IRubyObject rubyProxy, int inputId, LogstashContext context){
        this.rubyProxy = rubyProxy;
        this.inputId = inputId;
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
    public void run(Collector collector) {

    }
}