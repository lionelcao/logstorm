package com.ebay.logstorm.server.services.impl;

import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.services.PipelineEntityService;
import com.ebay.logstorm.server.services.PipelineExecutionService;
import com.ebay.logstorm.server.services.PipelineStatusSyncService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Component("pipelineStatusSyncService")
@Service
public class StatusSyncServiceImpl implements PipelineStatusSyncService {
    private final static Logger LOG = LoggerFactory.getLogger(StatusSyncServiceImpl.class);
    private final PipelineEntityService entityService;
    private final PipelineExecutionService executionService;

    @Autowired
    public StatusSyncServiceImpl(PipelineEntityService entityService, PipelineExecutionService executionService){
        this.entityService = entityService;
        this.executionService =executionService;
    }

    @Override
    public void doUpdate() {
        List<PipelineEntity> allPipelineEntities = entityService.findAll();
        LOG.info("Checking status of {} pipelines",allPipelineEntities.size());
        for (PipelineEntity pipelineEntity : allPipelineEntities) {
            if (pipelineEntity.getInstances() != null) {
                pipelineEntity.getInstances().forEach((instance) ->{
                    if(instance.getStatus() == PipelineExecutionStatus.STARTING || instance.getStatus() == PipelineExecutionStatus.STOPPING ) {
                        LOG.info("Status of {} is {}, skip check status",instance.getName(),instance.getStatus());
                        return;
                    }
                    try {
                        instance.setNeedUpdate(false);
                        LOG.info("Checking status of instance '{}'", instance.getName());
                        pipelineEntity.getCluster().getPlatformInstance().status(instance);
                    } catch (Exception e) {
                        LOG.error(ExceptionUtils.getStackTrace(e));
                        instance.setStatus(PipelineExecutionStatus.FAILED);
                        instance.setDescription(ExceptionUtils.getMessage(e));
                        instance.setNeedUpdate(true);
                    } finally {
                        if (instance.isNeedUpdate()) {
                            executionService.updateExecutionEntity(instance);
                            LOG.info("Updated status of instance '{}'", instance.getName());
                        }
                    }
                });
            } else {
                LOG.info("'{}'  has not been deployed yet, skipped status checking",pipelineEntity.getName());
            }
        }
        LOG.info("Updated status of {} pipelines",allPipelineEntities.size());
    }
}