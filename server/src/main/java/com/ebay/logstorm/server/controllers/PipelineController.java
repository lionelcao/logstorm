package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.services.PipelineSearchCriteria;
import com.ebay.logstorm.server.services.PipelineEntityService;
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
    private PipelineEntityService pipelineEntityService;

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
        return RestResponse.async(()->pipelineEntityService.searchPipelines(searchCriteria,pageable).getContent()).result();
    }

    @RequestMapping(path = "/{uuid}",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> getPipeline(
            @PathVariable String uuid) {
        PipelineSearchCriteria searchCriteria = new PipelineSearchCriteria();
        searchCriteria.setUuid(uuid);
        return RestResponse.<PipelineEntity>async((response)->{
            List<PipelineEntity> list = pipelineEntityService.searchPipelines(searchCriteria,null).getContent();
            if(list == null || list.size()==0){
                response.status(false,HttpStatus.NOT_FOUND);
            }else{
                response.data(list.get(0)).success(true);
            }
        }).result();
    }

    @RequestMapping(method= RequestMethod.POST)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> createPipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse.async(() -> pipelineEntityService.createPipeline(pipelineEntity)).result();
    }

    @RequestMapping(method= RequestMethod.PUT)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<PipelineEntity>> updatePipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse.async(() -> pipelineEntityService.updatePipeline(pipelineEntity)).result();
    }

    @RequestMapping(method= RequestMethod.DELETE)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<Integer>> deletePipeline(@RequestBody PipelineEntity pipelineEntity) {
        return RestResponse
                .async(() -> pipelineEntityService.deletePipeline(pipelineEntity))
                .then((response)-> {
                    if(response.current.getData() == 0){
                        response.status(false,HttpStatus.NOT_FOUND).message("No pipeline deleted");
                    }
                })
                .result();
    }

    @RequestMapping(path = "/compile",method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RestResponse<Pipeline>> compilePipeline(@RequestBody PipelineEntity pipelineEntity){
        return RestResponse.async(() -> PipelineCompiler.compileConfigString(pipelineEntity.getPipeline())).result();
    }
}