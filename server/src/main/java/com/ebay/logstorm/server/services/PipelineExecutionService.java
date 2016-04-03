package com.ebay.logstorm.server.services;

import com.ebay.logstorm.core.exception.PipelineExecutionException;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;

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
public interface PipelineExecutionService {
    /**
     * @param pipelineEntity
     * @return
     */
    PipelineExecutionEntity start(PipelineEntity pipelineEntity) throws Exception;
    /**
     *
     * @param pipelineId
     * @return
     */
    PipelineExecutionEntity start(String pipelineId) throws Exception;

    /**
     *
     * @param executionEntity
     * @return
     */
    PipelineExecutionEntity start(PipelineExecutionEntity executionEntity) throws Exception;

    /**
     *
     * @param instance
     */
    PipelineExecutionEntity stop(PipelineExecutionEntity instance);

    /**
     * re-balance or scale pipeline according new configuration
     *
     * @param instance
     */
    PipelineExecutionEntity rescale(PipelineExecutionEntity instance);


    PipelineExecutionEntity restart(PipelineExecutionEntity instance);

    /**
     * Submit only and don't persiste any status
     *
     * @param pipeline
     * @return
     */
    PipelineExecutionEntity submitOnly(PipelineEntity pipeline);

    /**
     *
     * @param uuid
     * @return
     */
    Optional<PipelineExecutionEntity> getExecutionEntityByPipelineId(String uuid);

    /**
     *
     * @param pipelineEntity
     * @return
     */
    Optional<PipelineExecutionEntity> getExecutionEntityByPipeline(PipelineEntity pipelineEntity);
    PipelineExecutionEntity saveExecutionEntity(PipelineExecutionEntity pipelineExecutionEntity);
    PipelineExecutionEntity updateExecutionEntity(PipelineExecutionEntity pipelineExecutionEntity);
    PipelineExecutionEntity deleteExecutionEntity(PipelineExecutionEntity pipelineExecutionEntity);


}