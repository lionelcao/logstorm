package com.ebay.logstorm.runner.storm;

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
class Constants {
    public static final String EVENT_KEY_FIELD = "partitionKey";
    public static final String EVENT_VALUE_FIELD = "value";
    public static final String STORM_AUTHOR_TUPLE ="storm_author_tuple";
    public static final String STORM_FILTER_BOLT_NAME = "filter";
}