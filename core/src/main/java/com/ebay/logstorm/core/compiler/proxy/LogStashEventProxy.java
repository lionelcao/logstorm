package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.compiler.proxy.LogStashProxyConstants;
import com.ebay.logstorm.core.compiler.proxy.RubyRuntimeFactory;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;

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
public class LogStashEventProxy {
    protected final RubyObject internal;
    public LogStashEventProxy(RubyObject internal) {
        this.internal = internal;
    }

    public LogStashEventProxy(RubyHash hash){
        this.internal = (RubyObject) Helpers.invoke(RubyRuntimeFactory.getSingletonRuntime().getCurrentContext(),LogStashProxyConstants.LOGSTASH_EVENT_RUBY_MODULE,"new", hash);
    }

    public RubyHash getInternalHash() {
        return (RubyHash) Helpers.invoke(RubyRuntimeFactory.getSingletonRuntime().getCurrentContext(), this.internal, "to_hash");
    }

    public String getInternalString() {
        return (String) JavaUtil.convertRubyToJava(Helpers.invoke(RubyRuntimeFactory.getSingletonRuntime().getCurrentContext(), this.internal, "to_s"), String.class);
    }

    public RubyObject getInternal() {
        return internal;
    }

    public Boolean isCancelled(){
        return (Boolean) JavaUtil.convertRubyToJava(Helpers.invoke(RubyRuntimeFactory.getSingletonRuntime().getCurrentContext(), this.internal, "cancelled?"), Boolean.class);
    }

    public Long getTimestamp(){
        return (Long) JavaUtil.convertRubyToJava(Helpers.invoke(RubyRuntimeFactory.getSingletonRuntime().getCurrentContext(), this.internal, "timestamp"), Long.class);
    }

    public String getJson(){
        return (String) JavaUtil.convertRubyToJava(Helpers.invoke(RubyRuntimeFactory.getSingletonRuntime().getCurrentContext(), this.internal, "to_json"), String.class);
    }
}