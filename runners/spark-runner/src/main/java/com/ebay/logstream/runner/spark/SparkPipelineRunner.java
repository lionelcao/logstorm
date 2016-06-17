package com.ebay.logstream.runner.spark;

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

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.FilterPlugin;
import com.ebay.logstorm.core.compiler.InputPlugin;
import com.ebay.logstorm.core.compiler.OutputPlugin;
import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.runner.PipelineRunner;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SparkPipelineRunner implements PipelineRunner {
    private final static Logger LOG = LoggerFactory.getLogger(SparkPipelineRunner.class);

    @Override
    public void run(Pipeline pipeline) {
        PipelineContext context = pipeline.getContext();
        List<InputPlugin> inputs = pipeline.getInputs();
        List<FilterPlugin> filters = pipeline.getFilters();
        List<OutputPlugin> outputs = pipeline.getOutputs();

        SparkConf conf = new SparkConf();
        if (pipeline.getContext().getDeployMode() == LogStormConstants.DeployMode.LOCAL) {
            conf.setMaster("local[10]").setAppName(pipeline.getContext().getPipelineName());
        } else {

        }

        JavaStreamingContext sc = new JavaStreamingContext(conf, new Duration(15000));
        List<JavaDStream<byte[]>> streams = new ArrayList<>();
        for (InputPlugin inputPlugin : inputs) {
            for (int i = 0; i < inputPlugin.getParallelism() + 1; i++) {
                LogStashSparkReceiver receiver = new LogStashSparkReceiver(i, inputPlugin, context);
                streams.add(sc.receiverStream(receiver));
            }
        }

        JavaDStream<byte[]> stream = sc.union(streams.get(0), streams.subList(1, streams.size()));
        JavaDStream<byte[]> filterStream = stream;
        for (FilterPlugin filterPlugin : filters) {
            filterStream = filterStream
                    .repartition((int)filterPlugin.getParallelism() + 3)
                    .map(new FilterFunction(filterPlugin, context));
        }

        for (OutputPlugin output : outputs) {
            filterStream
                    .repartition((int)output.getParallelism() + 10)
                    .foreach(new ForEachFunction(output, context));
        }

        sc.start();
        sc.awaitTermination();
    }
}
