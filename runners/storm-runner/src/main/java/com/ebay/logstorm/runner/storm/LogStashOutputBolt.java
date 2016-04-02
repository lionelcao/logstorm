package com.ebay.logstorm.runner.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.OutputPlugin;
import com.ebay.logstorm.core.event.Event;
import com.ebay.logstorm.core.serializer.Serializer;

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
public class LogStashOutputBolt extends BaseRichBolt{

    private final OutputPlugin logStashPlugin;
    private final Serializer serializer;

    public LogStashOutputBolt(OutputPlugin logStashFilterPlugin, PipelineContext context){
        this.logStashPlugin = logStashFilterPlugin;
        this.serializer = context.getSerializer();
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        try {
            logStashPlugin.initialize();
            logStashPlugin.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(Tuple input) {
        byte[] eventBytes = input.getBinaryByField(Constants.EVENT_VALUE_FIELD);
        Event event = this.serializer.deserialize(eventBytes);
        event.setContext(Constants.STORM_AUTHOR_TUPLE,input);
        event.setStreamId(input.getSourceStreamId());
        logStashPlugin.receive(event);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(Constants.EVENT_KEY_FIELD,Constants.EVENT_VALUE_FIELD));
    }

    @Override
    public void cleanup() {
        try {
            this.logStashPlugin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.cleanup();
    }
}