package com.ebay.logstorm.server.services;

import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.server.entities.PipelineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;
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
public interface PipelineEntityService {
    Page<PipelineEntity> findAll(Pageable pageable);
    List<PipelineEntity> findAll();
    Page<PipelineEntity> searchPipelines(PipelineSearchCriteria searchCriteria, Pageable pageable);
    PipelineEntity createPipeline(PipelineEntity pipelineEntity);
    PipelineEntity updatePipeline(PipelineEntity pipelineEntity);
    Integer deletePipeline(PipelineEntity pipelineEntity);

    Optional<PipelineEntity> getPipelineByUuid(String uuid);
    PipelineEntity getPipelineByIdOrThrow(String uuid) throws Exception;
    PipelineEntity getPipelineOrThrow(PipelineEntity pipeline) throws Exception;

    PipelineEntity getPipelineByUuidOrNameOrThrow(String uuid,String name) throws Exception;
}