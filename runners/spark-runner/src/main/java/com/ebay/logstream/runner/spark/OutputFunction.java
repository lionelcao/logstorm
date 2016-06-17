package com.ebay.logstream.runner.spark;

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
import com.ebay.logstorm.core.compiler.OutputPlugin;
import com.ebay.logstorm.core.event.Event;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OutputFunction implements Function<byte[], Boolean> {
    private final static Logger LOG = LoggerFactory.getLogger(OutputFunction.class);

    private OutputPlugin outputPlugin;
    private PipelineContext context;
    private Boolean inited;

    public OutputFunction(OutputPlugin outputPlugin, PipelineContext context) {
        this.outputPlugin = outputPlugin;
        this.context = context;
        this.inited = false;
    }

    @Override
    public Boolean call(byte[] eventBytes) throws Exception {
        if (!inited) {
            try {
                outputPlugin.initialize();
                outputPlugin.register();
            } catch (Exception e) {
                LOG.error("Failed to initialize/register '{}'", outputPlugin.getUniqueName(), e);
                throw new RuntimeException(e);
            }
            this.inited = true;
        }
        Event event = context.getSerializer().deserialize(eventBytes);
        outputPlugin.receive(event);
        return false;
    }
}
