package com.ebay.logstorm.runner.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import com.ebay.logstorm.core.event.Collector;
import com.ebay.logstorm.core.event.Event;
import com.ebay.logstorm.core.serializer.Serializer;

import java.util.Arrays;

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
public class StormEventCollector implements Collector{
    private final OutputCollector collector;
    private final Serializer serializer;

    public StormEventCollector(OutputCollector collector, Serializer serializer){
        this.collector = collector;
        this.serializer = serializer;
    }

    public void collect(Event event) {
        collector.emit(
                event.getRawEvent().getStreamId(),
                (Tuple) event.getContext().get(Constants.STORM_AUTHOR_TUPLE),
                Arrays.<Object>asList(event.getPartitionKey(), this.serializer.serialize(event.getRawEvent())));
    }
}