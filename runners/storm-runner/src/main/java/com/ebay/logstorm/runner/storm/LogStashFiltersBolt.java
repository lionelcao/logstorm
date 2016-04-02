package com.ebay.logstorm.runner.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.FilterPlugin;
import com.ebay.logstorm.core.event.Event;
import com.ebay.logstorm.core.serializer.Serializer;
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
public class LogStashFiltersBolt extends BaseRichBolt {
    private final List<FilterPlugin> logStashPlugins;
    private final Serializer serializer;
    private StormEventCollector collector;
//    private OutputCollector collector;
    private final static Logger LOG = LoggerFactory.getLogger(LogStashFiltersBolt.class);

    public LogStashFiltersBolt(List<FilterPlugin> logStashPlugins, PipelineContext context){
        this.logStashPlugins = logStashPlugins;
        this.serializer = context.getSerializer();
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = new StormEventCollector(collector,this.serializer);
        for(FilterPlugin filterPlugin: this.logStashPlugins) {
            try {
                filterPlugin.initialize();
                filterPlugin.register();
            } catch (Exception e) {
                LOG.error("Failed to register '{}'",filterPlugin.getUniqueName(),e);
                throw new RuntimeException(e);
            }
        }
    }

    public void execute(Tuple input) {
        byte[] eventBytes = input.getBinaryByField(Constants.EVENT_VALUE_FIELD);
        Event event = this.serializer.deserialize(eventBytes);
        event.setStreamId(input.getSourceStreamId());
        event.setContext(Constants.STORM_AUTHOR_TUPLE, input);
        for (FilterPlugin filter : this.logStashPlugins) {
            filter.filter(event);
        }
        this.collector.collect(event);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(Constants.EVENT_KEY_FIELD,Constants.EVENT_VALUE_FIELD));
    }

    @Override
    public void cleanup() {
        for(FilterPlugin filterPlugin: this.logStashPlugins) {
            try {
                filterPlugin.close();
            } catch (Exception e) {
                LOG.warn("Failed to close '{}'",filterPlugin.getUniqueName(),e);
            }
        }
        super.cleanup();
    }
}