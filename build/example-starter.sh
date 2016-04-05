#!/usr/bin/env bash

cd $(dirname $0)/../

echo "RUN: mvn clean install -DskipTests"
mvn clean install -DskipTests

echo "RUN: mvn exec:java -pl server -Dexec.mainClass=\"com.ebay.logstorm.server.LogStormServer\""
mvn exec:java -pl server -Dexec.mainClass="com.ebay.logstorm.server.LogStormServer" 1>/dev/null 2>&1 &
export SERVER_PORT=$!
echo "Sever is running at PORT: $SERVER_PORT"

echo "Sleep 10 seconds for server to be ready"
sleep 10

echo "RUN: ./examples/logstorm-samples/scripts/sample-pipeline-starter.sh"
./examples/logstorm-samples/scripts/sample-pipeline-starter.sh

ps -ef | grep com.ebay.logstorm.server.LogStormServer | awk '{print $2}' | sed 1d| while read pid;do kill $pid;done