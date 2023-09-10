FROM openjdk:8-jre-slim

# MAINTAINER
MAINTAINER 852203465@qq.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

# put jar into container
ADD target/*.jar app.jar

ENTRYPOINT ["/bin/sh", "-c", "set -e && java -Xms2048m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=utf-8 -jar /app.jar"]
