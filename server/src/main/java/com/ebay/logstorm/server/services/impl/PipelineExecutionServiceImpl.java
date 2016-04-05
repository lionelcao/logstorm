package com.ebay.logstorm.server.services.impl;

import com.ebay.logstorm.core.exception.PipelineExecutionException;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.services.PipelineEntityService;
import com.ebay.logstorm.server.services.PipelineExecutionRepository;
import com.ebay.logstorm.server.services.PipelineExecutionService;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Component("pipelineExecutionService")
public class PipelineExecutionServiceImpl implements PipelineExecutionService {
    private final PipelineExecutionRepository executionRespository;
    private final static Logger LOG = LoggerFactory.getLogger(PipelineExecutionServiceImpl.class);
    private final PipelineEntityService entityService;

    @Autowired
    public PipelineExecutionServiceImpl(PipelineEntityService entityService,PipelineExecutionRepository executionRespository){
        this.entityService = entityService;
        this.executionRespository = executionRespository;
    }

    @Transactional
    private void checkInitExecutionContext(PipelineEntity pipeline){
        Preconditions.checkNotNull(pipeline,"pipeline is null: "+pipeline);
        Preconditions.checkNotNull(pipeline.getCluster(),"cluster is null: "+ pipeline);
        Preconditions.checkNotNull(pipeline.getCluster().getPlatformInstance(),"platform instance is null: "+pipeline);
        if(pipeline.getExecution() == null) {
            LOG.info("{} has not been deployed yet, deploying now",pipeline);
            pipeline.setExecution(this.createExecutionEntity(new PipelineExecutionEntity()));
            entityService.updatePipeline(pipeline);
        }
    }

    @Override
    public PipelineEntity start(PipelineEntity pipeline) throws Exception {
        checkInitExecutionContext(pipeline);
        if(PipelineExecutionStatus.isReadyToStart(pipeline.getExecution().getStatus())){
            try {
                pipeline.getExecution().setStatus(PipelineExecutionStatus.STARTING);
                updateExecutionEntity(pipeline.getExecution());
                pipeline.getCluster().getPlatformInstance().start(pipeline);
                pipeline.getExecution().setStatus(PipelineExecutionStatus.RUNNING);
                updateExecutionEntity(pipeline.getExecution());
            } catch (Exception ex){
                pipeline.getExecution().setStatus(PipelineExecutionStatus.FAILED);
                pipeline.getExecution().setDescription(ExceptionUtils.getStackTrace(ex));
                updateExecutionEntity(pipeline.getExecution());
                LOG.error(ex.getMessage(),ex);
                throw ex;
            }
            return pipeline;
        } else {
            throw new PipelineExecutionException(pipeline+" is not ready to start, current status is "+pipeline.getExecution().getStatus());
        }
    }

    @Override
    public PipelineEntity start(String pipelineId) throws Exception {
        return start(entityService.getPipelineByIdOrThrow(pipelineId));
    }

    @Override
    public PipelineEntity stop(PipelineEntity pipeline) {
        return null;
    }

    @Override
    public PipelineEntity rescale(PipelineEntity instance) {
        return null;
    }

    @Override
    public PipelineEntity restart(PipelineEntity instance) {
        return null;
    }

    @Override
    public PipelineEntity restart(String pipelineId) throws Exception {
        return restart(entityService.getPipelineByIdOrThrow(pipelineId));
    }

    @Override
    @Transactional
    public PipelineExecutionEntity updateExecutionEntity(PipelineExecutionEntity executionEntity) {
        executionEntity.updateModifiedTimestamp();
        return executionRespository.save(executionEntity);
    }

    @Override
    @Transactional
    public PipelineExecutionEntity createExecutionEntity(PipelineExecutionEntity executionEntity) {
        executionEntity.ensureDefault();
        return executionRespository.save(executionEntity);
    }

    @Override
    public PipelineEntity stop(String pipelineId) throws Exception {
        return stop(entityService.getPipelineByIdOrThrow(pipelineId));
    }
}