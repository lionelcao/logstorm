package com.ebay.logstorm.core;

import com.ebay.logstorm.core.compiler.LogstashCompiler;
import com.ebay.logstorm.core.compiler.LogstashPipeline;
import com.ebay.logstorm.core.runner.LogstashRunner;
import com.ebay.logstorm.core.runner.local.LocalLogstashRunner;

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
public class LogstashRuntime {
    LogstashContext context;
    LogstashPipeline pipeline;

    public LogstashRuntime(String logstashConfigure){
        this(new LogstashContext(logstashConfigure,System.getProperties()));
    }

    public LogstashRuntime(LogstashContext context){
        this.context = context;
    }

    public LogstashPipeline compile(){
        pipeline =  LogstashCompiler.compile(context);
        return pipeline;
    }

    public void run(LogstashRunner runner){
        if(pipeline == null) compile();
        runner.run(pipeline);
    }

    public void run(){
        run(new LocalLogstashRunner());
    }
}