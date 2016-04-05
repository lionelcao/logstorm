package com.ebay.logstorm.server.entities;

import com.ebay.logstorm.core.compiler.Pipeline;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Entity(name = "PipelineExecution")
public class PipelineExecutionEntity extends BaseEntity {
    @Id
    private String uuid;

    @Column(nullable = true,length = 10000)
    private Properties properties;

    @JsonIgnore
    @Transient
    private PipelineEntity pipeline;

    @Column
    private long createdTimestamp;

    @Column
    private long modifiedTimestamp;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private PipelineExecutionStatus status;

    @Column
    private String url;

    @Column(length = 10000)
    private String description;

    public PipelineExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineExecutionStatus status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setProperty(String key, String value) {
        if(this.properties == null) this.properties = new Properties();
        this.properties.setProperty(key,value);
    }

    @Override
    public void ensureDefault() {
        this.setStatus(PipelineExecutionStatus.UNDEPLOYED);
        this.setProperties(new Properties());
        if(this.createdTimestamp == 0) this.updateCreatedTimestamp();
        if(this.modifiedTimestamp == 0) this.updateModifiedTimestamp();
        super.ensureDefault();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void setModifiedTimestamp(long modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    public PipelineExecutionEntity updateCreatedTimestamp() {
        this.createdTimestamp = System.currentTimeMillis();
        return this;
    }

    public PipelineExecutionEntity updateModifiedTimestamp() {
        this.modifiedTimestamp = System.currentTimeMillis();
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonIgnore
    public PipelineEntity getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineEntity pipeline) {
        this.pipeline = pipeline;
    }
}