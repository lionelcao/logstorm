package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.services.PipelineEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping("/api")
public class RestController extends BaseController{
    @Autowired
    private PipelineEntityService pipelineEntityService;

    @RequestMapping(path = "/pipeline",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody ResponseEntity<RestResponse<List<PipelineEntity>>> listPipelines(@PageableDefault(value = 50) Pageable pageable) {
        return RestResponse
                .data(pipelineEntityService.findAll(pageable).getContent())
                .status(HttpStatus.OK)
                .success(true)
                .build();
    }
}