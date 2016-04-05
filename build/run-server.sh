#!/usr/bin/env bash

cd $(dirname $0)/../

if [ ! -e server/target ];then
	mvn clean install -DskipTests
fi

mvn exec:java -pl server -Dexec.mainClass="com.ebay.logstorm.server.LogStormServer" -Dserver.port=8080