package com.ebay.logstorm.server.services.impl;

import com.ebay.logstorm.core.exception.PipelineExecutionException;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.platform.ExecutionManager;
import com.ebay.logstorm.server.services.PipelineEntityService;
import com.ebay.logstorm.server.services.PipelineExecutionRepository;
import com.ebay.logstorm.server.services.PipelineExecutionService;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        if(pipeline.getInstances() == null || pipeline.getInstances().size() == 0) {
            LOG.info("{} has not been deployed yet, deploying now", pipeline);
            List<PipelineExecutionEntity> executors = new ArrayList<>(pipeline.getParallelism());
            for(int i =0;i<pipeline.getParallelism();i++) {
                PipelineExecutionEntity instance = new PipelineExecutionEntity();
                instance.setNumber(i);
                instance.setName(String.format("%s_%s",pipeline.getName(),instance.getNumber()));
                instance.setPipeline(pipeline);
                executors.add(this.createExecutionEntity(instance));
            }
            pipeline.setInstances(executors);
            entityService.updatePipeline(pipeline);
            LOG.info("Initialized [] executors for pipeline {}", executors.size(),pipeline);
        }
    }

    @Override
    public PipelineEntity start(PipelineEntity pipeline) throws Exception {
        checkInitExecutionContext(pipeline);
        for(PipelineExecutionEntity executor: pipeline.getInstances()){
            boolean readyToStart = PipelineExecutionStatus.isReadyToStart(executor.getStatus());
            if(!readyToStart){
                throw new PipelineExecutionException(pipeline+" is not ready to start, executor "+executor.getName()+" is still running");
            }
        }
        pipeline.getInstances().stream().map((executor) -> ExecutionManager.getInstance().submit(()-> {
            try {
                executor.setStatus(PipelineExecutionStatus.STARTING);
                updateExecutionEntity(executor);
                pipeline.getCluster().getPlatformInstance().start(executor);
                executor.setStatus(PipelineExecutionStatus.RUNNING);
                updateExecutionEntity(executor);
            } catch (Exception ex) {
                executor.setStatus(PipelineExecutionStatus.FAILED);
                executor.setDescription(ExceptionUtils.getStackTrace(ex));
                updateExecutionEntity(executor);
                LOG.error(ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        })).forEach((future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }));
        return pipeline;
    }

    @Override
    public PipelineEntity start(String pipelineId) throws Exception {
        return start(entityService.getPipelineByIdOrThrow(pipelineId));
    }

    @Override
    public PipelineEntity stop(PipelineEntity pipeline) {
        pipeline.getInstances().stream().map((instance)-> ExecutionManager.getInstance().submit(()->{
            if(PipelineExecutionStatus.isReadyToStop(instance.getStatus())) {
                try {
                    instance.setStatus(PipelineExecutionStatus.STOPPING);
                    updateExecutionEntity(instance);
                    pipeline.getCluster().getPlatformInstance().stop(instance);
                    instance.setStatus(PipelineExecutionStatus.STOPPED);
                    updateExecutionEntity(instance);
                } catch (Exception e) {
                    instance.setStatus(PipelineExecutionStatus.FAILED);
                    instance.setDescription(ExceptionUtils.getStackTrace(e));
                    updateExecutionEntity(instance);
                    LOG.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        })).forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        });
        return pipeline;
    }

    @Override
    public PipelineEntity rescale(PipelineEntity instance) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public PipelineEntity restart(PipelineEntity instance) {
        throw new RuntimeException("Not implemented yet");
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
    public Page<PipelineExecutionEntity> searchExecutionEntities(Pageable pageable) {
        return executionRespository.findAll(pageable);
    }

    @Override
    public PipelineEntity stop(String pipelineId) throws Exception {
        return stop(entityService.getPipelineByIdOrThrow(pipelineId));
    }
}