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

package com.ebay.logstorm.server.services.impl;

import com.ebay.logstorm.server.entities.PlatformEntity;
import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstorm.server.services.PlatformEntityRepository;
import com.ebay.logstorm.server.services.PlatformEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("PlatformEntityService")
public class PlatformEntityServiceImpl implements PlatformEntityService {

    @Override
    public PlatformEntity getPlatformByClassName(String className) {
        PlatformEntity entity = new PlatformEntity();
        ExecutionPlatform platform = PlatformEntityRepository.getExecutonPlatform(className);
        if (platform != null) {
            entity.setType(platform.getType());
            entity.setClassName(className);
            entity.setFields(parseTemplateString(platform.getConfigTemplate()));
            return entity;
        } else {
            return null;
        }
    }

    private List<Map<String, String>> parseTemplateString(String template) {
        if (template == null || template.isEmpty()) {
            return null;
        }
        ObjectMapper om = new ObjectMapper();
        CollectionType collectionType = om.getTypeFactory().constructCollectionType(List.class, Map.class);
        try {
            return om.readValue(template, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PlatformEntity> getAllPlatforms() throws Exception {
        List<PlatformEntity> results = new LinkedList<>();
        PlatformEntityRepository.getAllPlatforms().forEach(className -> {
                    PlatformEntity entity = getPlatformByClassName(className);
                    if (entity != null) {
                        results.add(entity);
                    }
                });
        return results;
    }


}
