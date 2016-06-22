(function() {
	'use strict';

	var logStormControllers = angular.module('logStormControllers', ['ui.bootstrap']);

	// ===========================================================
	// =                        Functions                        =
	// ===========================================================
	function clusterMapWrapper(clusterMap, clusterList, mappingKey) {
		clusterList._promise.then(function () {
			$.each(clusterList, function (i, cluster) {
				clusterMap[cluster[mappingKey || "uuid"]] = cluster;
			});
		});
	}

	// ===========================================================
	// ===========================================================
	// ==                       Controller                      ==
	// ===========================================================
	// ===========================================================
	logStormControllers.controller('homeCtrl', function($scope, API, UI){});
	logStormControllers.controller('configurationCtrl', function($scope, API, UI){});

	// ===========================================================
	// =                         Cluster                         =
	// ===========================================================
	logStormControllers.controller('clusterCtrl', function($scope, API, UI) {
		$scope.clusterList = API.get("api/cluster");

		$scope.deleteCluster = function (cluster) {
			UI.deleteConfirm(cluster.name).then(null, null, function(holder) {
				API.delete("api/cluster", cluster.uuid).then(function () {
					holder.closeFunc();
					location.reload();
				});
			});
		};
	});

	logStormControllers.controller('clusterNewCtrl', function($scope, API) {
		$scope.clusterMetaList = API.get("api/platform");
		$scope.clusterMetaList._promise.then(function () {
			$scope._type = ($scope.clusterMetaList[0] || {}).type;
		});

		$scope._name = "";
		$scope._type = "";
		$scope._properties = {};

		$scope.currentCluster = function () {
			return $scope.clusterMetaList.find(function (cluster) {
				return cluster.type === $scope._type;
			}) || {};
		};

		var typeMapping = {
			storm: "com.ebay.logstorm.server.platform.storm.StormExecutionPlatform",
			spark: "com.ebay.logstorm.server.platform.spark.SparkExecutionPlatform"
		};

		$scope.create = function () {
			$.each($scope.currentCluster().fields, function (i, field) {
				var name = field.name;
				$scope._properties[name] = $scope._properties[name] || field.value;
			});

			API.post("api/cluster", {
				name: $scope._name,
				type: $scope._type,
				properties: $scope._properties,
				adapterClass: typeMapping[$scope._type]
			}).then(function () {
				location.href = "#/cluster";
			});
		};
	});

	logStormControllers.controller('clusterViewCtrl', function($scope, $stateParams, API, UI) {
		API.get("api/cluster/"+$stateParams.id)._promise.then(function (res) {
			$scope.cluster = res.data;
			console.log(res.data);
		});
	});

	// ===========================================================
	// =                       Application                       =
	// ===========================================================
	logStormControllers.controller('applicationCtrl', function($scope, API, UI){
		$scope.applicationList = API.get("api/pipeline");

		$scope.startApplication = function (application) {
			API.post("api/pipeline/start", {name: application.name}).then(function () {
				location.reload();
			});
		};

		$scope.stopApplication = function (application) {
			API.post("api/pipeline/stop", {name: application.name}).then(function () {
				location.reload();
			});
		};

		$scope.deleteApplication = function (application) {
			UI.deleteConfirm(application.name).then(null, null, function(holder) {
				API.delete("api/pipeline", application.uuid).then(function () {
					holder.closeFunc();
					location.reload();
				});
			});
		};
	});

	logStormControllers.controller('applicationNewCtrl', function($scope, API) {
		$scope.clusterList = API.get("api/cluster");
		$scope.clusters = {};
		clusterMapWrapper($scope.clusters, $scope.clusterList, "name");

		$scope._name = "";
		$scope._mode = "CLUSTER";
		$scope._cluster = "";
		$scope._pipeline = "";
		$scope._parallelism = 1;
		$scope._input_parallelism = 3;
		$scope._filter_parallelism = 3;
		$scope._output_parallelism = 3;
		$scope._properties = '{}';

		$scope.clusterList._promise.then(function () {
			$scope._cluster = ($scope.clusterList[0] || {}).name;
		});

		$scope.create = function () {
			console.log($scope.clusters, $scope._cluster, $scope.clusters[$scope._cluster]);

			API.post("api/pipeline", {
				name: $scope._name,
				mode: $scope._mode,
				cluster: {
					uuid: $scope.clusters[$scope._cluster].uuid
				},
				pipeline: $scope._pipeline,
				parallelism: $scope._parallelism,
				properties: {
					"input.parallelism": $scope._input_parallelism,
					"filter.parallelism": $scope._filter_parallelism,
					"output.parallelism": $scope._output_parallelism
				}
			}).then(function () {
				location.href = "#/application/"+ $scope._name;
			});
		};
	});

	logStormControllers.controller('applicationViewCtrl', function($scope,$stateParams, API) {
		API.get("api/pipeline/"+$stateParams.id)._promise.then(function (res) {
			$scope.application = res.data;
		});
		API.get("api/pipeline/" + $stateParams.id + "/compiled")._promise.then(function(res){
			$scope.compiled_pipeline = res.data;
		});

		$scope.startApplication = function () {
			API.post("api/pipeline/start", {name: $scope.application.name}).then(function () {
				location.reload();
			});
		};

		$scope.stopApplication = function () {
			API.post("api/pipeline/stop", {name: $scope.application.name}).then(function () {
				location.reload();
			});
		};
	});
})();
