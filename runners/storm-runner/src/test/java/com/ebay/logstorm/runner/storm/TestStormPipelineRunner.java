package com.ebay.logstorm.runner.storm;

import backtype.storm.utils.Utils;
import com.ebay.logstorm.core.PipelineManager;
import com.ebay.logstorm.core.compiler.LogStashConfigCompiler;
import com.ebay.logstorm.core.compiler.LogStashPipeline;
import com.ebay.logstorm.core.exception.LogStashCompileException;
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

    private StormPipelineRunner runner = new StormPipelineRunner();

    @Test
    public void testStormPipelineTopologyBuilder() throws IOException, LogStashCompileException {
        LogStashPipeline pipeline = LogStashConfigCompiler.compileResource("/simple-generator-stdout.txt");
        PipelineManager.getInstance().submit(pipeline,runner);
        Utils.sleep(5000l);
    }
}