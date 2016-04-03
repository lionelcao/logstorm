package com.ebay.logstorm.server.entities;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String uuid;

    @OneToOne @MapsId
    private PipelineEntity pipeline;

    @Column
    private Properties properties;

    @Column
    private long createdTimestamp;

    @Column
    private long modifiedTimestamp;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private PipelineExecutionStatus status;

    @Column
    private String trackingUrl = null;

    @Column
    private String description;

    public PipelineEntity getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineEntity pipeline) {
        this.pipeline = pipeline;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

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
        this.properties.setProperty(key,value);
    }

    @Override
    public void ensureDefault() {
        this.setStatus(PipelineExecutionStatus.INITIALIZED);
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
    public void updateCreatedTimestamp() {
        this.createdTimestamp = System.currentTimeMillis();
    }
    public void updateModifiedTimestamp() {
        this.modifiedTimestamp = System.currentTimeMillis();
    }
}