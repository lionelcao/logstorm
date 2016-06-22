(function() {
	'use strict';

	var host  = "http://localhost:8080/";
	/* App Module */
	var logStormApp = angular.module('logStormApp');

	logStormApp.service('API', function($http) {
		return {
			get: function (url) {
				var list = [];
				list._promise = $http.get(host + url).then(function (res) {
					var result = res.data;
					list.splice(0);
					Array.prototype.push.apply(list, result.data);
					return result;
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
	});
}());
