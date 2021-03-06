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

import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.core.serializer.SnappyJSONSerializer;
import com.ebay.logstorm.core.serializer.Serializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.typesafe.config.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

/**
 *
 */
public class PipelineContext implements Serializable{
    private Config config = null;
    /**
     * Pipeline Config
     */
    private String pipeline = null;
    private String pipelineName = "LOGSTORM_APP_"+ UUID.randomUUID();
    private LogStormConstants.DeployMode deployMode = LogStormConstants.DEFAULT_DEPLOY_MODE;

    public PipelineContext(){}

    public PipelineContext(String pipeline){
        this.setPipeline(pipeline);
        this.config = ConfigFactory.load(); // .withValue("logstorm.pipeline.configstr",null);
    }

    @JsonIgnore
    public Serializer getSerializer(){
        return DEFAULT_SERIALIZER;
    }

    private static final Serializer DEFAULT_SERIALIZER = new SnappyJSONSerializer();

    public int getInputQueueCapacity(){
        return 10000;
    }

    public int getInputBatchSize(){
        return 50;
    }

    public int getFilterParallelism(){
        if(this.getConfig().hasPath("filter.parallelism")) {
            return this.getConfig().getInt("filter.parallelism");
        } else return 1;
    }

    public int getInputParallelism(){
        if(this.getConfig().hasPath("input.parallelism")) {
            return this.getConfig().getInt("input.parallelism");
        }else{
            return 1;
        }
    }

    public int getOutputParallelism(){
        if(this.getConfig().hasPath("output.parallelism")) {
            return this.getConfig().getInt("output.parallelism");
        }else{
            return 1;
        }
    }

    public Config getConfig() {
        return config;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public static PipelineContextBuilder pipelineResource(String pipelineResource) throws IOException, PipelineException {
        URL resourceUrl = PipelineCompiler.class.getResource(pipelineResource);
        if(resourceUrl == null) {
            throw new IOException("Pipeline resource "+pipelineResource+" not found");
        }else{
            return new PipelineContextBuilder(FileUtils.readFileToString(new File(resourceUrl.getPath())));
        }
    }

    public static PipelineContextBuilder pipeline(String pipeline) throws PipelineException {
        return new PipelineContextBuilder(pipeline);
    }

    public void setConfig(Config config) {
        if(this.config == null) {
            this.config = config;
        }else{
            this.config = this.config.withFallback(config);
        }
    }

    public static PipelineContextBuilder builder() {
        return new PipelineContextBuilder();
    }

    public LogStormConstants.DeployMode getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(LogStormConstants.DeployMode deployMode) {
        this.deployMode = deployMode;
    }

    public void setDeployMode(String mode) {
        this.setDeployMode(LogStormConstants.DeployMode.locate(mode));
    }

    public void setConfig(Properties properties) {
        if(properties == null) return;
        if(this.config == null) {
            this.config = ConfigFactory.parseProperties(properties);
        } else {
            this.config = ConfigFactory.parseProperties(properties).withFallback(this.config);
        }
    }

    public String getPipelineJarPath(){
        return this.config.getString("pipeline.jar");
    }
}