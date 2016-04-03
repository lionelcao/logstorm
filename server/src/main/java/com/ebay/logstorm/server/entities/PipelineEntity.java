package com.ebay.logstorm.server.entities;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.server.platform.ExecutionEnvironment;

import javax.persistence.*;
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
public class PipelineEntity extends BaseEntity{
    @Id
    private String uuid;

    @Column(unique = true,nullable = false)
    private String name;

    @Column(nullable = false)
    private String pipeline;

    @Column(nullable = true)
    private Properties properties;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private LogStormConstants.DeployMode deployMode = LogStormConstants.DeployMode.STANDALONE;

    @OneToOne
    private EnvironmentEntity deployEnvironment;

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

    public LogStormConstants.DeployMode getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(LogStormConstants.DeployMode deployMode) {
        this.deployMode = deployMode;
    }

    public EnvironmentEntity getDeployEnvironment() {
        return deployEnvironment;
    }

    public void setDeployEnvironment(EnvironmentEntity deployEnvironment) {
        this.deployEnvironment = deployEnvironment;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ExecutionEnvironment getPlatform() {
        throw new RuntimeException("Not implemented yet");
    }
}