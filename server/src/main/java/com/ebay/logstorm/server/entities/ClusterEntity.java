package com.ebay.logstorm.server.entities;

import com.ebay.logstorm.server.platform.ExecutionPlatform;

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

    /**
     * ExecutionEnvironment type class name
     *
     * @see ExecutionPlatform
     */
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String className;

    @Column(nullable = true)
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
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

    public ExecutionPlatform getPlatform() {
        throw new RuntimeException("Not implemented");
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    protected void setType(String type) {
        this.type = type;
    }
}