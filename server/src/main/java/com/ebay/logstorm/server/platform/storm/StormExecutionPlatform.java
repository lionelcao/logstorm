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
package com.ebay.logstorm.server.platform.storm;

import backtype.storm.generated.*;
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

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * TODO: Build StormConfig from ClusterEntity properties instead of using default
 */
public class StormExecutionPlatform implements ExecutionPlatform {
    private final static Logger LOG = LoggerFactory.getLogger(StormExecutionPlatform.class);
    private StormPipelineRunner runner;

    static {
        if(System.getProperty("storm.jar") == null) {
            String pipelineJar = ConfigFactory.load().getString("pipeline.jar");
            LOG.info("Setting storm.jar as {}", pipelineJar);
            System.setProperty("storm.jar", pipelineJar);
        }
    }

    @Override
    public void prepare(Properties properties) {
        runner = new StormPipelineRunner();
    }

    @Override
    public synchronized void start(final PipelineExecutionEntity entity) throws Exception {
        PipelineContext context = new PipelineContext(entity.getPipeline().getPipeline());
        context.setConfig(entity.getPipeline().getCluster().getProperties());
        context.setDeployMode(entity.getPipeline().getMode());
        context.setPipelineName(entity.getPipeline().getName());
        Pipeline pipeline = PipelineCompiler.compile(context);

        switch (entity.getPipeline().getMode()){
            case LOCAL:
                TaskExecutor worker = startInLocalMode(entity.getName(),pipeline);
                entity.setDescription("Running inside " + worker.toString() + " in " + entity.getPipeline().getMode() + " mode");
                entity.setProperty("executor.id", String.valueOf(worker.getId()));
                entity.setProperty("executor.name", worker.getName());
                entity.setProperty("executor.thread", worker.toString());
                entity.setUrl("/api/executor/" + entity.getName());
                break;
            case CLUSTER:
                startInRemoteMode(pipeline);
                break;
        }
        status(entity);
    }

    private void startInRemoteMode(Pipeline pipeline) {
        runner.run(pipeline);
    }

    private TaskExecutor startInLocalMode(String instanceName,Pipeline pipeline) {
        return ExecutionManager.getInstance().submit(instanceName, () -> {
                runner.run(pipeline);
        });
    }

    @Override
    public synchronized void stop(final PipelineExecutionEntity entity) throws Exception {
        if(entity.getPipeline().getMode().equals(LogStormConstants.DeployMode.LOCAL)) {
            ExecutionManager.getInstance().stop(entity.getName());
            entity.setDescription("Stopped");
            entity.setStatus(PipelineExecutionStatus.STOPPED);
            ExecutionManager.getInstance().remove(entity.getName());
        } else {
            Map clusterConf = Utils.readStormConfig();
            clusterConf.putAll(Utils.readCommandLineOpts());
            Nimbus.Client client = NimbusClient.getConfiguredClient(clusterConf).getClient();
            client.killTopology(entity.getName());
            entity.setDescription("Stopped");
            entity.setStatus(PipelineExecutionStatus.STOPPED);
        }
    }

    private final static String topology_id_key = "topology.id";

    @Override
    public synchronized void status(final PipelineExecutionEntity entity) throws Exception {
        if (LogStormConstants.DeployMode.LOCAL.equals(entity.getPipeline().getMode())) {
            if(!ExecutionManager.getInstance().contains(entity.getName())){
                LOG.info("Pipeline instance '{}' is not ready yet",entity.getName());
            }
            PipelineExecutionStatus currentStatus = entity.getStatus();
            PipelineExecutionStatus newStatus = ExecutionManager.getWorkerStatus(ExecutionManager.getInstance().get(entity.getName()).getState());
            if (!currentStatus.equals(newStatus)) {
                LOG.info("Status of pipeline: {} changed from {} to {}", entity, currentStatus, newStatus);
                entity.setStatus(newStatus);
                entity.setDescription(String.format("Status of pipeline: %s changed from %s to %s", entity, currentStatus, newStatus));
            }
        } else {
            try {
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
                    Map<String,List<ErrorInfo>> errors = topologyInfo.get_errors();
                    StringBuilder sb = new StringBuilder();

                    int errorInfoSize = 0;
                    if(topologyInfo.get_errors_size()>0) {
                        for (Map.Entry<String, List<ErrorInfo>> entry : errors.entrySet()) {
                            sb.append(entry.getKey());
                            sb.append(": \n");
                            for (ErrorInfo errorInfo : entry.getValue()) {
                                errorInfoSize ++;
                                sb.append("\t");
                                sb.append(errorInfo.toString());
                                sb.append("\n");
                            }
                        }
                        if(errorInfoSize>0) {
                            LOG.error(sb.toString());
                            entity.setDescription(sb.toString());
                        }
                    }
                    entity.setName(topologyInfo.get_id());
                }
            }catch (NotAliveException ex){
                LOG.error("{} not alive",entity.getPipeline().getName(),ex);
                entity.setStatus(PipelineExecutionStatus.STOPPED);
                entity.setProperty("topology.status","NOT_ALIVE");
                entity.setDescription(ex.getMessage());
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