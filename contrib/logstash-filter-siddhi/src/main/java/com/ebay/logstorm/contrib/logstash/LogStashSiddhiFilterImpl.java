package com.ebay.logstorm.contrib.logstash;

import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;

import java.util.*;

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
public class LogStashSiddhiFilterImpl implements LogStashSiddhiFilter{
    private final String executionPlan;
    private final List<String> callbackStreams;
    private SiddhiManager siddhiManager;
    private ExecutionPlanRuntime executionPlanRuntime;
    private Map<String, AbstractDefinition> definitionMap;
    private Map<String, InputHandler> streamInputHandlerMap;

    public LogStashSiddhiFilterImpl(String executionPlan, List<String> callbackStreams){
        this.executionPlan = "define stream StockStream (symbol string, price float, volume long);" +
                "from StockStream[price + 0.0 > 0.0] " +
                "select symbol, price " +
                "insert into outputStream;";
        this.callbackStreams = Collections.emptyList();
        this.callbackStreams.add("outputStream");
    }

    @Override
    public void register(){
        siddhiManager = new SiddhiManager();
        executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(this.executionPlan);
        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
            }
        });
        this.definitionMap= executionPlanRuntime.getStreamDefinitionMap();
        this.streamInputHandlerMap = new HashMap<>();
        for(String streamId: this.definitionMap.keySet()){
            this.streamInputHandlerMap.put(streamId,executionPlanRuntime.getInputHandler(streamId));
        }
        for(String callbackStreamId:this.callbackStreams){
            this.executionPlanRuntime.addCallback(callbackStreamId, new StreamCallback() {
                @Override
                public void receive(Event[] events) {
                    // TODO:
                    EventPrinter.print(events);
                }
            });
        }
        executionPlanRuntime.start();
    }

    @Override
    public void filter(IRubyObject event){
        // do nothing now
        LogStashEventProxy eventProxy = new LogStashEventProxy(event);
        String streamId = eventProxy.getValue("_type");
        if(this.definitionMap.containsKey(streamId)){
            //
            AbstractDefinition definition = this.definitionMap.get(streamId);
            eventProxy.verifyDefinition(definition);
            if(!eventProxy.isCanceled()){
                try {
                    this.streamInputHandlerMap.get(streamId).send(eventProxy.getTimestamp(),eventProxy.getDatas(definition));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        eventProxy.setCancel(true);
    }

    @Override
    public RubyArray multi_filter(RubyArray events){
        return events;
    }

    @Override
    public void close(){
        this.executionPlanRuntime.shutdown();
        this.siddhiManager.shutdown();
    }
}