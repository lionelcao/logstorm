package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.services.PipelineEntityService;
import com.ebay.logstorm.server.services.PipelineExecutionService;
import com.ebay.logstorm.server.services.PipelineSearchCriteria;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
@Controller
@RequestMapping("/api/pipeline")
@CrossOrigin
public class PipelineController extends BaseController{
    private final static Logger LOG = LoggerFactory.getLogger(PipelineController.class);
    @Autowired
    private PipelineEntityService entityService;

    @Autowired
    private PipelineExecutionService executionService;

    @RequestMapping(method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<List<PipelineEntity>>> listPipelines(
            @RequestParam(required = false) String uuid,
            @RequestParam(required = false) String name,
            @PageableDefault(value = 50) Pageable pageable) {
        PipelineSearchCriteria searchCriteria = new PipelineSearchCriteria();
        searchCriteria.setUuid(uuid);
        searchCriteria.setName(name);
        return RestResponse.async(()-> entityService.searchPipelines(searchCriteria,pageable).getContent()).get();
    }

    @RequestMapping(path = "/instance",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<List<PipelineExecutionEntity>>> listAllPipelineInstances(
            @PageableDefault(value = 50) Pageable pageable) {
        return RestResponse.async(()-> executionService.searchExecutionEntities(pageable).getContent()).get();
    }

    @RequestMapping(method= RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> createPipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse.async(() -> entityService.createPipeline(pipelineEntity)).get();
    }

    @RequestMapping(method= RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> updatePipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse.async(() -> entityService.updatePipeline(pipelineEntity)).get();
    }

    @RequestMapping(method= RequestMethod.DELETE)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<Integer>> deletePipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse
                .async(() -> entityService.deletePipeline(pipelineEntity))
                .then((response) -> {
                    if (response.current.getData() == 0) {
                        response.status(false, HttpStatus.NOT_FOUND).message("No pipeline deleted");
                    }
                })
                .get();
    }

    @RequestMapping(path="/{uuid}", method= RequestMethod.DELETE)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<Integer>> deletePipelineByUuid(@PathVariable String uuid) {
        return RestResponse
                .async(() -> entityService.deletePipelineByUuid(uuid)).get();
    }



    @RequestMapping(path = "/compile",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<Pipeline>> compilePipeline(@RequestBody PipelineEntity pipelineEntity){
        Preconditions.checkNotNull(pipelineEntity.getPipeline(),"Attribute [pipeline] is null");
        return RestResponse.async(() -> PipelineCompiler.compileConfigString(pipelineEntity.getPipeline())).get();
    }

    @RequestMapping(path = "/{uuidOrName}",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> getPipeline(@PathVariable String uuidOrName) {
        return RestResponse.async(()-> entityService.getPipelineByUuidOrNameOrThrow(uuidOrName,uuidOrName)).get();
    }

    @RequestMapping(path = "/{uuidOrName}/compiled",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<Pipeline>> getCompiledPipeline(@PathVariable String uuidOrName) {
        return RestResponse.async(()-> PipelineCompiler.compileConfigString(entityService.getPipelineByUuidOrNameOrThrow(uuidOrName,uuidOrName).getPipeline())).get();
    }

    @RequestMapping(path = "/{uuidOrName}/executor",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<Collection<PipelineExecutionEntity>>> getPipelineExecution(@PathVariable String uuidOrName) {
        return RestResponse.async(()-> entityService.getPipelineByUuidOrNameOrThrow(uuidOrName,uuidOrName).getInstances()).get();
    }

    @RequestMapping(path = "/start",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> startPipeline(@RequestBody PipelineEntity pipeline){
        return RestResponse.async(()-> executionService.start(entityService.getPipelineOrThrow(pipeline))).get();
    }

    @RequestMapping(path = "/stop",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> stopPipeline(@RequestBody PipelineEntity pipeline){
        return RestResponse.async(()-> executionService.stop(entityService.getPipelineOrThrow(pipeline))).get();
    }

    @RequestMapping(path = "/restart",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> restartPipeline(@RequestBody PipelineEntity pipeline){
        return RestResponse.async(()-> executionService.restart(entityService.getPipelineOrThrow(pipeline))).get();
    }
}