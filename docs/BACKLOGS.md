Backlog
======

TODO
----
* Pipeline Server and Management UI
* LogStash Siddhi Filter Plugin (https://github.com/wso2/siddhi) 
* Dynamic topology/lifecycle management 
* More Pipeline Runner Support
    * Apache Spark Runner (http://spark.apache.org/)
    * Apache Flink Runner (http://flink.apache.org/)
    * Apache Beam Runner (http://beam.incubator.apache.org/)
    * High Performance Local Runner
* Replace default logstash message queue with [Disruptor](https://github.com/LMAX-Exchange/disruptor)
* LogStash input/output plugin metadata model and scanner, which could be used to provide more friendly experience while defining pipeline.
* Finalize the project name currently using "LogStorm", including:
    * Rename package name to `org.apache.projectName`
    * Rename `PipelineContext` to `ProjectNameContext`
* Support to use LogStash as multi-stages pipeline instead of simple input-filter-output

Done
----
* LogStash Pipeline Parser
* LogStash Plugin Proxy API
* Apache Storm Runner (http://storm.apache.org/)