
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

package com.ebay.logstorm.core;

import com.ebay.logstorm.core.exception.LogStormException;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PipelineManager {

    private final Config baseConfig;

    public PipelineManager(Config baseConfig){
        this.baseConfig = baseConfig;
    }

    private static PipelineManager instance = null;
    public static PipelineManager getInstance(){
        if(instance == null){
            instance = new PipelineManager(ConfigFactory.load());
        }
        return instance;
    }

    public void submit(PipelineContext context, PipelineRunner runner) throws LogStormException {
        Pipeline pipeline =  PipelineCompiler.compile(context);
        runner.run(pipeline);
    }

    public void submit(Pipeline pipeline, PipelineRunner runner) throws LogStormException {
        runner.run(pipeline);
    }
}