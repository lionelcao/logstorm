package com.ebay.logstorm.runner.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import com.ebay.logstorm.core.PipelineConfig;
import com.ebay.logstorm.core.compiler.LogStashInput;
import com.ebay.logstorm.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class LogStashInputSpout extends BaseRichSpout {

    private final LogStashInput logStashPlugin;
    private final Serializer serializer;
    private final int memoryQueueCapacity;
    private final int batchSize;
    private StormSourceCollector collector;

    private final static Logger LOG = LoggerFactory.getLogger(LogStashInputSpout.class);

    public LogStashInputSpout(LogStashInput logStashPlugin, PipelineConfig config){
        this.logStashPlugin = logStashPlugin;
        this.serializer = config.getSerializer();
        this.memoryQueueCapacity = config.getInputQueueCapacity();
        this.batchSize = config.getInputBatchSize();
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(Constants.EVENT_KEY_FIELD,Constants.EVENT_VALUE_FIELD));
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = new StormSourceCollector(this.serializer,collector,this.memoryQueueCapacity,this.batchSize);
        try {
            this.logStashPlugin.initialize();
        } catch (Exception e) {
            LOG.error("Failed to initialize",e);
        }
        try {
            this.logStashPlugin.register();
        } catch (Exception e) {
            LOG.error("Failed to register",e);
        }
        this.logStashPlugin.run(this.collector);
    }

    public void nextTuple() {
        this.collector.flush();
    }

    @Override
    public void close() {
        try {
            this.logStashPlugin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            super.close();
        }
    }
}