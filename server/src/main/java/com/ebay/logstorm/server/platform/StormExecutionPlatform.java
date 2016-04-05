package com.ebay.logstorm.server.platform;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.runner.storm.StormPipelineRunner;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public String getTypeName() {
        return "storm";
    }

    @Override
    public void start(PipelineEntity entity) throws Exception {
        if(entity.getMode().equals(LogStormConstants.DeployMode.LOCAL)) {
            TaskExecutor worker = ExecutionManager.getInstance().submit(buildExecutorId(entity), () -> {
                try {
                    runner.run(PipelineCompiler.compileConfigString(entity.getPipeline()));
                } catch (PipelineException e) {
                    throw new RuntimeException(e);
                }
            });
            entity.getExecution().setDescription("Running inside "+worker.toString()+" in "+entity.getMode()+" mode");
            entity.getExecution().setProperty("worker.id",String.valueOf(worker.getId()));
            entity.getExecution().setProperty("worker.name",worker.getName());
            entity.getExecution().setProperty("worker.thread",worker.toString());
            entity.getExecution().setProperty("worker.state",worker.getState().toString());
            entity.getExecution().setProperty("worker.stackTrace",worker.getStackTrace().toString());
        }
    }

    private String buildExecutorId(PipelineEntity entity){
        return "PipelineWorker["+entity.getName()+"]";
    }

    @Override
    public void stop(PipelineEntity entity) throws Exception {
        if(entity.getMode().equals(LogStormConstants.DeployMode.LOCAL)) {
            ExecutionManager.getInstance().stop(entity.getName());
            entity.getExecution().setDescription("Stopped");
        }
    }

    @Override
    public void status(PipelineEntity entity) throws Exception {
        if(entity.getMode().equals(LogStormConstants.DeployMode.LOCAL)) {
            PipelineExecutionStatus currentStatus = entity.getExecution().getStatus();
            PipelineExecutionStatus newStatus = ExecutionManager.getWorkerStatus(ExecutionManager.getInstance().get(buildExecutorId(entity)).getState());
            if(!currentStatus.equals(newStatus)){
                LOG.info("Status of pipeline: {} changed from {} to {}",entity,currentStatus,newStatus);
                entity.getExecution().setStatus(newStatus);
                entity.getExecution().setDescription(String.format("Status of pipeline: %s changed from %s to %s",entity,currentStatus,newStatus));
            }
        }
    }
}