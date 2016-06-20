package com.ebay.logstream.runner.spark;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.exception.PipelineException;
import org.junit.Test;

import java.io.IOException;

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
public class TestSparkPipelineRunner {
    @Test
    public void testSparkResourcePipelineTopologyBuilder() throws IOException, PipelineException {
        PipelineContext.pipelineResource("/simple-generator-stdout.txt")
                .name("simple-generator-stdout-pipeline")
                .runner(SparkPipelineRunner.class)
                .deploy(LogStormConstants.DeployMode.CLUSTER)
                .submit();
    }
}
