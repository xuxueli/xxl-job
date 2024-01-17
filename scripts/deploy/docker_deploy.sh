#!/bin/bash
current=$(cd "$(dirname "$0")";pwd)
pwd
# 拼接镜像完整路径
version="latest"
path="47.94.15.26:30014/xxx-jobs"
if [ -z $1 ]; then
  version="latest"
else
  version=$1
fi
# 输出镜像路径
echo '================================================================================================'
echo 'IMAGE: '$path
echo 'TAG: '${version}
echo '================================================================================================'
# 重新打包
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "mvn clean package failed"
    exit
fi
# 生成amd64平台镜像
docker buildx build --platform=linux/amd64 -t ${path}:${version} ./xxl-job-admin
#先登录
#docker login --username=admin 47.94.15.26:30014
#密码 8szBZSttgqnxpv

docker push ${path}:${version}
if [ $? -ne 0 ]; then
    echo "docker push failed"
    exit
fi
#docker rmi ${path}:${version}
echo '执行成功!' $(date "+%Y-%m-%d %H:%M:%S")
# admin JzKg5mOldcisrc
ssh admin@47.94.15.26 'sh /home/admin/upgrade_xxxbusiness.sh'