package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.LogStashContext;
import com.ebay.logstorm.core.compiler.LogStashInput;
import com.ebay.logstorm.core.compiler.LogStashPluginBase;
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
public class LogStashInputProxy extends LogStashPluginProxyBase implements LogStashInput {
    public LogStashInputProxy(IRubyObject rubyObject, int index, LogStashContext context){
        super(rubyObject);
        this.setIndex(index);
        this.setContext(context);
    }

    @Deprecated
    public LogStashInputProxy(){}

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