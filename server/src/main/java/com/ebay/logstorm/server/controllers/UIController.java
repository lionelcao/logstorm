package com.ebay.logstorm.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping("/")
@Deprecated
public class UIController {
    @RequestMapping("/ui")
    public ModelAndView home(){
        return new ModelAndView("home/index");
    }

    @RequestMapping("/pipeline")
    public ModelAndView listPipelines(){
        return new ModelAndView("pipelines/list");
    }

    @RequestMapping("/pipeline/create")
    public ModelAndView createPipelines(){
        return new ModelAndView("pipelines/create");
    }

    @RequestMapping("/pipeline/{uuid}")
    public ModelAndView viewPipeline(@PathVariable String uuid){
        return new ModelAndView("pipelines/view");
    }

    @RequestMapping("/pipeline/{uuid}/edit")
    public ModelAndView editPipeline(@PathVariable String uuid){
        return new ModelAndView("pipelines/edit");
    }
}