LogStorm
========

Prerequisites
-------------

* [Apache Maven](https://maven.apache.org/)
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Documentation
-------------
You can find the more documentation on [docs](/docs). This README file only contains basic setup instructions.

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

* Test sample pipeline through REST API
	
		./examples/scripts/run-sample-pipeline.sh
		
		
Code Style
----------
* Java Style: [https://google.github.io/styleguide/javaguide.html](https://google.github.io/styleguide/javaguide.html)
* Angular Style: [https://github.com/johnpapa/angular-styleguide/blob/master/a1/README.md](https://github.com/johnpapa/angular-styleguide/blob/master/a1/README.md)

Contact
-------

* [@haoch](https://github.com/haoch) (hao at apache dot org)
