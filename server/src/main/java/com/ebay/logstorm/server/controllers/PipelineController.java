package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.core.exception.PipelineExecutionException;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.functions.UncheckedFunction;
import com.ebay.logstorm.server.services.PipelineEntityService;
import com.ebay.logstorm.server.services.PipelineExecutionService;
import com.ebay.logstorm.server.services.PipelineSearchCriteria;
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
@Controller
@RequestMapping("/api/pipeline")
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

    @RequestMapping(method= RequestMethod.POST)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> createPipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse.async(() -> entityService.createPipeline(pipelineEntity)).get();
    }

    @RequestMapping(method= RequestMethod.PUT)
    @Transactional
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
                .then((response)-> {
                    if(response.current.getData() == 0){
                        response.status(false,HttpStatus.NOT_FOUND).message("No pipeline deleted");
                    }
                })
                .get();
    }

    @RequestMapping(path = "/compile",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<Pipeline>> compilePipeline(@RequestBody PipelineEntity pipelineEntity){
        return RestResponse.async(() -> PipelineCompiler.compileConfigString(pipelineEntity.getPipeline())).get();
    }

    @RequestMapping(path = "/{uuid}",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> getPipeline(
            @PathVariable String uuid) {
        PipelineSearchCriteria searchCriteria = new PipelineSearchCriteria();
        searchCriteria.setUuid(uuid);
        return RestResponse.<PipelineEntity>async((response)->{
            List<PipelineEntity> list = entityService.searchPipelines(searchCriteria,null).getContent();
            if(list == null || list.size()==0){
                response.status(false,HttpStatus.NOT_FOUND);
            }else{
                response.data(list.get(0)).success(true);
            }
        }).get();
    }

    @RequestMapping(path = "/{uuid}/compile",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<Pipeline>> compilePipeline(@PathVariable String uuid){
        return RestResponse.async(() -> withPipeline(uuid,(pipeline) -> PipelineCompiler.compileConfigString(pipeline.getPipeline()))).get();
    }

    /**
     *
     * @param uuid
     * @param func
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T withPipeline(String uuid, UncheckedFunction<PipelineEntity,T> func) throws Exception {
        Optional<PipelineEntity> entity = entityService.getPipelineByUuid(uuid);
        if(entity.isPresent()) {
            return func.apply(entity.get());
        } else {
            throw new PipelineException("Pipeline [uuid='"+uuid+"'] is not found");
        }
    }


    /**
     *
     * @param pipelineUuid
     * @param func
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T withExecutionOfPipeline(String pipelineUuid, UncheckedFunction<PipelineExecutionEntity,T> func) throws Exception {
        Optional<PipelineExecutionEntity> entity = executionService.getExecutionEntityByPipelineId(pipelineUuid);
        if(entity.isPresent()) {
            return func.apply(entity.get());
        } else {
            throw new PipelineExecutionException("Execution instance of pipeline [uuid='"+ pipelineUuid +"'] is not found");
        }
    }

    /**
     * Submit pipeline only and don't persist status
     *
     * @param pipeline
     * @return
     */
    @RequestMapping(path = "/submit",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineExecutionEntity>> submitPipeline(@RequestBody PipelineEntity pipeline){
        return RestResponse.async(()-> executionService.submitOnly(pipeline)).get();
    }

    @RequestMapping(path = "/{uuid}/start",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineExecutionEntity>> startPipeline(@PathVariable String uuid){
        return RestResponse.async(()-> executionService.start(uuid)).get();
    }

    @RequestMapping(path = "/{uuid}/stop",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineExecutionEntity>> stopPipeline(@PathVariable String uuid){
        return RestResponse.async(()-> withExecutionOfPipeline(uuid,(instance) -> executionService.stop(instance))).get();
    }

    @RequestMapping(path = "/{uuid}/status",method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineExecutionEntity>> getPipelineExecution(@PathVariable String uuid){
        return RestResponse.async(()-> withExecutionOfPipeline(uuid,(execution)->execution)).get();
    }

    @RequestMapping(path = "/{uuid}/restart",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineExecutionEntity>> restartPipeline(@PathVariable String uuid){
        return RestResponse.async(()-> withExecutionOfPipeline(uuid,(instance) -> executionService.restart(instance))).get();
    }

    @RequestMapping(path = "/{uuid}/rescale",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineExecutionEntity>> rescalePipeline(@PathVariable String uuid){
        return RestResponse.async(()-> withExecutionOfPipeline(uuid,(instance) -> executionService.rescale(instance))).get();
    }
}