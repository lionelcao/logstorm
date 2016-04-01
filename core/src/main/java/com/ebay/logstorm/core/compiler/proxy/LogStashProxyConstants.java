package com.ebay.logstorm.core.compiler.proxy;

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
public class LogStashProxyConstants {
    public final static String LOGSTASH_HOME = "uri:classloader:/META-INF/logstash.home";
    public final static String JRUBY_VERSION = "1.9";
    public final static String LOGSTORM_RUBY_FILE="logstorm.rb";
    public final static String LOGSTASH_PIPELINE_RUBY_CLASS="LogStashPipelineRubyProxy";
    public final static String LOGSTASH_PLUGIN_RUBY_CLASS ="LogStash::Plugin";
    public final static String LOGSTASH_COLLECTOR_RUBY_CLASS ="CollectorQueueRubyProxy";

    public final static String  LOGSTASH_INPUT_PLUGIN_RUN_METHOD="run";
    public final static String  LOGSTASH_INPUT_PLUGIN_REGISTER_METHOD="register";
    public final static String  LOGSTASH_INPUT_PLUGIN_CLOSE_METHOD="do_close";

    public final static String  LOGSTASH_FILTER_PLUGIN_FILTER_METHOD="filter";
    public final static String  LOGSTASH_OUTPUT_PLUGIN_RECEIVE_METHOD="receive";
    public final static String  LOGSTASH_OUTPUT_PLUGIN_MULTI_RECEIVE_METHOD="multi_receive";
}