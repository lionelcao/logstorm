package com.ebay.logstorm.core;

import com.ebay.logstorm.core.compiler.LogStashPlugin;

import java.io.Serializable;

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
public class LogStormConstants {
    public final static String CURRENT_VERSION = "0.1-SNAPSHOT";

    public final static DeployMode DEFAULT_DEPLOY_MODE = DeployMode.STANDALONE;
    public final static String DEFAULT_RUNNER_CLASS_NAME = "com.ebay.logstorm.runner.storm.StormPipelineRunner";

    public static class PluginType{
        public final static String INPUT = "input";
        public final static String FILTER = "filter";
        public final static String OUTPUT = "output";

        public static boolean isInputPlugin(LogStashPlugin plugin){
            return INPUT.equals(plugin.getPluginType());
        }
        public static boolean isFilterPlugin(LogStashPlugin plugin){
            return FILTER.equals(plugin.getPluginType());
        }
        public static boolean isOutputPlugin(LogStashPlugin plugin){
            return OUTPUT.equals(plugin.getPluginType());
        }
    }

    public enum DeployMode implements Serializable {
        STANDALONE("standalone"), CLUSTER("cluster");

        private final String mode;

        DeployMode(String mode){
            this.mode = mode;
        }

        public static DeployMode locate(String mode){
            if(STANDALONE.mode.equalsIgnoreCase(mode) || "local".equals(mode)){
                return STANDALONE;
            }else if(CLUSTER.mode.equalsIgnoreCase(mode)){
                return CLUSTER;
            }else{
                throw new RuntimeException("Illegal deployment mode: "+mode+", options: [local, cluster]");
            }
        }
    }
}