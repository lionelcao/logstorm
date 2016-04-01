package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.event.Event;
import org.jruby.*;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.builtin.InternalVariables;
import org.jruby.runtime.builtin.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
public class LogStashPluginObjectProxy {
    private final IRubyObject rubyObject;
    private Ruby ruby = RubyRuntimeFactory.getSingletonRuntime();
    private final static Logger LOG = LoggerFactory.getLogger(LogStashPluginObjectProxy.class);

    public LogStashPluginObjectProxy(IRubyObject rubyObject){
        this.rubyObject = rubyObject;
    }

    public <T> T invoke(String methodName, Class<T> returnType){
        return (T) JavaUtil.convertRubyToJava(Helpers.invoke(ruby.getCurrentContext(),this.rubyObject,methodName),returnType);
    }
    public void invokeWithArguments(String methodName, IRubyObject ... args){
        Helpers.invoke(ruby.getCurrentContext(),this.rubyObject,methodName,args);
    }

    public void invoke(String methodName){
        // if(LOG.isDebugEnabled()) LOG.debug("Invoking method '{}():void' of '{}''",methodName,this.rubyObject);
        Helpers.invoke(ruby.getCurrentContext(),this.rubyObject,methodName);
    }

    public <T> T invokeAs(RubyClass clazz, String methodName, Class<T> returnType){
        return (T) JavaUtil.convertRubyToJava(Helpers.invokeAs(ruby.getCurrentContext(),clazz,this.rubyObject,methodName,new IRubyObject[]{},null),returnType);
    }

    public <T> T getMetaInstanceVariable(String varName, Class<T> type){
        return (T) JavaUtil.convertRubyToJava(this.rubyObject.getMetaClass().getInstanceVariable(varName),type);
    }

    public String[] getMetaVariableNames(){
        return this.rubyObject.getMetaClass().getVariableNames();
    }

    public List<Variable<Object>> getVariables(){
        return this.rubyObject.getVariableList();
    }

    public Variable<Object> getVariable(String variableName){
        for(Variable<Object> var:this.getVariables()){
            if(var.getName().equals(variableName)){
                return var;
            }
        }
        return null;
    }

    public InternalVariables getInternalVariables(){
        return this.rubyObject.getInternalVariables();
    }

    public String getDebugInfo(){
        return invoke("debug_info",String.class);
    }

    public String getConfigName(){
        return this.getMetaInstanceVariable("@config_name",String.class);
    }

    public String getInspect(){
        return invoke("inspect",String.class);
    }

    public String toString(){
        return invoke("to_s",String.class);
    }

    /**
     *
     * @plugin_type
     *
     * @return input,filter,output
     */
    public String getPluginType(){
        return this.getMetaInstanceVariable("@plugin_type",String.class);
    }

    public Map<String,String> getTags(){
        return this.getMetaInstanceVariable("@tags",Map.class);
    }

    public String getConfig(){
        return this.getMetaInstanceVariable("@config",String.class);
    }

    public RubyHash getParams(){
        return (RubyHash) this.getVariable("@params").getValue();
    }
    public RubyHash getOriginalParams(){
        return (RubyHash) this.getVariable("@original_params").getValue();
    }
    public Long getThreads(){
        if(this.getParams().containsKey("threads")) {
            return (Long) this.getParams().get("threads");
        } else {
            return 0L;
        }
    }

    public void invokeRegister(){
        this.invoke(LogStashProxyConstants.LOGSTASH_INPUT_PLUGIN_REGISTER_METHOD_NAME);
    }

    public void invokeClose(){
        this.invoke(LogStashProxyConstants.LOGSTASH_INPUT_PLUGIN_CLOSE_METHOD_NAME);
    }

    public void invokeFilter(RubyObject event) {
        this.invokeWithArguments(LogStashProxyConstants.LOGSTASH_FILTER_PLUGIN_FILTER_METHOD_NAME,event);
    }

    public void invokeReceive(RubyObject event) {
        this.invokeWithArguments(LogStashProxyConstants.LOGSTASH_OUTPUT_PLUGIN_RECEIVE_METHOD_NAME,event);
    }

    public void invokeMultiReceive(RubyArray events) {
        this.invokeWithArguments(LogStashProxyConstants.LOGSTASH_OUTPUT_PLUGIN_MULTI_RECEIVE_METHOD_NAME,events);
    }

    public void invokeReceive(List<Event> events) {
        IRubyObject[] objects = new RubyObject[events.size()];
        for(int i=0;i<events.size();i++) objects[i] = events.get(i).getInternal();
        this.invokeMultiReceive(RubyArray.newArray(ruby,objects));
    }
}