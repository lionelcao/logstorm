package com.ebay.logstorm.core.compiler.proxy;

import org.jruby.Ruby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ebay.logstorm.core.compiler.proxy.LogStashProxyConstants.*;

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
public class RubyRuntimeFactory {
    private static Ruby runtime;
    private final static Logger LOG = LoggerFactory.getLogger(RubyRuntimeFactory.class);

    public static Ruby getSingletonRuntime(){
        if(runtime == null) {
            runtime = Ruby.getGlobalRuntime();
            LOG.info("New ruby runtime instance: "+runtime);
            String rubyGemHome = String.format("%s/vendor/bundle/jruby/%s", LOGSTASH_HOME, JRUBY_VERSION);
            String bootstrap = "";
            bootstrap += String.format("ENV[\"%s\"] = \"%s\";\n", "LOGSTASH_HOME", LOGSTASH_HOME);
            bootstrap += String.format("ENV[\"%s\"] = \"%s\";\n", "GEM_HOME", rubyGemHome);
            bootstrap += "require '" + LOGSTORM_RUBY_FILE + "';\n";
            if(LOG.isDebugEnabled()) LOG.debug("Bootstrapping ruby runtime with scriptlet:\n\n"+bootstrap);
            runtime.evalScriptlet(bootstrap);
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    if(runtime!=null){
                        LOG.info("ShutdownHook for ruby runtime: "+runtime);
                        runtime.shutdownTruffleContextIfRunning();
                    }
                }
            });
        }
        return runtime;
    }
}