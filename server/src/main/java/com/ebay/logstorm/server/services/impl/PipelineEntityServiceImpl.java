package com.ebay.logstorm.server.services.impl;

import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.services.PipelineEntityRepository;
import com.ebay.logstorm.server.services.PipelineEntityService;
import com.ebay.logstorm.server.services.PipelineSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Component("pipelineEntityService")
@Transactional
public class PipelineEntityServiceImpl implements PipelineEntityService {
    private final PipelineEntityRepository pipelineEntityRepository;

    @Autowired
    public PipelineEntityServiceImpl(PipelineEntityRepository pipelineEntityRepository){
        this.pipelineEntityRepository = pipelineEntityRepository;
    }

    public Page<PipelineEntity> findAll(Pageable pageable) {
       return this.pipelineEntityRepository.findAll(pageable);
    }

    @Override
    public List<PipelineEntity> findAll() {
        return pipelineEntityRepository.findAll();
    }

    public Page<PipelineEntity> searchPipelines(PipelineSearchCriteria searchCriteria, Pageable pageable) {
        if(searchCriteria.getUuid()!=null) {
            return this.pipelineEntityRepository.findOneByUuid(searchCriteria.getUuid(), pageable);
        } if(searchCriteria.getName()!=null){
            return this.pipelineEntityRepository.findOneByName(searchCriteria.getName(),pageable);
        }else{
            return findAll(pageable);
        }
    }

    public PipelineEntity createPipeline(PipelineEntity pipelineEntity) {
        pipelineEntity.ensureDefault();
        return this.pipelineEntityRepository.save(pipelineEntity);
    }

    @Override
    public PipelineEntity updatePipeline(PipelineEntity pipelineEntity) {
        return this.pipelineEntityRepository.save(pipelineEntity);
    }

    @Override
    public Integer deletePipeline(PipelineEntity pipelineEntity) {
        if(pipelineEntity.getUuid()!=null){
            return this.pipelineEntityRepository.removeByUuid(pipelineEntity.getUuid());
        }else if(pipelineEntity.getName()!=null) {
            return this.pipelineEntityRepository.removeByName(pipelineEntity.getName());
        } else {
            throw new IllegalArgumentException("uuid and name are both null");
        }
    }

    @Override
    public Integer deletePipelineByUuid(String uuid) {
        return this.pipelineEntityRepository.removeByUuid(uuid);
    }

    private PipelineExecutionStatus statusPipeline(PipelineEntity pipelineEntity) throws Exception {
        if (pipelineEntity.getInstances() != null) {
            for (PipelineExecutionEntity instance : pipelineEntity.getInstances()) {
                if (instance.getStatus().equals(PipelineExecutionStatus.RUNNING)) {
                    return PipelineExecutionStatus.RUNNING;
                }
            }
        }
        return PipelineExecutionStatus.INITIALIZED;
    }

    @Override
    public Optional<PipelineEntity> getPipelineByUuid(String uuid) {
        return this.pipelineEntityRepository.findOneByUuid(uuid);
    }

    @Override
    public PipelineEntity getPipelineByIdOrThrow(String uuid) throws Exception {
        Optional<PipelineEntity> entity = getPipelineByUuid(uuid);
        if(entity.isPresent()){
            return entity.get();
        }else{
            throw new PipelineException("Pipeline [uuid='"+uuid+"'] not found");
        }
    }

    @Override
    public PipelineEntity getPipelineOrThrow(PipelineEntity pipeline) throws Exception {
        return pipelineEntityRepository.findOneByUuidOrName(pipeline.getUuid(),pipeline.getName()).orElseThrow(()-> new PipelineException("Pipeline ["+pipeline+"] not found"));
    }

    @Override
    public PipelineEntity getPipelineByUuidOrNameOrThrow(String uuid,String name) throws Exception {
        PipelineEntity pipelineEntity = pipelineEntityRepository.findOneByUuidOrName(uuid, name).orElseThrow(()-> new PipelineException("Pipeline [uuid='"+uuid+"' or name='"+name+"'] not found"));
        return pipelineEntity;
    }
}