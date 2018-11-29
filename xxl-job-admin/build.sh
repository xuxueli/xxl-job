#!/bin/bash

echo "mvn ..."
mvn clean package

echo "mkdir -p tmp/"
mkdir -p tmp

echo "cp files to tmp/"
cp -rv target/xxl-job-admin-1.9.2 Dockerfile docker-entrypoint.sh tmp/

echo "cd tmp/"
cd tmp/

echo "docker build..."
docker -H localhost build -t huahouye/xxl-job-admin:1.9.2 .

echo "cd .."
cd ..

echo "rm -fr tmp/"
rm -fr tmp/
