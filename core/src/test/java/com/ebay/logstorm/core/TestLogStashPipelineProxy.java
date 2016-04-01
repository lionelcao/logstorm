package com.ebay.logstorm.core;

import com.ebay.logstorm.core.compiler.LogStashInput;
import com.ebay.logstorm.core.compiler.proxy.LogStashPipelineProxy;
import com.ebay.logstorm.core.event.MemoryCollector;
import com.ebay.logstorm.core.exception.LogStashCompileException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
public class TestLogStashPipelineProxy {
    LogStashPipelineProxy proxy;
    final String configStr = "input { generator { lines => [ \"GET /user 0.98\",\"GET /user 1.98\",\"GET /user 2.98\"] count => 3}}";

    @Before
    public void setUp() throws LogStashCompileException {
        proxy = new LogStashPipelineProxy(configStr,null);
    }

    @Test
    public void testSingleInputProxy() throws LogStashCompileException {
        LogStashInput input = proxy.getInputs().get(0);
        Assert.assertEquals(1,proxy.getInputsProxy().size());
        Assert.assertEquals(0,input.getIndex());
        Assert.assertEquals(configStr,input.getConfigStr());
        Assert.assertEquals("input_generator_0",input.getUniqueName());
    }

    @Test
    public void testSingleInputProxyRun() throws Exception {
        LogStashInput input = proxy.getInputs().get(0);
        input.initialize();
        MemoryCollector collector = new MemoryCollector();
        input.register();
        input.run(collector);
        Assert.assertEquals(9,collector.memorySize());
        input.close();
        Assert.assertEquals(0,collector.memorySize());
    }
}