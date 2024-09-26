#!/bin/bash

VERSION=2.4.1

TAG="xxl-job:$VERSION"

echo build $TAG ...

docker build -f Dockerfile -t $TAG

echo build $TAG done.

echo "------------------------------------------"
echo "    docker run -d -p 8080:8080 %TAG%"
echo "    http://localhost:8080/xxl-job-admin"