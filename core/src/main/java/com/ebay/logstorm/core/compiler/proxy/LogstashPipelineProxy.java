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

package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.LogStashContext;
import com.ebay.logstorm.core.compiler.LogStashFilter;
import com.ebay.logstorm.core.compiler.LogStashInput;
import com.ebay.logstorm.core.compiler.LogStashOutput;
import com.ebay.logstorm.core.compiler.LogStashPipeline;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.List;

public class LogStashPipelineProxy implements LogStashPipeline {

    public final static String LOGSTORM_RUBY_FILE="logstorm";
    public final static String LOGSTASH_PIPELINE_RUBY_CLASS="LogstashPipeline";

    private final LogStashContext context;
    private final String logStashConfigStr;
    private Ruby runtime;
    private IRubyObject rubyPipeline;
    private List<LogStashInput> inputs;
    private List<LogStashFilter> filters;
    private List<LogStashOutput> outputs;
    private final static String LOGSTASH_HOME = "/Users/hchen9/Downloads/logstash-2.2.0";
    private final static String JRUBY_VERSION = "1.9";

    public LogStashPipelineProxy(String logStashConfigStr,LogStashContext context){
        this.context = context;
        this.logStashConfigStr = logStashConfigStr;
        runtime = Ruby.getGlobalRuntime();
        bootstrap();
        evaluate();
    }

    private void bootstrap(){
        String rubyGemHome = String.format("%s/vendor/bundle/jruby/%s",LOGSTASH_HOME,JRUBY_VERSION);
        String bootstrap = "";
        bootstrap += String.format("ENV[\"%s\"] = \"%s\";\n","LOGSTASH_HOME",LOGSTASH_HOME);
        bootstrap += String.format("ENV[\"%s\"] = \"%s\";\n","GEM_HOME",rubyGemHome);
        bootstrap += "require '"+ LOGSTORM_RUBY_FILE+"';\n";
        this.runtime.evalScriptlet(bootstrap);
    }

    private void evaluate(){
        RubyModule rubyModule = runtime.getClassFromPath(LOGSTASH_PIPELINE_RUBY_CLASS);
        this.rubyPipeline = Helpers.invoke(runtime.getCurrentContext(),rubyModule,"new", JavaUtil.convertJavaToRuby(runtime,logStashConfigStr));
    }

    @Override
    public List<LogStashInput> getInputs(){
        return inputs;
    }

    @Override
    public List<LogStashFilter> getFilters(){
        return null;
    }

    @Override
    public List<LogStashOutput> getOutputs(){
        return null;
    }
}