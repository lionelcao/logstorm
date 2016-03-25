package com.ebay.logstorm.core.runner.local;

import com.ebay.logstorm.core.LogStormConfig;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.ebay.logstorm.core.compiler.LogStashPipeline;

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
public class LocalLogStashRunner implements PipelineRunner {

    @Override
    public void run(LogStashPipeline pipeline, LogStormConfig context) {

        throw new RuntimeException("not implemented yet");
    }
}