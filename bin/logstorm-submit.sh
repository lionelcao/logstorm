#!/usr/bin/env bash

# Example:
#
# ./logstorm-submit                     |
#   --name application_name             | logstorm.app.name=application_name
#   --master storm://localhost:8080     | logstorm.app.master=storm://localhost:8080
#			 spark://localhost:8080     | logstorm.app.master=spark://localhost:8080
#            flink://localhost:8080     | logstorm.app.master=flink://localhost:8080
#   --mode local or cluster             | logstorm.app.mode=local
#   --input-queue-size  1000            | logstorm.app.input.queue=1000
#   --input-batch-size  50              | logstorm.app.input.batch=50
#   --filter-executor   1               | logstorm.app.filter.executor=1
#   application_pipeline.conf           | logstorm.app.config=Files.readAsString(pipeline.conf)
