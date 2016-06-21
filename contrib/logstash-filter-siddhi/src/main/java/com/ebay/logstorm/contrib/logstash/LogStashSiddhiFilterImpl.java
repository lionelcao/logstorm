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
 *
 * @see lib/logstorm/filter/siddhi.rb
 */
package com.ebay.logstorm.contrib.logstash;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;

import java.util.*;

public class LogStashSiddhiFilterImpl implements LogStashSiddhiFilter{

    private final static String STREAM_TYPE_KEY ="type";

    private final String executionPlan;
    private final List<String> exportStreams;
    private SiddhiManager siddhiManager;
    private ExecutionPlanRuntime executionPlanRuntime;
    private Map<String, AbstractDefinition> definitionMap;
    private Map<String, InputHandler> streamInputHandlerMap;
    private final static Logger LOG = LoggerFactory.getLogger(LogStashSiddhiFilterImpl.class);

    public LogStashSiddhiFilterImpl(String queryPlan, List<String> exportStreams){
        this.executionPlan = queryPlan;
        this.exportStreams = exportStreams;
        this.definitionMap = new HashMap<>();
    }

    public LogStashSiddhiFilterImpl(RubyString executionPlan, RubyArray exportStreams){
        this((String) JavaUtil.convertRubyToJava(executionPlan,String.class),Arrays.asList((String[]) JavaUtil.convertRubyToJava(exportStreams)));
    }

    @Override
    public void register(){
        siddhiManager = new SiddhiManager();
        executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(this.executionPlan);

        for(String exportStream:this.exportStreams) {
            executionPlanRuntime.addCallback(exportStream, new StreamCallback() {
                @Override
                public void receive(Event[] events) {
                    EventPrinter.print(events);
                }
            });
        }

        this.definitionMap= executionPlanRuntime.getStreamDefinitionMap();
        LOG.info("Loaded StreamDefinition for {}",this.definitionMap.keySet());
        this.streamInputHandlerMap = new HashMap<>();
        for(String streamId: this.definitionMap.keySet()){
            this.streamInputHandlerMap.put(streamId,executionPlanRuntime.getInputHandler(streamId));
        }
        for(String callbackStreamId:this.exportStreams){
            this.executionPlanRuntime.addCallback(callbackStreamId, new StreamCallback() {
                @Override
                public void receive(Event[] events) {
                    // TODO:
                    EventPrinter.print(events);
                }
            });
        }
        executionPlanRuntime.start();
        LOG.info("Registered logstash-siddhi-filter {}",this.toString());
    }

    @Override
    public void filter(IRubyObject event){
        // do nothing now
        LogStashEventProxy eventProxy = new LogStashEventProxy(event);
        String streamId = eventProxy.getValue(STREAM_TYPE_KEY);

        if(streamId == null){
            LOG.warn("{} is null: {}",STREAM_TYPE_KEY,event);
            eventProxy.setCancel(true);
        }else {
            if (this.definitionMap.containsKey(streamId)) {
                //
                AbstractDefinition definition = this.definitionMap.get(streamId);
                if (!eventProxy.isCanceled()) {
                    try {
                        this.streamInputHandlerMap.get(streamId).send(eventProxy.getTimestamp(), eventProxy.getDatas(definition));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                LOG.warn("Unknown streamId: {}",streamId);
            }
            eventProxy.setCancel(true);
        }
    }

    @Override
    public RubyArray multi_filter(RubyArray events){
        for(Object object:events){
            filter((IRubyObject) object);
        }
        return events;
    }

    @Override
    public void close(){
        this.executionPlanRuntime.shutdown();
        this.siddhiManager.shutdown();
    }
}