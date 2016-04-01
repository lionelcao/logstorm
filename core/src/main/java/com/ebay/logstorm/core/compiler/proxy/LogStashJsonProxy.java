package com.ebay.logstorm.core.compiler.proxy;

import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.Helpers;

import static com.ebay.logstorm.core.compiler.proxy.RubyRuntimeFactory.getSingletonRuntime;

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
public class LogStashJsonProxy {
    private final static Ruby ruby = getSingletonRuntime();

    public static RubyHash loadJson(String json){
        return (RubyHash) Helpers.invoke(ruby.getCurrentContext(),LogStashProxyConstants.LOGSTASH_JSON_RUBY_CLASS,"load", JavaEmbedUtils.javaToRuby(ruby,json));
    }
}