package com.ebay.logstorm.server;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.server.entities.PipelineEntity;
import com.ebay.logstorm.server.entities.PipelineExecutionEntity;
import com.ebay.logstorm.server.platform.storm.SparkExecutionPlatform;
import org.junit.Test;

import java.util.Properties;

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

public class TestSparkExecutionPlatform {
    @Test
    public void testStart() {
        SparkExecutionPlatform sparkExecutionPlatform = new SparkExecutionPlatform();
        sparkExecutionPlatform.prepare(new Properties());

        PipelineExecutionEntity entity = new PipelineExecutionEntity();
        PipelineEntity pipelineEntity = new PipelineEntity();
        entity.setPipeline(pipelineEntity);

        pipelineEntity.setMode(LogStormConstants.DeployMode.LOCAL);
        pipelineEntity.setName("simple-generator-stdout-pipeline");
        pipelineEntity.setPipeline("input {\n" +
                "\tgenerator {\n" +
                "\t\ttype => \"one_stream\"\n" +
                "\t\tlines => [\n" +
                "\t\t\t\"Dec 23 12:11:43 louis postfix/smtpd[31499]: connect from unknown[95.75.93.154]\",\n" +
                "\t\t\t\"Dec 23 14:42:56 louis named[16000]: client 199.48.164.7#64817: query (cache) 'amsterdamboothuren.com/MX/IN' denied\",\n" +
                "            \"Dec 23 14:30:01 louis CRON[619]: (www-data) CMD (php /usr/share/cacti/site/poller.php >/dev/null 2>/var/log/cacti/poller-error.log)\",\n" +
                "            \"Dec 22 18:28:06 louis rsyslogd: [origin software=\\\"rsyslogd\\\" swVersion=\\\"4.2.0\\\" x-pid=\\\"2253\\\" x-info=\\\"http://www.rsyslog.com\\\"] rsyslogd was HUPed, type 'lightweight'.\"\n" +
                "\t\t]\n" +
                "\t\tcount => 5\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "filter{\n" +
                "\tgrok {\n" +
                "      match => { \"message\" => \"%{SYSLOGTIMESTAMP:syslog_timestamp} %{SYSLOGHOST:syslog_hostname} %{DATA:syslog_program}(?:\\[%{POSINT:syslog_pid}\\])?: %{GREEDYDATA:syslog_message}\" }\n" +
                "      add_field => [ \"received_at\", \"%{@timestamp}\" ]\n" +
                "      add_field => [ \"received_from\", \"%{host}\" ]\n" +
                "    }\n" +
                "    date {\n" +
                "      match => [ \"syslog_timestamp\", \"MMM  d HH:mm:ss\", \"MMM dd HH:mm:ss\" ]\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "output {\n" +
                "  stdout {\n" +
                "    codec => rubydebug\n" +
                "  }\n" +
                "}");
        pipelineEntity.setParallelism(4);
        pipelineEntity.setProperties(new Properties());
        try {
            sparkExecutionPlatform.start(entity);
            sparkExecutionPlatform.status(entity);
            sparkExecutionPlatform.stop(entity);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
