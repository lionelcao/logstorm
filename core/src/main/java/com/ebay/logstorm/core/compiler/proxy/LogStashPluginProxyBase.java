package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.compiler.LogStashPluginBase;
import com.ebay.logstorm.core.exception.LogStashExecutionException;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class LogStashPluginProxyBase extends LogStashPluginBase {
    private transient LogStashPluginObjectProxy rubyProxy;
    private final static Logger LOG = LoggerFactory.getLogger(LogStashPluginProxyBase.class);

    protected void setRubyProxy(IRubyObject rubyObject){
        this.rubyProxy = new LogStashPluginObjectProxy(rubyObject);
        this.setConfigName(rubyProxy.getConfigName());
        this.setDebugInfo(rubyProxy.getDebugInfo());
        this.setInspect(rubyProxy.getInspect());
        this.setString(rubyProxy.toString());
        this.setPluginType(rubyProxy.getPluginType());
        this.setParallelism(rubyProxy.getThreads());
    }

    public LogStashPluginProxyBase(){}

    public LogStashPluginObjectProxy getProxy(){
        return rubyProxy;
    }

    @Override
    public String toString() {
        return this.getString();
    }

    @Override
    public void initialize() throws Exception {
        if(this.rubyProxy == null) {
            LogStashPipelineProxy pipelineProxy = new LogStashPipelineProxy(this.getContext());
            if (LogStormConstants.PluginType.isInputPlugin(this)) {
                this.rubyProxy = new LogStashPluginObjectProxy((IRubyObject) pipelineProxy.getInputsProxy().get(this.getIndex()));
            } else if (LogStormConstants.PluginType.isFilterPlugin(this)) {
                this.rubyProxy = new LogStashPluginObjectProxy((IRubyObject) pipelineProxy.getFiltersProxy().get(this.getIndex()));
            } else if (LogStormConstants.PluginType.isOutputPlugin(this)) {
                this.rubyProxy = new LogStashPluginObjectProxy((IRubyObject) pipelineProxy.getOutputsProxy().get(this.getIndex()));
            } else {
                throw new LogStashExecutionException("Illegal plugin type: " + this);
            }
            LOG.info("Initialized '{}': {}",this.getUniqueName(),this.getProxy());
        } else {
            LOG.warn("'{}' was already initialized, do nothing",this.getUniqueName());
        }
    }

    @Override
    public void register() {
        LOG.info("Registering '{}': {}",this.getUniqueName(),this.getProxy());
        this.getProxy().invokeRegister();
    }

    @Override
    public void close() {
        LOG.info("Closing '{}'",this.getUniqueName());
        this.getProxy().invokeClose();
    }
}