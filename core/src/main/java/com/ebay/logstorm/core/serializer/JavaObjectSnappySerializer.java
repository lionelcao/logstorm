package com.ebay.logstorm.core.serializer;

import com.ebay.logstorm.core.event.EventContext;
import com.ebay.logstorm.core.utils.SerializableUtils;
import org.jruby.RubyObject;

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
public class JavaObjectSnappySerializer implements Serializer {
    @Override
    public byte[] serialize(EventContext obj) {
        return SerializableUtils.serializeToByteArray(obj.getEvent());
    }

    @Override
    public EventContext deserialize(byte[] bytes) {
        return new EventContext((RubyObject) SerializableUtils.deserializeFromByteArray(bytes,"Deserialize event object from binary array"));
    }
}
