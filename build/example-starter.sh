#!/usr/bin/env bash

cd $(dirname $0)/../

echo "RUN: mvn clean install -DskipTests"
mvn clean install -DskipTests

echo "RUN: mvn exec:java -pl server -Dexec.mainClass=\"com.ebay.logstorm.server.LogStormServer\""
mvn exec:java -pl server -Dexec.mainClass="com.ebay.logstorm.server.LogStormServer" -Dserver.port=8080 1>server.log 2>&1 &
export SERVER_PORT=$!
echo "Sever is running at PORT: $SERVER_PORT with LOG: ./server.log"

echo "Sleep 10 seconds for server to be ready"
sleep 10

echo "RUN: ./examples/logstorm-samples/scripts/sample-pipeline-starter.sh"
./examples/logstorm-samples/scripts/sample-pipeline-starter.sh

rm ./server.log
kill -9 $SERVER_PORT