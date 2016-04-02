package com.ebay.logstorm.server.services;

import com.ebay.logstorm.server.entities.PipelineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Component("pipelineService")
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

    public Page<PipelineEntity> findPipelines(PipelineEntitySearchCriteria searchCriteria, Pageable pageable) {
        if(searchCriteria.getId()!=null) {
            return this.pipelineEntityRepository.findByUuid(searchCriteria.getId(),pageable);
        }else{
            return findAll(pageable);
        }
    }

    public PipelineEntity createPipeline(PipelineEntity pipelineEntity) {
        return this.pipelineEntityRepository.save(pipelineEntity);
    }
}