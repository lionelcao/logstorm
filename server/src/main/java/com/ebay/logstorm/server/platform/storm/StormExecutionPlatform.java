package com.ebay.logstorm.server.platform.storm;

import backtype.storm.generated.Nimbus;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;
import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.runner.storm.StormPipelineRunner;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.platform.ExecutionManager;
import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstorm.server.platform.TaskExecutor;
import com.typesafe.config.ConfigFactory;
import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
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

    static {
        String pipelineJar = ConfigFactory.load().getString("pipeline.jar");
        LOG.info("Setting storm.jar as {}",pipelineJar);
        System.setProperty("storm.jar",pipelineJar);
    }

    @Override
    public void prepare(Properties properties) {
        runner = new StormPipelineRunner();
    }

    @Override
    public void start(final PipelineExecutionEntity entity) throws Exception {
        PipelineContext context = new PipelineContext(entity.getPipeline().getPipeline());

        context.setConfig(entity.getPipeline().getProperties());
        context.setDeployMode(entity.getPipeline().getMode());
        context.setPipelineName(entity.getPipeline().getName());
        Pipeline pipeline = PipelineCompiler.compile(context);

        switch (entity.getPipeline().getMode()){
            case LOCAL:
                TaskExecutor worker = startInLocalMode(pipeline);
                entity.setDescription("Running inside " + worker.toString() + " in " + entity.getPipeline().getMode() + " mode");
                entity.setProperty("executor.id", String.valueOf(worker.getId()));
                entity.setProperty("executor.name", worker.getName());
                entity.setProperty("executor.thread", worker.toString());
                entity.setUrl("/api/executor/" + entity.getName());
            case CLUSTER:
                startInRemoteMode(pipeline);
                status(entity);
         //   throw new IllegalStateException("State of "+entity.getPipeline().getName()+" is illegal: "+ entity.getPipeline().getMode());
        }
    }

    private void startInRemoteMode(Pipeline pipeline) {
        runner.run(pipeline);
    }

    private TaskExecutor startInLocalMode(Pipeline pipeline) {
        return ExecutionManager.getInstance().submit(pipeline.getContext().getPipelineName(), () -> {
                runner.run(pipeline);
        });
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
        } else {
            try {
                String topology_id_key = "topology.id";
                Map clusterConf = Utils.readStormConfig();
                clusterConf.putAll(Utils.readCommandLineOpts());
                Nimbus.Client client = NimbusClient.getConfiguredClient(clusterConf).getClient();

                String id = entity.getProperties() == null? null:entity.getProperties().getProperty(topology_id_key);

                if(id == null) {
                    for (TopologySummary topologySummary : client.getClusterInfo().get_topologies()) {
                        if (topologySummary.get_name().equals(entity.getPipeline().getName())) {
                            id = topologySummary.get_id();
                        }
                    }
                }

                if(id == null){
                    throw new IllegalStateException("Topology named "+entity.getPipeline().getName()+" is not found");
                } else {
                    TopologyInfo topologyInfo = client.getTopologyInfo(id);
                    entity.setProperty(topology_id_key, topologyInfo.get_id());
                    entity.setProperty("topology.name", topologyInfo.get_name());
                    entity.setProperty("topology.status", topologyInfo.get_status());
                    entity.setProperty("topology.uptime_secs", String.valueOf(topologyInfo.get_uptime_secs()));
                    entity.setProperty("topology.executors_size", String.valueOf(topologyInfo.get_executors_size()));
                    entity.setProperty("topology.errors_size", String.valueOf(topologyInfo.get_errors_size()));
                    entity.setName(topologyInfo.get_id());
                }
            }catch (NotAliveException ex){
                LOG.error("{} not alive",entity.getPipeline().getName(),ex);
                entity.setStatus(PipelineExecutionStatus.STOPPED);
            } catch (TException ex ){
                LOG.error("Failed to connect to nimbus through thrift",ex);
                throw ex;
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