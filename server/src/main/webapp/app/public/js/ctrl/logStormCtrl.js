(function() {
	'use strict';

	var logStormControllers = angular.module('logStormControllers', ['ui.bootstrap']);

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

		$scope.clusterList._promise.then(function () {
			$.each($scope.clusterList, function (i, cluster) {
				$scope.clusters[cluster.uuid] = cluster.name;
			});
		});

		$scope.deleteApplication = function (application) {
			UI.deleteConfirm(application.name).then(null, null, function(holder) {
				API.delete("api/pipeline", application.uuid).then(function () {
					holder.closeFunc();
					location.reload();
				});
			});
		};
	});
})();
