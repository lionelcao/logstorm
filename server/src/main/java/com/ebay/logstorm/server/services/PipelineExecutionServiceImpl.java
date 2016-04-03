package com.ebay.logstorm.server.services;

import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.core.exception.PipelineExecutionException;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Component("pipelineExecutionService")
@Transactional
public class PipelineExecutionServiceImpl implements PipelineExecutionService {
    private final PipelineExecutionRepository executionRespository;
    private final static Logger LOG = LoggerFactory.getLogger(PipelineExecutionServiceImpl.class);
    private final PipelineEntityRepository entityRepository;

    @Autowired
    public PipelineExecutionServiceImpl(PipelineEntityRepository entityRepository,PipelineExecutionRepository executionRespository){
        this.executionRespository = executionRespository;
        this.entityRepository = entityRepository;
    }

    /**
     * Deploy pipeline
     *
     * @param pipeline
     * @return
     * @throws PipelineExecutionException
     */
    @Override
    public PipelineExecutionEntity start(PipelineEntity pipeline) throws Exception {
        Preconditions.checkNotNull(pipeline);
        PipelineExecutionEntity executionEntity = new PipelineExecutionEntity();
        executionEntity.setPipeline(pipeline);
        executionEntity.setDescription("Initially created for pipeline "+pipeline);
        executionEntity.ensureDefault();
        LOG.info("Creating new PipelineExecutionEntity: {}",executionEntity);
        return start(saveExecutionEntity(executionEntity));
    }

    /**
     * Check pipeline execution firstly, if exist try start
     * If not exists, create a new pipeline execution entity and start
     * @param pipelineId
     * @return
     */
    @Override
    public PipelineExecutionEntity start(String pipelineId) throws Exception{
        Preconditions.checkNotNull(pipelineId);
        PipelineExecutionEntity executionEntity = null;
        Optional<PipelineExecutionEntity> option = this.getExecutionEntityByPipelineId(pipelineId);
        if(option.isPresent()){
            LOG.info("Pipeline[uuid='{}'] has already been deployed as: {}"+option);
            executionEntity = option.get();
            return start(executionEntity);
        } else {
            LOG.info("Pipeline[uuid='{}'] has not been deployed yet, deploying now");
            return start(entityRepository.findOneByUuid(pipelineId).orElseThrow(()-> new PipelineException("Pipeline [uuid='"+pipelineId+"'] not found")));
        }
    }

    @Override
    public PipelineExecutionEntity start(PipelineExecutionEntity executionEntity) throws Exception {
        Preconditions.checkNotNull(executionEntity);
        if(PipelineExecutionStatus.isReadyToStart(executionEntity.getStatus())){
            try {
                executionEntity.setStatus(PipelineExecutionStatus.STARTING);
                updateExecutionEntity(executionEntity);
                executionEntity.getPipeline().getDeployEnvironment().getScheduleService().start(executionEntity);
                executionEntity.setStatus(PipelineExecutionStatus.RUNNING);
            } catch (Exception ex){
                executionEntity.setStatus(PipelineExecutionStatus.FAILED);
                executionEntity.setDescription(ExceptionUtils.getStackTrace(ex));
            }
            return updateExecutionEntity(executionEntity);
        } else {
            throw new PipelineExecutionException(executionEntity+" is not ready to start, current status is "+executionEntity.getStatus());
        }
    }

    @Override
    public PipelineExecutionEntity stop(PipelineExecutionEntity instance) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public PipelineExecutionEntity rescale(PipelineExecutionEntity instance) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public PipelineExecutionEntity restart(PipelineExecutionEntity instance) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public PipelineExecutionEntity submitOnly(PipelineEntity pipeline) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<PipelineExecutionEntity> getExecutionEntityByPipelineId(String uuid) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<PipelineExecutionEntity> getExecutionEntityByPipeline(PipelineEntity pipelineEntity) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public PipelineExecutionEntity saveExecutionEntity(PipelineExecutionEntity pipelineExecutionEntity) {
        if(pipelineExecutionEntity.getCreatedTimestamp() == 0){
            pipelineExecutionEntity.updateCreatedTimestamp();
        }
        if(pipelineExecutionEntity.getModifiedTimestamp() == 0){
            pipelineExecutionEntity.updateCreatedTimestamp();
        }
        return executionRespository.save(pipelineExecutionEntity);
    }

    @Override
    public PipelineExecutionEntity updateExecutionEntity(PipelineExecutionEntity pipelineExecutionEntity) {
        pipelineExecutionEntity.updateCreatedTimestamp();
        return executionRespository.update(pipelineExecutionEntity);
    }

    @Override
    public PipelineExecutionEntity deleteExecutionEntity(PipelineExecutionEntity pipelineExecutionEntity) {
        throw new RuntimeException("Not implemented yet");
    }
}