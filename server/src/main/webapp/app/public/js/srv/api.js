(function() {
	'use strict';

	//var host  = "http://localhost:8080/";
	var host = "/";
	/* App Module */
	var logStormApp = angular.module('logStormApp');

	logStormApp.service('API', function($http) {
		var API = {
			get: function (url) {
				var list = [];
				list._promise = $http.get(host + url).then(function (res) {
					console.log(res);
					var result = res.data;
					list.splice(0);
					Array.prototype.push.apply(list, result.data);
					return result;
				},function(res){
					console.error(res);
					alert(res.data.message);
				});
				return list;
			},
			post: function (url, data) {
				return $http.post(host + url, data);
			},
			delete: function (url, data) {
				if(typeof data === "string") {
					return $http.delete(host + url + "/" + data);
				} else {
					return $http.delete(host + url, {data: data});
				}
			}
		};

		API.get("api/cluster")._promise.then(null, function () {
			host = "http://localhost:8080/";
		});

		return API;
	});
}());
