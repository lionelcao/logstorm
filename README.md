LogStorm
========

Prerequisites
-------------

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Apache Maven](https://maven.apache.org/)

Build
-----

    git clone git@github.corp.ebay.com:hchen9/logstorm.git
    cd logstorm
    mvn install
  
Test
----

* Getting started

		mvn test -Dtest=com.ebay.logstorm.runner.storm.TestStormPipelineRunner

* Run all unit tests 
	
		mvn test

Backlog
-------

1. Pipeline Server and Management UI
2. LogStash Siddhi Filter Plugin (https://github.com/wso2/siddhi) 
3. More Pipeline Runner Support
* Apache Spark Runner (http://spark.apache.org/)
* Apache Flink Runner (http://flink.apache.org/)
* Apache Beam Runner (http://beam.incubator.apache.org/)
* High Performance Local Runner
4. Replace default logstash message queue with [Disruptor](https://github.com/LMAX-Exchange/disruptor)

Contact
-------

* [@hchen9](https://github.corp.ebay.com/hchen9) (hchen9@ebay.com)
