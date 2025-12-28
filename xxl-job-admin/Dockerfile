# base image
FROM openjdk:21-jdk-slim

# maintainer
MAINTAINER xuxueli

# set params
ENV PARAMS=""

# set timezone
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# copy jar
ADD target/xxl-job-admin-*.jar /app.jar

# command
# log home: -e LOG_HOME=/data/applogs
# jvm options: -e JAVA_OPTS="-Xms128m -Xmx128m"
# app params: -e PARAMS="--server.port=8080"
ENTRYPOINT ["sh","-c","java ${LOG_HOME:+-DLOG_HOME=$LOG_HOME} -jar $JAVA_OPTS /app.jar $PARAMS"]