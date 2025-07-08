FROM openjdk:21-jdk-slim
#FROM openjdk:17-jdk-slim

MAINTAINER xuxueli

ENV PARAMS=""

ENV XXL_JOB_SERVER_PORT=8080 \
    XXL_JOB_SERVER_SERVLET_CONTEXT_PATH="/xxl-job-admin" \
    XXL_JOB_DATABASE_HOST=127.0.0.1 \
    XXL_JOB_DATABASE_PORT=3306 \
    XXL_JOB_DATABASE=xxl_job \
    XXL_JOB_DATABASE_PARAM="useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai" \
    XXL_JOB_DATABASE_USERNAME=root \
    XXL_JOB_DATABASE_PASSWORD=root_pwd \
    XXL_JOB_ACCESS_TOKEN=default_token \
    XXL_JOB_LOG_RETENTION_DAYS=30

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/xxl-job-admin-*.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]
