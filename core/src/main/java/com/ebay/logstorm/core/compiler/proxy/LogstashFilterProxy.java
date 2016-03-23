package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.LogStashContext;
import com.ebay.logstorm.core.compiler.LogStashFilter;
import com.ebay.logstorm.core.event.Collector;
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
public class LogStashFilterProxy extends LogStashPluginProxyBase implements LogStashFilter {
    public LogStashFilterProxy(IRubyObject rubyObject, int index, LogStashContext context){
        super(rubyObject);
        this.setContext(context);
        this.setIndex(index);
    }

    public LogStashFilterProxy(){}

    @Override
    public void initialize() {
        throw new RuntimeException("not implement yet, should recreate ruby proxy here");
    }

    @Override
    public void register() {

    }

    @Override
    public void close() {

    }

    @Override
    public void nextEvent(Event event, Collector collector) {

    }

    @Override
    public void filter(Event event, Collector collector) {

    }

    @Override
    public void filter(List<Event> events, Collector collector) {

    }
}