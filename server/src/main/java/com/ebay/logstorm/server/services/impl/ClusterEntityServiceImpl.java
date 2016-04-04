package com.ebay.logstorm.server.services.impl;

import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.server.entities.ClusterEntity;
import com.ebay.logstorm.server.services.ClusterEntityRepository;
import com.ebay.logstorm.server.services.ClusterEntityService;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component("clusterEntityService")
public class ClusterEntityServiceImpl implements ClusterEntityService {
    private final ClusterEntityRepository entityRepository;

    @Autowired
    public ClusterEntityServiceImpl(ClusterEntityRepository entityRepository){
        this.entityRepository = entityRepository;
    }

    @Override
    public ClusterEntity createCluster(ClusterEntity clusterEntity) {
        return entityRepository.save(clusterEntity);
    }

    @Override
    public ClusterEntity updateCluster(ClusterEntity clusterEntity) {
        return entityRepository.save(clusterEntity);
    }

    @Override
    public Integer deleteCluster(ClusterEntity clusterEntity) {
        return entityRepository.delete(clusterEntity);
    }

    @Override
    public Integer deleteClusterByName(String name) {
        return entityRepository.deleteByName(name);
    }

    @Override
    public ClusterEntity getClusterByUuidOrName(String uuid, String name) throws Exception {
        Preconditions.checkArgument(uuid!=null || name!=null,"uuid and name are both null");
        return entityRepository.findOneByUuidOrName(uuid,name).orElseThrow(()-> new PipelineException("No cluster found for uuid = "+uuid+" or name = '"+name+"'"));
    }
}
