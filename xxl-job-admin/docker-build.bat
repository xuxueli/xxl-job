@echo off

set VERSION=2.4.1

set TAG=xxl-job:%VERSION%

echo build image %TAG% ...

docker build -f Dockerfile -t %TAG%

echo build image %TAG% done.

echo ------------------------------------------
echo     docker run -d -p 8080:8080 %TAG%
echo     http://localhost:8080/xxl-job-admin