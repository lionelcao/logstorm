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
import com.ebay.logstorm.core.compiler.*;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.google.common.collect.Maps;
import org.apache.spark.SparkConf;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SparkPipelineRunner implements PipelineRunner {
    private final static Logger LOG = LoggerFactory.getLogger(SparkPipelineRunner.class);
    public static final String SPARK_HOME_KEY="spark.home";
    public static final String SPARK_MASTER_KEY="spark.master";
    public static final String SPARK_DRIVER_KEY="spark.driver";
    public static final String JAVA_HOME="java.home";

    @Override
    public Map<String, Object> run(Pipeline pipeline) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> env = Maps.newHashMap();
        env.put("SPARK_PRINT_LAUNCH_COMMAND", "1");
        SparkLauncher launcher = new SparkLauncher(env);
        launcher.setAppResource(pipeline.getContext().getPipelineJarPath());
        launcher.setAppName(pipeline.getContext().getPipelineName());
        launcher.setMainClass(SparkPipelineRunner.class.getCanonicalName());
        launcher.setSparkHome(pipeline.getContext().getConfig().getString(SPARK_HOME_KEY));
        launcher.setJavaHome(pipeline.getContext().getConfig().getString(JAVA_HOME));
        //set app args
        launcher.addAppArgs(pipeline.getContext().getPipeline());
        launcher.addAppArgs(pipeline.getContext().getPipelineName());
        launcher.addAppArgs(pipeline.getContext().getDeployMode().toString());
        launcher.addAppArgs(pipeline.getContext().getInputParallelism() + "");
        launcher.addAppArgs(pipeline.getContext().getFilterParallelism() + "");
        launcher.addAppArgs(pipeline.getContext().getOutputParallelism() + "");
        //work around(for get driver pid)
        String uuid = UUID.randomUUID().toString();
        launcher.addAppArgs(uuid);
        launcher.addAppArgs();
        launcher.setVerbose(true);
        launcher.addSparkArg("--verbose");
        if (pipeline.getContext().getDeployMode() == LogStormConstants.DeployMode.LOCAL) {
            launcher.setMaster("local[*]");
        } else {
            launcher.setMaster(pipeline.getContext().getConfig().getString(SPARK_MASTER_KEY));
        }

        try {
            SparkAppHandle handle = launcher.startApplication();
            while (handle.getAppId() == null) {
                Thread.sleep(1000);
            }
            result.put("applicationId", handle.getAppId());
            LOG.info("generate spark applicationId " + handle.getAppId());
            //get driver pid
            String cmd = "ps -ef | grep " + uuid + " | grep -v grep | awk '{print $2}'";
            LOG.info("cmd {}", cmd);
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd});
            synchronized (process) {
                try {
                    process.wait();
                } catch (Exception e) {
                    LOG.warn("failed to wait driver pid: ", e);
                }
            }
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String pid;
            while ((pid = bufferedReader.readLine()) != null) {
                result.put("driverPid", pid);
                System.out.println(pid);
            }
            bufferedReader.close();
        } catch (Exception e) {
            LOG.error("failed to start as a spark application, ", e);
        }

        return result;
    }

    public static void main(String[] args) {
        String pipeLine = args[0];
        String pipeLineName = args[1];
        String mode = args[2];
        int intputParallelism = Integer.parseInt(args[3]);
        int filterParallelism = Integer.parseInt(args[4]);
        int outputParallelism = Integer.parseInt(args[5]);
        try {
            PipelineContext context = new PipelineContext(pipeLine);
            context.setDeployMode(mode);
            context.setPipelineName(pipeLineName);
            Pipeline pipeline = PipelineCompiler.compile(context);

            List<InputPlugin> inputs = pipeline.getInputs();
            List<FilterPlugin> filters = pipeline.getFilters();
            List<OutputPlugin> outputs = pipeline.getOutputs();

            SparkConf conf = new SparkConf();
            JavaStreamingContext jsc = new JavaStreamingContext(conf, new Duration(5000));
            List<JavaDStream<byte[]>> streams = new ArrayList<>();
            for (InputPlugin inputPlugin : inputs) {
                for (int i = 0; i < intputParallelism; i++) {
                    LogStashSparkReceiver receiver = new LogStashSparkReceiver(i, inputPlugin, context);
                    streams.add(jsc.receiverStream(receiver));
                }
            }

            JavaDStream<byte[]> stream = jsc.union(streams.get(0), streams.subList(1, streams.size()));
            JavaDStream<byte[]> filterStream = stream;
            for (FilterPlugin filterPlugin : filters) {
                filterStream = filterStream
                        .repartition(filterParallelism)
                        .map(new FilterFunction(filterPlugin, context));
            }

            for (OutputPlugin output : outputs) {
                filterStream
                        .repartition(outputParallelism)
                        .foreach(new ForEachFunction(output, context));
            }

            jsc.start();
            jsc.awaitTermination();
        } catch (Exception e) {
            LOG.error("failed to run spark application: ", e);
        }
    }
}
