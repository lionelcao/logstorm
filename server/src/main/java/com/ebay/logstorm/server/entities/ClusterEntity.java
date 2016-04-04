package com.ebay.logstorm.server.entities;

import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstorm.server.platform.ExecutionPlatformFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

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
@Entity(name = "Cluster")
public class ClusterEntity extends BaseEntity{
    @Id
    private String uuid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String adapterClass;

    @Column(nullable = true)
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getType() {
        if(getPlatformInstance()!=null){
            return getPlatformInstance().getTypeName();
        }else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonIgnore
    public ExecutionPlatform getPlatformInstance() {
        if(perInstanceCache == null && getAdapterClass()!=null) {
            perInstanceCache = ExecutionPlatformFactory.newPlatformInstance(getAdapterClass(), this.getProperties());
        }
        return perInstanceCache;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        try {
            Preconditions.checkArgument(ExecutionPlatform.class.isAssignableFrom(Class.forName(adapterClass)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.adapterClass = adapterClass;
    }

    @JsonIgnore
    @Transient
    private ExecutionPlatform perInstanceCache;
}