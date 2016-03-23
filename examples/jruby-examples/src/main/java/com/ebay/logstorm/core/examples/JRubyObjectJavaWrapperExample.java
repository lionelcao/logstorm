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
package com.ebay.logstorm.core.examples;

import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;

public class JRubyObjectJavaWrapperExample {
    public static void main(String[] args){
        Ruby rubyRuntime = Ruby.getGlobalRuntime();
        rubyRuntime.evalScriptlet("require 'lib/panda'");
        RubyModule rubyModule = rubyRuntime.getClassFromPath("Panda");
        IRubyObject panda = Helpers.invoke(rubyRuntime.getCurrentContext(),rubyModule,"new", JavaUtil.convertJavaToRuby(rubyRuntime,"JavaPanda!"));
        System.out.println(panda);
        Helpers.invoke(rubyRuntime.getCurrentContext(),panda,"eat");
        Helpers.invoke(rubyRuntime.getCurrentContext(),panda,"fight");
        Helpers.invoke(rubyRuntime.getCurrentContext(),panda,"run");
    }
}