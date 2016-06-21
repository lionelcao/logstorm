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
		$scope._name = "";
		$scope._type = "storm";
		$scope._properties = "";

		var typeMapping = {
			storm: "com.ebay.logstorm.server.platform.storm.StormExecutionPlatform",
			spark: "com.ebay.logstorm.server.platform.spark.SparkExecutionPlatform"
		};

		$scope.create = function () {
			var _properties = {};
			try {
				_properties = JSON.parse($scope._properties);
			} catch(err) {}

			API.post("api/cluster", {
				name: $scope._name,
				type: $scope._type,
				properties: _properties,
				adapterClass: typeMapping[$scope._type]
			}).then(function () {
				location.href = "#/cluster";
			});
		};
	});

	// ===========================================================
	// =                       Application                       =
	// ===========================================================
	logStormControllers.controller('applicationCtrl', function($scope, API, UI){
		$scope.applicationList = API.get("api/pipeline");
		$scope.clusterList = API.get("api/cluster");
		$scope.clusters = {};
		clusterMapWrapper($scope.clusters, $scope.clusterList);

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
		$scope._properties = '{}';

		$scope.clusterList._promise.then(function () {
			$scope._cluster = ($scope.clusterList[0] || {}).name;
		});

		$scope.create = function () {
			API.post("api/pipeline", {
				name: $scope._name,
				mode: $scope._mode,
				cluster: {
					uuid: $scope.clusters[$scope._cluster].uuid
				},
				pipeline: $scope._pipeline
			}).then(function () {
				location.href = "#/application/"+ $scope._name;
			});
		};
	});

	logStormControllers.controller('applicationViewCtrl', function($scope,$stateParams, API) {
		API.get("api/pipeline/"+$stateParams.id,function(data){
			$scope.application = data;
		})
		API.get("api/pipeline/"+$stateParams.id+"/compiled",function(data){
			$scope.compiled_pipeline=data;
		})
	});
})();
