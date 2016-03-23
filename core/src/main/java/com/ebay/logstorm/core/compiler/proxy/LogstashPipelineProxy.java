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
import org.apache.commons.lang3.time.StopWatch;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LogStashPipelineProxy implements LogStashPipeline {
    private final LogStashContext context;
    private final String logStashConfigStr;
    private Ruby rubyRuntime;
    private IRubyObject rubyPipeline;
    private List<LogStashInput> inputs;
    private List<LogStashFilter> filters;
    private List<LogStashOutput> outputs;
    private Logger LOG = LoggerFactory.getLogger(LogStashPipelineProxy.class);

    public LogStashPipelineProxy(String logStashConfigStr,LogStashContext context) throws LogStashCompileException {
        this.context = context;
        this.logStashConfigStr = logStashConfigStr;
        try {
            rubyRuntime = RubyRuntimeFactory.getSingletonRuntime();
        }catch (Exception ex){
            LOG.error("Failed to bootstrap ruby runtime "+ex.getMessage(),ex);
            if(rubyRuntime !=null) rubyRuntime.shutdownTruffleContextIfRunning();
            throw new LogStashCompileException("Failed to bootstrap ruby runtime",ex);
        }
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            evaluate();
        } catch (Exception ex){
            LOG.error("Failed to evaluate logstash configuration: "+this.logStashConfigStr,ex);
            if(rubyRuntime !=null)  rubyRuntime.shutdownTruffleContextIfRunning();
            throw new LogStashCompileException("Failed to evaluate logstash configuration: "+this.logStashConfigStr,ex);
        } finally {
            stopWatch.stop();
            LOG.info("Taken {} seconds to evaluate",stopWatch.getTime()/1000.0);
        }
    }

    private void evaluate(){
        RubyModule rubyModule = RubyRuntimeFactory.getSingletonRuntime().getClassFromPath(LogStashProxyConstants.LOGSTASH_PIPELINE_RUBY_CLASS);;
        this.rubyPipeline = Helpers.invoke(rubyRuntime.getCurrentContext(),rubyModule,"new", JavaUtil.convertJavaToRuby(rubyRuntime,logStashConfigStr));
        RubyArray inputs = (RubyArray) Helpers.invoke(rubyRuntime.getCurrentContext(),this.rubyPipeline,"get_input_plugins");
        RubyArray filters = (RubyArray) Helpers.invoke(rubyRuntime.getCurrentContext(),this.rubyPipeline,"get_filter_plugins");
        RubyArray outputs = (RubyArray) Helpers.invoke(rubyRuntime.getCurrentContext(),this.rubyPipeline,"get_output_plugins");
        setInputs(inputs);
        setFilters(filters);
        setOutputs(outputs);
    }

    private void setInputs(RubyArray inputs){
        if(inputs != null) {
            this.inputs = new ArrayList<LogStashInput>(inputs.size());
            for (int i = 0; i < inputs.size(); i++) {
                LogStashInputProxy inputProxy = new LogStashInputProxy((IRubyObject) inputs.get(i), i, this.context);
                inputProxy.setConfigStr(this.logStashConfigStr);
                this.inputs.add(inputProxy);
            }
        }
    }

    private void setOutputs(RubyArray outputs){
        if(outputs != null) {
            this.outputs = new ArrayList<LogStashOutput>(outputs.size());
            for (int i = 0; i < outputs.size(); i++) {
                LogStashOutputProxy outputProxy = new LogStashOutputProxy((IRubyObject) outputs.get(i), i, this.context);
                outputProxy.setConfigStr(this.logStashConfigStr);

                this.outputs.add(outputProxy);
            }
        }
    }

    private void setFilters(RubyArray filters){
        if(filters != null) {
            this.filters = new ArrayList<LogStashFilter>(filters.size());
            for (int i = 0; i < filters.size(); i++) {
                LogStashFilterProxy filterProxy = new LogStashFilterProxy((IRubyObject) filters.get(i), i, this.context);
                filterProxy.setConfigStr(this.logStashConfigStr);
                this.filters.add(filterProxy);
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