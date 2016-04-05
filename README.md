LogStorm
========

Prerequisites
-------------

* [Apache Maven](https://maven.apache.org/)
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Getting Started
---------------
Bootstrap from source code and run examples
	
	    ./build/bootstrap-example.sh

Development
-----------

* Build with source code

		git clone git@github.corp.ebay.com:hchen9/logstorm.git
        cd logstorm
        mvn install

* Start LogStorm Server: 
	
		mvn exec:java -pl server -Dexec.mainClass="com.ebay.logstorm.server.LogStormServer" -Dserver.port=8080

Test
----
* Run all unit tests 
	
		mvn test
		
* Test single test, for example TestStormPipelineRunner

		mvn test -Dtest=com.ebay.logstorm.runner.storm.TestStormPipelineRunner -DfailIfNoTests=false
 
Contact
-------

* [@hchen9](https://github.corp.ebay.com/hchen9) (hchen9@ebay.com)
