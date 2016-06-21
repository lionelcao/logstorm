package com.ebay.logstorm.core.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

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
public class PipelineEnvironmentLoaderForTest {
    private final static Logger LOG = LoggerFactory.getLogger(PipelineEnvironmentLoaderForTest.class);
    private static File findAssemblyJarFile(String relativeToHomePath){
        String projectRootDir = System.getProperty("user.dir");
        String assemblyModuleTargeDirPath=relativeToHomePath == null? projectRootDir+"/assembly/target/": projectRootDir+relativeToHomePath+"/assembly/target/";
        File assemblyTargeDirFile = new File(assemblyModuleTargeDirPath);
        if(!assemblyTargeDirFile.exists()) {
            throw new IllegalStateException(assemblyModuleTargeDirPath + " not found, please execute 'mvn install -DskipTests' under " + projectRootDir + " to build the project firstly and retry");
        }
        String jarFileNameWildCard="logstorm-assembly-*.jar";
        Collection<File> jarFiles = FileUtils.listFiles(assemblyTargeDirFile,new WildcardFileFilter(jarFileNameWildCard), TrueFileFilter.INSTANCE);
        if(jarFiles.size() == 0){
            throw new IllegalStateException("jar is not found, please execute 'mvn install -DskipTests' from project root firstly and retry");
        }
        File jarFile = jarFiles.iterator().next();
        LOG.debug("Found pipeline.jar: {}",jarFile.getAbsolutePath());
        return jarFile;
    }

    public PipelineEnvironmentLoaderForTest(String relativeToHomePath){
        findAssemblyJarFile(relativeToHomePath);
        String path = findAssemblyJarFile(relativeToHomePath).getAbsolutePath();
        LOG.info("pipeline.jar: {}",path);
        System.setProperty("pipeline.jar",path);
    }

    public PipelineEnvironmentLoaderForTest(){
        this(null);
    }
}