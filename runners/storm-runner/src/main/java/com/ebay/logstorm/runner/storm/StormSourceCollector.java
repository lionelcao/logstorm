package com.ebay.logstorm.runner.storm;

import backtype.storm.spout.SpoutOutputCollector;
import com.ebay.logstorm.core.event.Collector;
import com.ebay.logstorm.core.event.Event;
import com.ebay.logstorm.core.serializer.Serializer;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

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
public class StormSourceCollector implements Collector {
    private final LinkedBlockingQueue<Event> queue;
    private final Serializer serializer;
    private final SpoutOutputCollector collector;
    private final int batchSize;

    public StormSourceCollector( Serializer serializer,SpoutOutputCollector collector,int maxQueueSize, int batchSize){
        this.queue = new LinkedBlockingQueue<Event>(maxQueueSize);
        this.serializer = serializer;
        this.collector = collector;
        this.batchSize = batchSize;
    }

    public void collect(Event event) {
        synchronized (queue) {
            try {
                queue.put(event);
                if(queue.size() >=  batchSize){
                    flush();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void flush(){
        synchronized (queue){
            int count = 0;
            while(count < batchSize && !queue.isEmpty()){
                try {
                    Event event  = queue.take();
                    collector.emit(event.getStreamId(), Arrays.<Object>asList(event.getPartitionKey(), this.serializer.serialize(event)));
                    count ++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}