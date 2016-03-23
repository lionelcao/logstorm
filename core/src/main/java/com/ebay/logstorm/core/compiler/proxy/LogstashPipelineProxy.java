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
import com.ebay.logstorm.core.compiler.*;
import com.ebay.logstorm.core.exception.LogStashCompileException;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.ArrayList;
import java.util.List;

public class LogStashPipelineProxy implements LogStashPipeline {
    private final LogStashContext context;
    private final String logStashConfigStr;
    private Ruby runtime;
    private IRubyObject rubyPipeline;
    private List<LogStashInput> inputs;
    private List<LogStashFilter> filters;
    private List<LogStashOutput> outputs;

    public LogStashPipelineProxy(String logStashConfigStr,LogStashContext context) throws LogStashCompileException {
        this.context = context;
        this.logStashConfigStr = logStashConfigStr;
        try {
            runtime = RubyRuntimeFactory.getSingletonRuntime();
        }catch (Exception ex){
            if(runtime!=null) runtime.shutdownTruffleContextIfRunning();
            throw new LogStashCompileException("Failed to bootstrap ruby runtime",ex);
        }
        try {
            evaluate();
        } catch (Exception ex){
            runtime.shutdownTruffleContextIfRunning();
            throw new LogStashCompileException("Failed to evaluate logstash configuration",ex);
        }
    }



    private void evaluate(){
        RubyModule rubyModule = RubyRuntimeFactory.getSingletonRuntime().getClassFromPath(LogStashProxyConstants.LOGSTASH_PIPELINE_RUBY_CLASS);;
        this.rubyPipeline = Helpers.invoke(runtime.getCurrentContext(),rubyModule,"new", JavaUtil.convertJavaToRuby(runtime,logStashConfigStr));
        RubyArray inputs = (RubyArray) Helpers.invoke(runtime.getCurrentContext(),this.rubyPipeline,"get_input_plugins");
        RubyArray filters = (RubyArray) Helpers.invoke(runtime.getCurrentContext(),this.rubyPipeline,"get_filter_plugins");
        RubyArray outputs = (RubyArray) Helpers.invoke(runtime.getCurrentContext(),this.rubyPipeline,"get_output_plugins");
        setInputs(inputs);
        setFilters(filters);
        setOutputs(outputs);
    }

    private void setInputs(RubyArray inputs){
        if(inputs != null) {
            this.inputs = new ArrayList<LogStashInput>(inputs.size());
            for (int i = 0; i < inputs.size(); i++) {
                this.inputs.add(new LogStashInputProxy((IRubyObject) inputs.get(i), i, this.context));
            }
        }
    }

    private void setOutputs(RubyArray outputs){
        if(outputs != null) {
            this.outputs = new ArrayList<LogStashOutput>(outputs.size());
            for (int i = 0; i < outputs.size(); i++) {
                this.outputs.add(new LogStashOutputProxy((IRubyObject) outputs.get(i), i, this.context));
            }
        }
    }

    private void setFilters(RubyArray filters){
        if(filters != null) {
            this.filters = new ArrayList<LogStashFilter>(filters.size());
            for (int i = 0; i < filters.size(); i++) {
                this.filters.add(new LogStashFilterProxy((IRubyObject) filters.get(i), i, this.context));
            }
        }
    }

    @Override
    public List<LogStashInput> getInputs(){
        return inputs;
    }

    @Override
    public List<LogStashFilter> getFilters(){
        return filters;
    }

    @Override
    public List<LogStashOutput> getOutputs(){
        return outputs;
    }
}