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
public class PipeineExecutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String uuid;

    @OneToOne
    private PipelineEntity pipeline;

    @Column
    private Properties properties;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private PipelineStatus status;

    @Column
    private String trackingUrl = null;

    /**
     * Used to locking
     */
    @Column
    private boolean locked = false;

    /**
     * Used for situation when locking is timeout
     */
    @Column
    private long lockedTimestamp;

    /**
     * Who is holding the lock
     */
    @Column
    private String lockHolder;

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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public PipelineStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineStatus status) {
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

    public long getLockedTimestamp() {
        return lockedTimestamp;
    }

    public void setLockedTimestamp(long lockedTimestamp) {
        this.lockedTimestamp = lockedTimestamp;
    }

    public String getLockHolder() {
        return lockHolder;
    }

    public void setLockHolder(String lockHolder) {
        this.lockHolder = lockHolder;
    }
}
