package com.ebay.logstorm.server.platform;

import com.ebay.logstorm.server.services.PipelineStatusSyncService;
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
public class ExecutionStatusUpdater extends TaskExecutor {
    public final static String WORKER_NAME = ExecutionStatusUpdater.class.getSimpleName();
    private PipelineStatusSyncService statusSyncService;

    public ExecutionStatusUpdater(PipelineStatusSyncService statusSyncService){
        this.statusSyncService = statusSyncService;
    }

    private final static Logger LOG = LoggerFactory.getLogger(ExecutionStatusUpdater.class);
    private volatile Boolean stopped = false;
    private static Long periodMillis = 30* 1000l;

    @Override
    public void run() {
        while(!stopped){
            try {
                statusSyncService.doUpdate();
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            } finally {
                try {
                    LOG.debug("Sleep ");
                    TaskExecutor.sleep(periodMillis);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

    @Override
    public void interrupt() {
        this.stopped = true;
        super.interrupt();
    }
}
