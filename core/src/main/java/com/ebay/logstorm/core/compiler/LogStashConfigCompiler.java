package com.ebay.logstorm.core.compiler;
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

import com.ebay.logstorm.core.LogStormConfig;
import com.ebay.logstorm.core.compiler.proxy.LogStashPipelineProxy;
import com.ebay.logstorm.core.exception.LogStashCompileException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class LogStashConfigCompiler {
    public static LogStashPipeline compile(String logStashConfigStr, LogStormConfig config) throws LogStashCompileException {
        return new LogStashPipelineProxy(logStashConfigStr,config);
    }

    public static LogStashPipeline compile(String logStashConfigStr) throws LogStashCompileException {
        return new LogStashPipelineProxy(logStashConfigStr,new LogStormConfig(System.getProperties()));
    }

    public static LogStashPipeline compile(File file) throws IOException, LogStashCompileException {
        return compile(FileUtils.readFileToString(file));
    }

    public static LogStashPipeline compileResource(String resource) throws IOException, LogStashCompileException {
        return compile(FileUtils.readFileToString(new File(LogStashConfigCompiler.class.getResource(resource).getPath())));
    }
}