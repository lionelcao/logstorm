#!/usr/bin/env bash

echo "---------------------------------------"
echo " Sample: Sample Pipeline for Starter"
echo "---------------------------------------"

echo "[STEP 1] Create cluster by POST http://localhost:8080/api/cluster"
curl -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/cluster -d '
	{
	  "uuid": "47bda9f1-f0c0-458d-be1b-3d54b6bec25b",
	  "name":"sample_storm_cluster",
	  "adapterClass":"com.ebay.logstorm.server.platform.StormExecutionPlatform"
	}
'

echo "[STEP 2] View created cluster by GET http://localhost:8080/api/cluster/sample_storm_cluster"
curl -XGET http://localhost:8080/api/cluster/sample_storm_cluster

echo "[STEP 3] Create pipeline by POST http://localhost:8080/api/pipeline"
curl -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/pipeline -d '
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


echo "[STEP 4] View created pipeline by GET http://localhost:8080/api/pipeline/test_pipeline"
curl -XGET http://localhost:8080/api/pipeline/test_pipeline

echo "[STEP 5] Start pipeline through POST http://localhost:8080/api/pipeline/start"
curl -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/pipeline/start -d '
	{"name": "test_pipeline"}
'

echo "[STEP 6] Check pipeline status through POST http://localhost:8080/api/pipeline/test_pipeline"
curl -XGET http://localhost:8080/api/pipeline/test_pipeline

echo "---------------------------------------"
echo " Done "
echo "---------------------------------------"