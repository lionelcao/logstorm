package com.ebay.logstream.runner.spark;

import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.OutputPlugin;
import com.ebay.logstorm.core.event.Event;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

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

public class ForEachFunction implements Function2<JavaRDD<byte[]>, Time, Void> {
    private final static Logger LOG = LoggerFactory.getLogger(ForEachFunction.class);

    private OutputPlugin outputPlugin;
    private PipelineContext context;

    class InternalVoidFunction implements VoidFunction<Iterator<byte[]>> {
        private OutputPlugin outputPlugin;
        private PipelineContext context;
        private Boolean inited;

        public InternalVoidFunction(OutputPlugin outputPlugin, PipelineContext context) {
            this.outputPlugin = outputPlugin;
            this.context = context;
            this.inited = false;
        }

        @Override
        public void call(Iterator<byte[]> iterator) throws Exception {
            if (!this.inited) {
                try {
                    this.outputPlugin.initialize();
                    this.outputPlugin.register();
                } catch (Exception e) {
                    LOG.error("Failed to initialize/register '{}'", this.outputPlugin.getUniqueName(), e);
                    throw new RuntimeException(e);
                }
                this.inited = true;
            }

            while (iterator.hasNext()) {
                Event event = this.context.getSerializer().deserialize(iterator.next());
                this.outputPlugin.receive(event);
            }
        }
    }

    public ForEachFunction(OutputPlugin outputPlugin, PipelineContext context) {
        this.outputPlugin = outputPlugin;
        this.context = context;
    }

    @Override
    public Void call(JavaRDD<byte[]> v1, Time v2) throws Exception {
        v1.foreachPartition(new InternalVoidFunction(outputPlugin, context));
        return null;
    }
}
