package com.ebay.logstorm.storm.runner;

import backtype.storm.utils.Utils;
import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.exception.PipelineException;
import com.ebay.logstorm.runner.storm.StormPipelineRunner;
import org.junit.After;
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
public class TestStormPipelineRunner {
    @Test
    public void testStormSimplestPipelineTopologyBuilder() throws IOException, PipelineException {
        PipelineContext.pipeline(
            "input { generator { lines => [ \"GET /user 0.98\",\"GET /user 1.98\",\"GET /user 2.98\"] count => 3}}"     +
            "filter{ grok { add_field => { \"new_field\" => \"new_value\"}}}"+
            "output { stdout { codec => rubydebug }}"
        ).submit();
    }

    @Test
    public void testStormFluentPipelineTopologyBuilder() throws PipelineException {
        PipelineContext.builder()
            .input("generator{ lines => [\"GET /user 0.98\",\"GET /user 1.98\",\"GET /user 2.98\"] count => 3 }")
            .filter("grok { add_field => { \"new_field\" => \"new_value\"}}")
            .output("stdout { codec => rubydebug }")
        .submit();
    }

    @Test
    public void testStormResourcePipelineTopologyBuilder() throws IOException, PipelineException {
        PipelineContext.pipelineResource("/simple-generator-stdout.txt")
            .name("simple-generator-stdout-pipeline")
            .runner(StormPipelineRunner.class)
            .deploy(LogStormConstants.DeployMode.STANDALONE)
            .submit();
    }

    @Test
    public void testStormInlinePipelineTopologyBuilder() throws PipelineException {
        PipelineContext.pipeline(
                "input { generator { lines => [ \"GET /user 0.98\",\"GET /user 1.98\",\"GET /user 2.98\"] count => 3}}"     +
                "filter{ grok { add_field => { \"new_field\" => \"new_value\" } } }"+
                "output { stdout { codec => rubydebug } }")
            .name("simple-generator-stdout-pipeline")
            .deploy("local")
            .submit(StormPipelineRunner.class);
    }

    @After
    public void onAfter(){
        Utils.sleep(5000l);
    }
}