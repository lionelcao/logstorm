package com.ebay.logstream.runner.spark;

import com.ebay.logstorm.core.PipelineContext;
import com.ebay.logstorm.core.compiler.InputPlugin;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
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

public class LogStashSparkReceiver extends Receiver<byte []> {
    private final static Logger LOG = LoggerFactory.getLogger(LogStashSparkReceiver.class);

    private InputPlugin inputPlugin;
    private SparkSourceCollector collector;
    private int index;
    public LogStashSparkReceiver(int index, InputPlugin input, PipelineContext context) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.index = index;
        this.inputPlugin = input;
        this.collector = new SparkSourceCollector(this, context.getSerializer());
    }

    @Override
    public void onStart() {
        try {
            inputPlugin.initialize();
        } catch (Exception e) {
            LOG.error("Failed to initialize", e);
            return;
        }
        try {
            inputPlugin.register();
        } catch (Exception e) {
            LOG.error("Failed to register", e);
            return;
        }
        System.out.println("receiver start " + index);
        inputPlugin.run(collector);
    }

    @Override
    public void onStop() {
        this.collector.flush();
        try {
            this.inputPlugin.close();
        } catch (Exception e) {
            LOG.error("Failed to close", e);
        }
    }
}
