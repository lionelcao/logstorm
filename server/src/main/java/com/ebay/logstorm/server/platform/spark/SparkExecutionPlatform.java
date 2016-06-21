package com.ebay.logstorm.server.platform.spark;

import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.Pipeline;
import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.ebay.logstorm.server.platform.ExecutionPlatform;
import com.ebay.logstream.runner.spark.SparkPipelineRunner;
import com.google.common.base.Preconditions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

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

public class SparkExecutionPlatform implements ExecutionPlatform {
    private final static Logger LOG = LoggerFactory.getLogger(SparkExecutionPlatform.class);
    private SparkPipelineRunner runner;
    private static int MIN_REST_PORT = 4040;
    private static int MAX_REST_PORT = 4100;
    private String sparkRestUrl;
    private Properties properties;
    private static final String SPARK_REST_API_PATH = "/api/v1/applications/";
    private static final String SPARK_JOB_PATH = "/jobs/";

    @Override
    public void prepare(Properties properties) {
        this.runner = new SparkPipelineRunner();
        this.sparkRestUrl = (String) properties.get(SparkPipelineRunner.SPARK_DRIVER_KEY);
        this.properties = properties;
    }

    private void startPipeLine(final PipelineExecutionEntity entity) throws Exception {
        PipelineContext context = new PipelineContext(entity.getPipeline().getPipeline());
        context.setConfig(properties);
        context.setDeployMode(entity.getPipeline().getMode());
        context.setPipelineName(entity.getPipeline().getName());
        Pipeline pipeline = PipelineCompiler.compile(context);
        Map<String, Object> result = runner.run(pipeline);
        String applicationId = (String)result.get("applicationId");
        String driverPid = (String)result.get("driverPid");

        Preconditions.checkNotNull(applicationId,"applicationId is null");
        Preconditions.checkNotNull(driverPid,"driverPid is null");

        entity.setProperty("applicationId", applicationId);
        entity.setProperty("driverPid", driverPid);
        entity.setStatus(PipelineExecutionStatus.STARTING);
    }

    @Override
    public void start(final PipelineExecutionEntity entity) throws Exception {
        startPipeLine(entity);
        status(entity);
    }

    @Override
    public void stop(final PipelineExecutionEntity entity) throws Exception {
        String applicationId = entity.getProperties().getProperty("applicationId");
        String driverPid = entity.getProperties().getProperty("driverPid");
        String cmd = "kill -9 " + driverPid;
        Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd});
        synchronized (process) {
            try {
                process.wait();
            } catch (Exception e) {
                LOG.warn("failed to kill the application {}, driver pid {}", applicationId, driverPid, e);
                return;
            }
        }
        LOG.info("kill the application {}, driver pid {}", applicationId, driverPid);
        entity.setStatus(PipelineExecutionStatus.STOPPED);
    }

    @Override
    public void status(final PipelineExecutionEntity entity) throws Exception {
        String applicationId = entity.getProperties().getProperty("applicationId");
        int beginPort = MIN_REST_PORT;
        while (beginPort++ < MAX_REST_PORT) {
            String restURL = sparkRestUrl + ":" + beginPort + SPARK_REST_API_PATH + applicationId;
            try {
                URL url = new URL(restURL);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String statusStr = "";
                String line;
                while (null != (line = br.readLine())) {
                    statusStr += line + "\n";
                }
                br.close();

                entity.setDescription(statusStr);
                entity.setUrl(sparkRestUrl + ":" + beginPort + SPARK_JOB_PATH);
                LOG.info(statusStr);
                //parse more json fields later, just work now
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(statusStr);
                JSONObject jsonObject = (JSONObject) obj;
                if (applicationId.equals(jsonObject.get("id"))) {
                    LOG.info("find application {} rest url {}", applicationId, restURL);
                } else {
                    LOG.warn("wrong application {} rest url {}", applicationId, restURL);
                    continue;
                }

                JSONArray a = (JSONArray) jsonObject.get("attempts");
                for (int i = 0; i < a.size(); i++) {
                    boolean finished = (boolean) ((JSONObject) a.get(i)).get("completed");
                    if (!finished) {
                        entity.setStatus(PipelineExecutionStatus.RUNNING);
                        return;
                    }
                }

                entity.setStatus(PipelineExecutionStatus.STOPPED);
                return;
            } catch (Exception e) {
            }
        }
        entity.setStatus(PipelineExecutionStatus.STOPPED);
        LOG.warn("get status for application {} failed, assume stopped", applicationId);
    }

    @Override
    public String getType() {
        return "spark";
    }

    @Override
    public String getVersion() {
        return "v1.6.1";
    }
}
