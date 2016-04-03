package com.ebay.logstorm.server.services;

import com.ebay.logstorm.server.entities.PipelineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Optional<PipelineEntity> getPipelineByUuid(String uuid) {
        return this.pipelineEntityRepository.findOneByUuid(uuid);
    }
}