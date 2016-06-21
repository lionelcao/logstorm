#!/usr/bin/env sh

echo "---------------------------------------"
echo " Sample: Sample Pipeline for Starter"
echo "---------------------------------------"
echo "[STEP 1] Create cluster by POST http://localhost:8080/api/cluster"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/cluster -d '
	{
	  "uuid": "7ef7d151-fe79-4b7c-847e-5d1e682ddfb4",
	  "name": "sample_spark_cluster",
	  "adapterClass": "com.ebay.logstorm.server.platform.spark.SparkExecutionPlatform",
	  "properties": {
	    "spark.master": "spark://127.0.0.1:7077",
	    "spark.rest": "http://127.0.0.1:?/api/v1/applications/"
	  },
	  "type": "spark"
	}
'

echo '\n'
echo "[STEP 2] View created cluster by GET http://localhost:8080/api/cluster/sample_storm_cluster"
sleep 1
curl --silent -XGET http://localhost:8080/api/cluster/sample_spark_cluster

echo '\n'
echo "[STEP 3] Create pipeline by POST http://localhost:8080/api/pipeline"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/pipeline -d '
	{
	  "uuid": "d8f6837b-331d-475d-b202-5c1466e254d8",
	  "name": "test_pipeline_spark_local",
	  "pipeline": "input { generator { type => \"one_stream\" lines => [ \"GET /user 0.98\", \"GET /user 1.98\", \"GET /user 2.98\" ] count => 3 } } output { stdout { codec => rubydebug } }",
	  "properties": {"a":"b"},
	  "instances": null,
	  "cluster":{
	        "uuid": "7ef7d151-fe79-4b7c-847e-5d1e682ddfb4"
	   },
	  "mode": "LOCAL"
	}
'

echo '\n'
echo "[STEP 4] View created pipeline by GET http://localhost:8080/api/pipeline/test_pipeline"
sleep 1
curl --silent -XGET http://localhost:8080/api/pipeline/test_pipeline_spark_local

echo '\n'
echo "[STEP 5] Start pipeline through POST http://localhost:8080/api/pipeline/start"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/pipeline/start -d '
	{"name": "test_pipeline_spark_local"}
'

echo '\n'
sleep 1
echo "[STEP 6] Check pipeline status through GET http://localhost:8080/api/pipeline/test_pipeline"
curl --silent -XGET http://localhost:8080/api/pipeline/test_pipeline_spark_local

echo '\n'
echo "---------------------------------------"
echo " Done. See more detail or replay with $0."
