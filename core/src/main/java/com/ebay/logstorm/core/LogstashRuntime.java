
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

import com.ebay.logstorm.core.exception.LogStashCompileException;
import com.ebay.logstorm.core.compiler.LogStashConfigCompiler;
import com.ebay.logstorm.core.compiler.LogStashPipeline;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.ebay.logstorm.core.runner.local.LocalLogStashRunner;

public class LogStashRuntime {

    private final LogStormConfig baseConfig;

    public LogStashRuntime(LogStormConfig baseConfig){
        this.baseConfig = baseConfig;
    }

    public void run(String logStashConfigStr,PipelineRunner runner,LogStormConfig config) throws LogStashCompileException {
        LogStashPipeline pipeline =  LogStashConfigCompiler.compile(logStashConfigStr,config);
        runner.run(pipeline,config);
    }

    public void run(String logStashConfigStr,LogStormConfig config) throws LogStashCompileException {
        run(logStashConfigStr,new LocalLogStashRunner(),config);
    }
}