package com.ebay.logstorm.runner.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.FilterPlugin;
import com.ebay.logstorm.core.compiler.InputPlugin;
import com.ebay.logstorm.core.compiler.OutputPlugin;
import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
public class StormPipelineRunner implements PipelineRunner {
    private final static Logger LOG = LoggerFactory.getLogger(StormPipelineRunner.class);
    public void run(Pipeline pipeline) {
        PipelineContext context = pipeline.getContext();
        List<InputPlugin> inputs = pipeline.getInputs();
        List<FilterPlugin> filters = pipeline.getFilters();
        List<OutputPlugin> outputs = pipeline.getOutputs();

        Config stormConfig = new Config();
        TopologyBuilder builder = new TopologyBuilder();

        List<LogStashInputSpout> inputSpouts = new ArrayList<LogStashInputSpout>(inputs.size());
        LogStashFiltersBolt filtersBolt;
        List<LogStashOutputBolt> outputBolts = new ArrayList<LogStashOutputBolt>(inputs.size());

        Preconditions.checkState(inputs.size()>0,"Inputs number is less then 0");

        for(InputPlugin input:inputs){
            LogStashInputSpout  inputSpout = new LogStashInputSpout(input,context);
            builder.setSpout(input.getUniqueName(),inputSpout,input.getParallelism());
            inputSpouts.add(inputSpout);
        }

        // TODO: Avoid create filter bolt if having no filters, even created, it will do nothing but just pass through the events
        filtersBolt = new LogStashFiltersBolt(filters,context);
        BoltDeclarer declarer = builder.setBolt(Constants.STORM_FILTER_BOLT_NAME,filtersBolt, context.getFilterParallesm());
        for(InputPlugin input:inputs) {
            declarer.fieldsGrouping(input.getUniqueName(),new Fields(Constants.EVENT_KEY_FIELD));
        }

        for(OutputPlugin output: outputs){
            LogStashOutputBolt outputBolt = new LogStashOutputBolt(output,context);
            outputBolts.add(outputBolt);
            builder.setBolt(output.getUniqueName(),outputBolt,output.getParallelism() ).fieldsGrouping(Constants.STORM_FILTER_BOLT_NAME, new Fields(Constants.EVENT_KEY_FIELD));
        }

        LOG.info("Submitting topology '{}': {}",context.getPipelineName(),pipeline);
        if(pipeline.getContext().getDeployMode() == LogStormConstants.DeployMode.LOCAL) {
            LOG.info("Submitting topology '{}': {} in local mode",context.getPipelineName(),pipeline);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(context.getPipelineName(), stormConfig, builder.createTopology());
        } else {
            LOG.info("Submitting topology '{}': {} in remote mode",context.getPipelineName(),pipeline);
            try {
                StormSubmitter.submitTopologyWithProgressBar(context.getPipelineName(),stormConfig,builder.createTopology());
            } catch (AlreadyAliveException | InvalidTopologyException e) {
                LOG.error(e.getMessage(),e);
            }
        }
    }
}