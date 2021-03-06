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

import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.proxy.LogStashPipelineProxy;
import com.ebay.logstorm.core.exception.PipelineException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PipelineCompiler {
    public static Pipeline compile(PipelineContext config) throws PipelineException {
        return new LogStashPipelineProxy(config);
    }

    public static Pipeline compile(File file) throws IOException, PipelineException {
        return compileConfigString(FileUtils.readFileToString(file));
    }

    public static Pipeline compileResource(String resource) throws IOException, PipelineException {
        URL resourceUrl = PipelineCompiler.class.getResource(resource);
        if(resourceUrl == null) {
            throw new IOException("Resource "+resource+" not found");
        }else{
            return compileConfigString(FileUtils.readFileToString(new File(resourceUrl.getPath())));
        }
    }

    public static Pipeline compileConfigString(String configStr) throws PipelineException {
        return new LogStashPipelineProxy(new PipelineContext(configStr));
    }
}