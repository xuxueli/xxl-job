# 基础镜像使用java
FROM eclipse-temurin:17-jre

MAINTAINER enjoy-ai

ENV PARAMS=""
WORKDIR /home/admin
ADD target/xxl-job-admin-*.jar /home/admin/app.jar
# TODO 时区调整
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/admin/app.jar"]