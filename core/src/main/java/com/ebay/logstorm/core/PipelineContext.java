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
import com.ebay.logstorm.core.exception.LogStormException;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.ebay.logstorm.core.serializer.SnappyJSONSerializer;
import com.ebay.logstorm.core.serializer.Serializer;
import com.typesafe.config.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
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
    private PipelineConstants.DeployMode deployMode = PipelineConstants.DEFAULT_DEPLOY_MODE;

    public PipelineContext(String pipeline){
        this.setPipeline(pipeline);
        this.config = ConfigFactory.load(); // .withValue("logstorm.pipeline.configstr",null);
    }

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

    public int getFilterParallesm(){
        return 1;
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

    public static PipelineContextBuilder pipelineResource(String pipelineResource) throws IOException, LogStormException {
        URL resourceUrl = PipelineCompiler.class.getResource(pipelineResource);
        if(resourceUrl == null) {
            throw new IOException("Pipeline resource "+pipelineResource+" not found");
        }else{
            return new PipelineContextBuilder(FileUtils.readFileToString(new File(resourceUrl.getPath())));
        }
    }

    public static PipelineContextBuilder pipeline(String pipeline) throws IOException, LogStormException {
        return new PipelineContextBuilder(pipeline);
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Define pipeline
     *
     * @param pipeline
     * @return
     */
    public static PipelineContextBuilder define(String pipeline) {
        return new PipelineContextBuilder(pipeline);
    }

    public PipelineConstants.DeployMode getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(PipelineConstants.DeployMode deployMode) {
        this.deployMode = deployMode;
    }

    public void setDeployMode(String mode) {
        this.setDeployMode(PipelineConstants.DeployMode.locate(mode));
    }

    public static class PipelineContextBuilder{
        private final PipelineContext pipelineContext;
        private PipelineRunner runner;
        private final static Logger LOG = LoggerFactory.getLogger(PipelineContextBuilder.class);

        public PipelineContextBuilder(String pipeline){
            this.pipelineContext = new PipelineContext(pipeline);
        }


        public PipelineContextBuilder runner(PipelineRunner runner){
            this.runner = runner;
            return this;
        }

        public PipelineContextBuilder runner(Class<? extends PipelineRunner> runnerClass){
            try {
                this.runner = runnerClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public PipelineContextBuilder runner(String runnerClass){
            try {
                this.runner = (PipelineRunner) Class.forName(runnerClass).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public void submit() throws LogStormException {
            if(this.runner == null){
                LOG.info("No runner set, use default runner: "+PipelineConstants.DEFAULT_RUNNER_CLASS_NAME);
                this.runner(PipelineConstants.DEFAULT_RUNNER_CLASS_NAME);
            }
            this.runner.run(PipelineCompiler.compile(this.pipelineContext));
        }

        public void submit(Class<? extends PipelineRunner> runnerClass) throws LogStormException {
            this.runner(runnerClass);
            submit();
        }

        public void submit(PipelineRunner runner) throws LogStormException {
            this.runner = runner;
            submit();
        }

        public PipelineContextBuilder setConfig(Config config) {
            this.pipelineContext.setConfig(config);
            return this;
        }

        public PipelineContextBuilder name(String pipelineName){
            this.pipelineContext.setPipelineName(pipelineName);
            return this;
        }

        public PipelineContextBuilder deploy(PipelineConstants.DeployMode mode){
            this.pipelineContext.setDeployMode(mode);
            return this;
        }

        public PipelineContextBuilder deploy(String mode){
            this.pipelineContext.setDeployMode(mode);
            return this;
        }
    }
}