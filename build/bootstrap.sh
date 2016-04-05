#!/usr/bin/env bash

cd $(dirname $0)/../

echo "RUN: mvn install"
mvn install
