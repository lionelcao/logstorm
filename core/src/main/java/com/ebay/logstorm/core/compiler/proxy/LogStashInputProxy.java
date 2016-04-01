package com.ebay.logstorm.core.compiler.proxy;

import com.ebay.logstorm.core.compiler.LogStashInput;
import com.ebay.logstorm.core.event.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class LogStashInputProxy extends LogStashPluginProxyBase implements LogStashInput {
    private Collector collector;
    private final static Logger LOG = LoggerFactory.getLogger(LogStashInputProxy.class);

    @Override
    public void run(Collector collector) {
        this.collector = collector;
        this.getProxy().invokeWithArguments(LogStashProxyConstants.LOGSTASH_INPUT_PLUGIN_RUN_METHOD_NAME,RubyRuntimeFactory.createRubyEventCollector(collector));
    }

    @Override
    public void close() {
        if(this.collector != null) {
            if(LOG.isDebugEnabled())
                LOG.debug("Flushing '{}'",this.collector);
            this.collector.flush();
        }
        super.close();
    }
}