#!/usr/bin/env bash

logstash_version=$1
logstash_package=logstash-all-plugins-${logstash_version}.tar.gz
logstash_dir=logstash-${logstash_version}

if [ "$2" != "" ] && [ "$2" != "logstorm-parent" ];then
	exit 0
fi

cd $(dirname $0)/../

if [ ! -e vendor ];then
	echo "Create vendor"
	mkdir vendor
fi

if [ -e vendor/${logstash_dir} ];then
	echo "vendor/${logstash_dir} already exists"
	exit 0
fi

cd vendor

if [ ! -e vendor/${logstash_package} ];then
	wget https://download.elastic.co/logstash/logstash/${logstash_package}
fi

tar xzvf ${logstash_package}

exit 0