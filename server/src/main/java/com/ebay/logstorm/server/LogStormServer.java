package com.ebay.logstorm.server;

import com.ebay.logstorm.core.compiler.proxy.RubyRuntimeFactory;
import com.ebay.logstorm.server.platform.ExecutionManager;
import com.ebay.logstorm.server.platform.PipelineStatusChecker;
import com.ebay.logstorm.server.services.PipelineStatusSyncService;
import com.ebay.logstorm.server.utils.EagleBanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;

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
@ComponentScan({"com.ebay.logstorm"})
@EnableTransactionManagement
@SpringBootApplication
public class LogStormServer  extends SpringBootServletInitializer {
    private final static Logger LOG = LoggerFactory.getLogger(LogStormServer.class);

    @Autowired
    PipelineStatusSyncService statusSyncService;

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return (ServletContext servletContext) -> {
            Thread thread = new Thread(RubyRuntimeFactory::getSingletonRuntime);
            thread.setDaemon(true);
            thread.start();
            ExecutionManager.getInstance().submit(PipelineStatusChecker.WORKER_NAME,new PipelineStatusChecker(statusSyncService));
        };
    }

    @Bean
    public SpringBootServletInitializer springBootServletInitializer(){
        return new SpringBootServletInitializer(){
            @Override
            protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
                return builder.banner(new EagleBanner()).sources(LogStormServer.class);
            }
        };
    }


    public static void start(String[] args){
        ApplicationContext context = SpringApplication.run(LogStormServer.class,args);
        LOG.info("Started application named '{}'",context.getApplicationName());
    }

    public static void main(String[] args){
        start(args);
    }
}