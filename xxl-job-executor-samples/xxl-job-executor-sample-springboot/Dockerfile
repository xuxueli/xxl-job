FROM openjdk:21-jdk-slim
MAINTAINER xuxueli

ENV LOG_HOME=/data/applogs
ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/xxl-job-executor-sample-springboot-*.jar /app.jar

ENTRYPOINT ["sh","-c","java -DLOG_HOME=$LOG_HOME -jar $JAVA_OPTS /app.jar $PARAMS"]