package com.ebay.logstorm.server.platform;

import com.ebay.logstorm.server.entities.PipelineExecutionStatus;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

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
public class ExecutionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionManager.class);

    private final ExecutorService executorService;
    private final Map<Object,TaskExecutor> executorMap;
    private static ExecutionManager instance;
    private final static int threadPoolCoreSize = 10;
    private final static int threadPoolMaxSize = 20;
    private final static long threadPoolShrinkTime = 60000L;

    private ExecutionManager(){
        executorService = new ThreadPoolExecutor(threadPoolCoreSize, threadPoolMaxSize, threadPoolShrinkTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        executorMap = new TreeMap<>();
    }

    public Map<Object,TaskExecutor> getExecutorMap(){
        return executorMap;
    }

    public static ExecutionManager getInstance(){
        if(instance == null){
            instance = new ExecutionManager();
        }
        return instance;
    }

    /**
     * Submit not managed task
     *
     * @param runnable
     * @return
     */
    public Future<?> submit(Runnable runnable){
        return this.executorService.submit(runnable);
    }

    public TaskExecutor submit(Object id, Runnable runnable){
        if(executorMap.containsKey(id)){
            TaskExecutor executor = executorMap.get(id);
            if(!executor.isAlive() || executor.getState() == Thread.State.TERMINATED){
                LOG.info("Replacing dead executor: {}",executor);
                executorMap.remove(id);
            }else {
                throw new IllegalArgumentException("Duplicated id '" + id + "'");
            }
        }

        TaskExecutor worker = new TaskExecutor(runnable);
        LOG.info("Registering new executor {}: {}",id,worker);
        executorMap.put(id,worker);
        worker.setName(id.toString());
        worker.setDaemon(true);
        worker.start();
        return worker;
    }

    public TaskExecutor get(Object id){
        Preconditions.checkArgument(executorMap.containsKey(id), "Executor '" + id + "' not found");
        return executorMap.get(id);
    }

    public TaskExecutor stop(Object id) throws Exception{
        TaskExecutor worker = get(id);
        worker.interrupt();
        this.executorMap.remove(id);
        return worker;
    }

    public static PipelineExecutionStatus getWorkerStatus(java.lang.Thread.State state){
        if(whereIn(state,
                java.lang.Thread.State.RUNNABLE,
                java.lang.Thread.State.TIMED_WAITING,
                java.lang.Thread.State.WAITING
                )){
            return PipelineExecutionStatus.RUNNING;
        } else if(whereIn(state, java.lang.Thread.State.NEW)){
            return PipelineExecutionStatus.STARTING;
        } else if(whereIn(state, java.lang.Thread.State.TERMINATED)){
            return PipelineExecutionStatus.STOPPED;
        }
        throw new IllegalStateException("Unknown state: "+state);
    }

    public static PipelineExecutionStatus getTopologyStatus(String status) {
        if (whereIn(status, PipelineExecutionStatus.KILLED.toString())) {
            return PipelineExecutionStatus.STOPPING;
        } else {
            return PipelineExecutionStatus.RUNNING;
        }
    }

    private static boolean whereIn(java.lang.Thread.State state, java.lang.Thread.State... inStates){
        for(java.lang.Thread.State _state : inStates){
            if(_state == state){
                return true;
            }
        }
        return false;
    }

    private static boolean whereIn(String state, String... inStates){
        for(String _state : inStates){
            if(_state.equalsIgnoreCase(state)){
                return true;
            }
        }
        return false;
    }

    public void remove(Object id) {
        TaskExecutor executor = this.get(id);
        if(executor.isAlive()){
            throw new RuntimeException("Failed to remove alive executor '"+id+"'");
        }else{
            this.executorMap.remove(id);
        }
    }

    public boolean contains(Object id) {
        return executorMap.containsKey(id);
    }
}