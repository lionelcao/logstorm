package com.ebay.logstorm.server.entities;

import com.ebay.logstorm.core.LogStormConstants;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
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
@Entity(name = "Pipeline")
public class PipelineEntity extends BaseEntity {
    @Id
    private String uuid;

    @Column(unique = true,nullable = false)
    private String name;

    @Column(nullable = false)
    private String pipeline;

    @Column(nullable = true,length = 10000)
    private Properties properties;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<PipelineExecutionEntity> executors;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private LogStormConstants.DeployMode mode = LogStormConstants.DEFAULT_DEPLOY_MODE;

    @OneToOne
    private ClusterEntity cluster;

    @Column
    private int parallelism = 1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public LogStormConstants.DeployMode getMode() {
        return mode;
    }

    public void setMode(LogStormConstants.DeployMode mode) {
        this.mode = mode;
    }

    public ClusterEntity getCluster() {
        return cluster;
    }

    public void setCluster(ClusterEntity cluster) {
        this.cluster = cluster;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setProperty(String key, String value) {
        if(this.properties == null){
            this.properties = new Properties();
        }
        this.properties.setProperty(key,value);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

//    public PipelineExecutionEntity getExecution() {
//        return execution;
//    }
//
//    public void setExecution(PipelineExecutionEntity executionContext) {
//        this.execution = executionContext;
//        if(this.execution!=null) {
//            this.execution.setPipeline(this);
//        }
//    }

    @Override
    public String toString() {
        return String.format("Pipeline[uuid=%s, name=%s]",this.getUuid(),this.getName());
    }

    public Collection<PipelineExecutionEntity> getExecutors() {
        return executors;
    }

    public void setExecutors(Collection<PipelineExecutionEntity> executions) {
        this.executors = executions;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    @Override
    public void ensureDefault() {
        super.ensureDefault();
        if(this.getExecutors()==null) {
            this.setExecutors(Collections.emptyList());
        }
    }
}