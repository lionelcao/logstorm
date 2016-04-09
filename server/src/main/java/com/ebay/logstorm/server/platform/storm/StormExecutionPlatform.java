package com.ebay.logstorm.server.platform.storm;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.runner.storm.StormPipelineRunner;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.platform.ExecutionManager;
import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstorm.server.platform.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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
public class StormExecutionPlatform implements ExecutionPlatform {
    private final static Logger LOG = LoggerFactory.getLogger(StormExecutionPlatform.class);

    private StormPipelineRunner runner;
    @Override
    public void init(Properties properties) {
        runner = new StormPipelineRunner();
    }

    @Override
    public void start(final PipelineExecutionEntity entity) throws Exception {
        if(entity.getPipeline().getMode().equals(LogStormConstants.DeployMode.LOCAL)) {
            TaskExecutor worker = ExecutionManager.getInstance().submit(entity.getName(), () -> {
                try {
                    runner.run(PipelineCompiler.compileConfigString(entity.getPipeline().getPipeline()));
                } catch (PipelineException e) {
                    throw new RuntimeException(e);
                }
            });
            entity.setDescription("Running inside " + worker.toString() + " in " + entity.getPipeline().getMode() + " mode");
            entity.setProperty("executor.id", String.valueOf(worker.getId()));
            entity.setProperty("executor.name", worker.getName());
            entity.setProperty("executor.thread", worker.toString());
            entity.setProperty("executor.state", worker.getState().toString());
            entity.setUrl("/api/executor/" + entity.getName());
        }
    }

    @Override
    public void stop(final PipelineExecutionEntity entity) throws Exception {
        if(entity.getPipeline().getMode().equals(LogStormConstants.DeployMode.LOCAL)) {
            ExecutionManager.getInstance().stop(entity.getName());
            entity.setDescription("Stopped");
            entity.setStatus(PipelineExecutionStatus.STOPPED);
            ExecutionManager.getInstance().remove(entity.getName());
        }
    }

    @Override
    public void status(final PipelineExecutionEntity entity) throws Exception {
        if (LogStormConstants.DeployMode.LOCAL.equals(entity.getPipeline().getMode())) {
            PipelineExecutionStatus currentStatus = entity.getStatus();
            PipelineExecutionStatus newStatus = ExecutionManager.getWorkerStatus(ExecutionManager.getInstance().get(entity.getName()).getState());
            if (!currentStatus.equals(newStatus)) {
                LOG.info("Status of pipeline: {} changed from {} to {}", entity, currentStatus, newStatus);
                entity.setStatus(newStatus);
                entity.setDescription(String.format("Status of pipeline: %s changed from %s to %s", entity, currentStatus, newStatus));
            }
        }
    }

    @Override
    public String getType() {
        return "storm";
    }

    @Override
    public String getVersion() {
        return "v0.8";
    }
}