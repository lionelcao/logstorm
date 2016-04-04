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
    PipelineEntity start(PipelineEntity pipelineEntity) throws Exception;

    /**
     *
     * @param pipelineId
     * @return
     */
    PipelineEntity start(String pipelineId) throws Exception;

    /**
     *
     * @param instance
     */
    PipelineEntity stop(PipelineEntity instance);
    PipelineEntity stop(String uuid) throws Exception;

    /**
     * re-balance or scale pipeline according new configuration
     *
     * @param instance
     */
    PipelineEntity rescale(PipelineEntity instance);
    PipelineEntity restart(PipelineEntity instance);
    PipelineEntity restart(String uuid) throws Exception;

    PipelineExecutionEntity updateExecutionEntity(PipelineExecutionEntity executionEntity);
    PipelineExecutionEntity createExecutionEntity(PipelineExecutionEntity executionEntity);
}