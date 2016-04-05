package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.server.entities.ClusterEntity;
import com.ebay.logstorm.server.services.ClusterEntityService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/cluster")
public class ClusterController extends BaseController {

    @Autowired
    private ClusterEntityService entityService;

    @RequestMapping(method= RequestMethod.POST)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<ClusterEntity>> saveClusterEntity(@RequestBody ClusterEntity clusterEntity) {
        return RestResponse.async(() -> entityService.createCluster(clusterEntity)).get();
    }

    @RequestMapping(method= RequestMethod.PUT)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<ClusterEntity>> updateClusterEntity(@RequestBody ClusterEntity clusterEntity) {
        return RestResponse.async(() -> entityService.updateCluster(clusterEntity)).get();
    }

    @RequestMapping(method= RequestMethod.DELETE)
    @Transactional
    public @ResponseBody
    ResponseEntity<RestResponse<Integer>> deleteClusterEntity(@RequestBody ClusterEntity clusterEntity) {
        return RestResponse.async(() -> entityService.deleteCluster(clusterEntity)).get();
    }

    @RequestMapping(path = "/{uuidOrName}",method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<ClusterEntity>> getClusterEntity(@PathVariable String uuidOrName) {
        return RestResponse.async(() -> entityService.getClusterByUuidOrName(uuidOrName,uuidOrName)).get();
    }

    @RequestMapping(method= RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    ResponseEntity<RestResponse<List<ClusterEntity>>> getAllClusters() {
        return RestResponse.async(() -> entityService.getAllClusters()).get();
    }
}