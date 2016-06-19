package com.ebay.logstorm.server.platform.storm;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstream.runner.spark.SparkPipelineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

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

public class SparkExecutionPlatform implements ExecutionPlatform {
    private final static Logger LOG = LoggerFactory.getLogger(SparkExecutionPlatform.class);
    private SparkPipelineRunner runner;

    @Override
    public void prepare(Properties properties) {
        runner = new SparkPipelineRunner();
    }

    @Override
    public void start(final PipelineExecutionEntity entity) throws Exception {
        PipelineContext context = new PipelineContext(entity.getPipeline().getPipeline());

        context.setConfig(entity.getPipeline().getProperties());
        context.setDeployMode(entity.getPipeline().getMode());
        context.setPipelineName(entity.getPipeline().getName());
        Pipeline pipeline = PipelineCompiler.compile(context);
        List<String> result = runner.run(pipeline);
        String applicationId = result.get(0);
        entity.setProperty("applicationId", applicationId);
    }

    @Override
    public void stop(final PipelineExecutionEntity entity) throws Exception {
        String applicationId = entity.getProperties().getProperty("applicationId");
        if (LogStormConstants.DeployMode.CLUSTER.equals(entity.getPipeline().getMode())) {
            //TODO
        } else {
            LOG.warn("spark only supports cluster mode");
        }
    }

    @Override
    public void status(final PipelineExecutionEntity entity) throws Exception {
        String applicationId = entity.getProperties().getProperty("applicationId");

        if (LogStormConstants.DeployMode.CLUSTER.equals(entity.getPipeline().getMode())) {
            //TODO
        } else {
            LOG.warn("spark only supports cluster mode");
        }

    }

    @Override
    public String getType() {
        return "spark";
    }

    @Override
    public String getVersion() {
        return "v1.6.1";
    }
}
