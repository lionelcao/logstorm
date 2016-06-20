package com.ebay.logstorm.server.services;

import com.ebay.logstorm.server.entities.ClusterEntity;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

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
public interface ClusterEntityRepository extends Repository<ClusterEntity, String> {
    Optional<ClusterEntity> findOneByName(String name);
    Optional<ClusterEntity> findOneByUuid(String uuid);
    ClusterEntity save(ClusterEntity entity);
    Integer delete(ClusterEntity entity);
    Integer delete(String uuid);
    Integer deleteByName(String name);
    Optional<ClusterEntity> findOneByUuidOrName(String uuid, String name);
    List<ClusterEntity> findAll();
}