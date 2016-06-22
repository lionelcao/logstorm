/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.server.entities.PlatformEntity;
import com.ebay.logstorm.server.services.PlatformEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/platform")
@CrossOrigin
public class PlatformController extends BaseController {

    @Autowired
    private PlatformEntityService entityService;

    @RequestMapping(path = "/{className:.+}",method= RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<RestResponse<PlatformEntity>> getPlatformEntity(@PathVariable String className) {
        return RestResponse.async(() -> entityService.getPlatformByClassName(className)).get();
    }

    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<RestResponse<List<PlatformEntity>>> getAllPlatformEntities() {
        return RestResponse.async(() -> entityService.getAllPlatforms()).get();
    }


}

