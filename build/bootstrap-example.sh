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

echo "---------------------------------------"
echo " Sample: Sample Pipeline for Starter"
echo "---------------------------------------"
echo "[STEP 1] Create cluster by POST http://localhost:8080/api/cluster"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/cluster -d '
	{
	  "uuid": "47bda9f1-f0c0-458d-be1b-3d54b6bec25b",
	  "name":"sample_storm_cluster",
	  "adapterClass":"com.ebay.logstorm.server.platform.storm.StormExecutionPlatform"
	}
'
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/cluster -d '
	{
	  "name":"sample_spark_cluster",
	  "adapterClass":"com.ebay.logstorm.server.platform.spark.SparkExecutionPlatform"
	}
'
echo '\n'
echo "[STEP 2] View created cluster by GET http://localhost:8080/api/cluster/sample_storm_cluster"
sleep 1
curl --silent -XGET http://localhost:8080/api/cluster/sample_storm_cluster

echo '\n'
echo "[STEP 3] Create pipeline by POST http://localhost:8080/api/pipeline"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/pipeline -d '
	{
	  "uuid": "fc6ffb53-2905-4665-b0ae-ed5f32565fee",
	  "name": "test_pipeline",
	  "pipeline": "input { generator { type => \"one_stream\" lines => [ \"GET /user 0.98\", \"GET /user 1.98\", \"GET /user 2.98\" ] count => 3 } } output { stdout { codec => rubydebug } }",
	  "properties": {"a":"b"},
	  "execution": null,
	  "cluster":{
	        "uuid": "47bda9f1-f0c0-458d-be1b-3d54b6bec25b"
	    },
	  "mode": "LOCAL"
	}
'

echo '\n'
echo "[STEP 4] View created pipeline by GET http://localhost:8080/api/pipeline/test_pipeline"
sleep 1
curl --silent -XGET http://localhost:8080/api/pipeline/test_pipeline

echo '\n'
echo "[STEP 5] Start pipeline through POST http://localhost:8080/api/pipeline/start"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/pipeline/start -d '
	{"name": "test_pipeline"}
'

echo '\n'
sleep 1
echo "[STEP 6] Check pipeline status through GET http://localhost:8080/api/pipeline/test_pipeline"
curl --silent -XGET http://localhost:8080/api/pipeline/test_pipeline

echo '\n'
echo "---------------------------------------"
echo " Done. See more detail or replay with $0."

rm ./server.log
kill -9 $SERVER_PORT