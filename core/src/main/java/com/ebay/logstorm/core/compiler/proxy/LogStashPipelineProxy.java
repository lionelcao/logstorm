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

import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.*;
import com.ebay.logstorm.core.exception.PipelineException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.time.StopWatch;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

public class LogStashPipelineProxy implements Pipeline {
    private final PipelineContext context;
    private final String logStashConfigStr;

    @JsonIgnore
    private Ruby rubyRuntime;

    @JsonIgnore
    private IRubyObject pipelineProxy;
    private List<InputPlugin> inputs;
    private List<FilterPlugin> filters;
    private List<OutputPlugin> outputs;

    private final Logger LOG = LoggerFactory.getLogger(LogStashPipelineProxy.class);

    @JsonIgnore
    private RubyArray outputsProxy;

    @JsonIgnore
    private RubyArray inputsProxy;

    @JsonIgnore
    private RubyArray filtersProxy;

    public LogStashPipelineProxy(String pipelineConfigStr) throws PipelineException {
        this(new PipelineContext(pipelineConfigStr));
    }

    public LogStashPipelineProxy(PipelineContext context) throws PipelineException {
        this.context = context;
        this.logStashConfigStr = this.context.getPipeline();
        try {
            rubyRuntime = RubyRuntimeFactory.getSingletonRuntime();
        }catch (Exception ex){
            LOG.error("Failed to bootstrap ruby runtime "+ex.getMessage(),ex);
            if(rubyRuntime !=null) rubyRuntime.shutdownTruffleContextIfRunning();
            throw new PipelineException("Failed to bootstrap ruby runtime",ex);
        }
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            evaluate();
        } catch (Exception ex){
            LOG.error("Failed to evaluate logstash configuration: "+this.logStashConfigStr,ex);
            if(rubyRuntime !=null)  rubyRuntime.shutdownTruffleContextIfRunning();
            throw new PipelineException("Failed to evaluate logstash configuration: "+this.logStashConfigStr,ex);
        } finally {
            stopWatch.stop();
            LOG.info("Taken {} seconds to evaluate",stopWatch.getTime()/1000.0);
        }
    }

    public RubyArray getOutputsProxy(){
        return this.outputsProxy;
    }
    public RubyArray getInputsProxy(){
        return this.inputsProxy;
    }
    public RubyArray getFiltersProxy(){
        return this.filtersProxy;
    }
    public IRubyObject getPipelineProxy(){
        return this.pipelineProxy;
    }

    private void evaluate(){
        this.pipelineProxy = Helpers.invoke(rubyRuntime.getCurrentContext(),LogStashProxyConstants.LOGSTASH_PIPELINE_RUBY_CLASS,"new", JavaUtil.convertJavaToRuby(rubyRuntime,logStashConfigStr));
        this.inputsProxy = (RubyArray) Helpers.invoke(rubyRuntime.getCurrentContext(),this.pipelineProxy,"get_input_plugins");
        this.filtersProxy = (RubyArray) Helpers.invoke(rubyRuntime.getCurrentContext(),this.pipelineProxy,"get_filter_plugins");
        this.outputsProxy = (RubyArray) Helpers.invoke(rubyRuntime.getCurrentContext(),this.pipelineProxy,"get_output_plugins");
        setInputs(inputsProxy);
        setFilters(filtersProxy);
        setOutputs(outputsProxy);
    }

    private void setInputs(RubyArray inputs){
        if(inputs != null) {
            this.inputs = new ArrayList<>(inputs.size());
            for (int i = 0; i < inputs.size(); i++) {
                LogStashInputProxy inputProxy = new LogStashInputProxy();
                inputProxy.setRubyProxy((IRubyObject) inputs.get(i));
                inputProxy.setIndex(i);
                inputProxy.setContext(this.context);
                inputProxy.setConfigStr(this.logStashConfigStr);
                this.inputs.add(inputProxy);
            }
        }
    }

    private void setOutputs(RubyArray outputs){
        if(outputs != null) {
            this.outputs = new ArrayList<>(outputs.size());
            for (int i = 0; i < outputs.size(); i++) {
                LogStashOutputProxy outputProxy = new LogStashOutputProxy();
                outputProxy.setRubyProxy((IRubyObject) outputs.get(i));
                outputProxy.setIndex(i);
                outputProxy.setContext(this.context);
                outputProxy.setConfigStr(this.logStashConfigStr);
                this.outputs.add(outputProxy);
            }
        }
    }

    private void setFilters(RubyArray filters){
        if(filters != null) {
            this.filters = new ArrayList<>(filters.size());
            for (int i = 0; i < filters.size(); i++) {
                LogStashFilterProxy filterProxy = new LogStashFilterProxy();
                filterProxy.setRubyProxy((IRubyObject) filters.get(i));
                filterProxy.setIndex(i);
                filterProxy.setContext(this.context);
                filterProxy.setConfigStr(this.logStashConfigStr);
                this.filters.add(filterProxy);
            }
        }
    }

    @Override
    public List<InputPlugin> getInputs(){
        return inputs;
    }

    @Override
    public List<FilterPlugin> getFilters(){
        return filters;
    }

    @Override
    public List<OutputPlugin> getOutputs(){
        return outputs;
    }

    @Override
    public PipelineContext getContext() {
        return this.context;
    }
}