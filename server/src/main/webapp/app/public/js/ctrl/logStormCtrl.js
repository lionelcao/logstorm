(function() {
	'use strict';

	var logStormControllers = angular.module('logStormControllers', ['ui.bootstrap']);

	// ===========================================================
	// =                        Controller                       =
	// ===========================================================
	logStormControllers.controller('clusterCtrl', function($scope, API, UI) {
		$scope.clusterList = API.get("api/cluster");

		$scope.createCluster = function () {
			UI.createConfirm("Cluster", {}, [
				{field: "name"},
				{field: "adapterClass", type: "select", valueList: ["com.ebay.logstorm.server.platform.storm.StormExecutionPlatform", "com.ebay.logstorm.server.platform.spark.SparkExecutionPlatform"]}
			], function () {
			}).then(null, null, function(holder) {
				API.post("api/cluster", holder.entity).then(function () {
					holder.closeFunc();
					location.reload();
				});
			});
		};

		$scope.deleteCluster = function (cluster) {
			UI.deleteConfirm(cluster.name).then(null, null, function(holder) {
				API.delete("api/cluster", cluster.uuid).then(function () {
					holder.closeFunc();
					location.reload();
				});
			});
		};
	});
})();
