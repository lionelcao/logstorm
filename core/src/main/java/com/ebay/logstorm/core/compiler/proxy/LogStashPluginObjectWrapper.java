package com.ebay.logstorm.core.compiler.proxy;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
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
public class LogStashPluginObjectWrapper {
    private final IRubyObject rubyObject;
    Ruby ruby = RubyRuntimeFactory.getSingletonRuntime();

    public LogStashPluginObjectWrapper(IRubyObject rubyObject){
        this.rubyObject = rubyObject;
    }
    public String getDebugInfo(){
        return (String) JavaUtil.convertRubyToJava(Helpers.invoke(ruby.getCurrentContext(),this.rubyObject,"debug_info"),String.class);
    }

    public String getConfigName(){
        return null;
//        (String) JavaUtil.convertRubyToJava(Helpers.invoke(ruby.getCurrentContext(),rubyObject,"config_name"),String.class);
    }

    public String getUniqueName(){
        return null;
//        (String) JavaUtil.convertRubyToJava(Helpers.invoke(ruby.getCurrentContext(),rubyObject,"plugin_unique_name"),String.class);
    }
}
