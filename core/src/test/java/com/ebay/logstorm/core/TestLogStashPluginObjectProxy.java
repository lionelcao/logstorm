package com.ebay.logstorm.core;

import com.ebay.logstorm.core.compiler.proxy.LogStashPipelineProxy;
import com.ebay.logstorm.core.compiler.proxy.LogStashPluginObjectProxy;
import com.ebay.logstorm.core.exception.LogStashCompileException;
import org.jruby.RubyObject;
import org.jruby.runtime.builtin.Variable;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
public class TestLogStashPluginObjectProxy {
    private final static Logger LOG = LoggerFactory.getLogger(TestLogStashPluginObjectProxy.class);
    @Test
    public void testRubyObjectProxy() throws LogStashCompileException {
        String configStr = "input { generator { threads => 10 lines => [ \"GET /user 0.98\",\"GET /user 1.98\",\"GET /user 2.98\"] count => 3}}";
        LogStashPipelineProxy proxy = new LogStashPipelineProxy(configStr,null);
        RubyObject rubyObject = (RubyObject) proxy.getInputsProxy().get(0);
        LogStashPluginObjectProxy objectProxy = new LogStashPluginObjectProxy(rubyObject);

        LOG.info("meta_variable_names: {}",objectProxy.getMetaVariableNames());
        Assert.assertEquals("input",objectProxy.getPluginType());

        List<Variable<Object>> variables  =  objectProxy.getVariables();

        LOG.info("plugin_type: {}",objectProxy.getPluginType());
        LOG.info("variables: {}",variables.toString());
        LOG.info("config: {}",objectProxy.getConfig());
        LOG.info("debug_info: {}",objectProxy.getDebugInfo());
        LOG.info("config_name: {}",objectProxy.getConfigName());
        LOG.info("params: {}",objectProxy.getParams());
        LOG.info("original_params: {}",objectProxy.getOriginalParams());
        LOG.info("threads: {}",objectProxy.getThreads());
    }
}