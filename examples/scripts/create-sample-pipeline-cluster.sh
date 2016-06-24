#!/usr/bin/env sh

echo "---------------------------------------"
echo " Sample: Sample Pipeline for Starter"
echo "---------------------------------------"
echo "[STEP 1] Create cluster by POST http://localhost:8080/api/cluster"
sleep 1
curl --silent -H 'Content-Type:application/json' -XPOST http://localhost:8080/api/cluster -d '
	{
	  "uuid": "47bda9f1-f0c0-458d-be1b-3d54b6bec25b",
	  "name":"sample_storm_cluster",
	  "adapterClass":"com.ebay.logstorm.server.platform.storm.StormExecutionPlatform",
	  "properties":{
	    "storm.ui":"http://localhost:6060",
	    "storm.nimbus":"localhost"
	  }
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
	  "name": "test_pipeline_cluster",
	  "pipeline": "input { generator { type => \"one_stream\" lines => [ \"GET /user 0.98\", \"GET /user 1.98\", \"GET /user 2.98\" ] count => 3 } } output { stdout { codec => rubydebug } }",
	  "properties": {
	    "input.parallelism":3,
	    "output.parallelism":3,
	    "filter.parallelism":3
	  },
	  "parallelism":2,
	  "execution": null,
	  "cluster":{
	    "uuid": "47bda9f1-f0c0-458d-be1b-3d54b6bec25b"
	  },
	  "mode": "CLUSTER"
	}run-sample-pipeline-cluster.sh
'

echo '\n'
echo "[STEP 4] View created pipeline by GET http://localhost:8080/api/pipeline/test_pipeline_cluster"
sleep 1
curl --silent -XGET http://localhost:8080/api/pipeline/test_pipeline_cluster

echo '\n'
echo "---------------------------------------"
echo " Done. See more detail or replay with $0."