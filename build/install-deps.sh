#!/usr/bin/env bash

logstash_version=$1
logstash_package=logstash-all-plugins-${logstash_version}.tar.gz
logstash_dir=logstash-${logstash_version}
logstash_download_link="https://download.elastic.co/logstash/logstash/${logstash_package}"

if [ "$2" != "" ] && [ "$2" != "logstorm-parent" ];then
	exit 0
fi

if [ "$logstash_version" == "" ];then
    echo "Usage: $0 [logstash-version]" 1>&2
    exit 1
fi

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] Installing Logstash (version: ${logstash_version})"
echo "[INFO] ------------------------------------------------------------------------"

cd $(dirname $0)/../build

if [ ! -e vendor ];then
	echo "[INFO] Create directory 'build/vendor'"
	mkdir -p vendor
fi

cd vendor

if [ -e ${logstash_dir} ];then
	echo "[INFO] build/vendor/${logstash_dir} already exists"
	exit 0
fi

$(which curl 1>/dev/null 2>&1)
if [ "$?" != "0" ];then
    echo "[ERROR] Failed to install logstash because command 'curl' (https://curl.haxx.se/) is not found" 1>&2
    echo "[ERROR] To resolve the problem, you must install 'curl' compatible with your operation system, " 1>&2
    echo "[ERROR] or manually download logstash from ${logstash_download_link} and extract into `pwd`/${logstash_dir}" 1>&2
    echo "[ERROR] then retry to build again" 1>&2
    exit 1
fi

if [ ! -e ${logstash_package} ];then
    echo "[INFO] Installing ${logstash_download_link} into build/vendor/${logstash_dir}"
	curl ${logstash_download_link} | tar xz
fi

if [ -e ${logstash_dir} ];then
    echo "[INFO] Successfully installed logstash into build/vendor/${logstash_dir}"
    exit 0
else
    echo "[ERROR] Failed to install logstash, because build/vendor/${logstash_dir} not exist"
    exit 1
fi