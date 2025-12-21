# base image
FROM eclipse-temurin:21-jre-jammy

# maintainer
MAINTAINER xuxueli

# install mysql client for database initialization
RUN apt-get update && \
    apt-get install -y default-mysql-client && \
    rm -rf /var/lib/apt/lists/*

# set params
ENV PARAMS=""

# database initialization environment variables
ENV DB_INIT=false \
    DB_HOST=127.0.0.1 \
    DB_PORT=3306 \
    DB_DATABASE=xxl_job \
    DB_USERNAME=root \
    DB_PASSWORD=root_pwd

# set timezone
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# create directory for init scripts
RUN mkdir -p /docker-entrypoint-initdb.d

# copy SQL initialization script
COPY doc/db/tables_xxl_job.sql /docker-entrypoint-initdb.d/tables_xxl_job.sql

# copy jar
ADD xxl-job-admin/target/xxl-job-admin-*.jar /app.jar

# copy entrypoint script
COPY xxl-job-admin/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# command
# database init: -e DB_INIT=true (default: false)
# database config: -e DB_HOST=mysql -e DB_PORT=3306 -e DB_DATABASE=xxl_job -e DB_USERNAME=root -e DB_PASSWORD=root_pwd
# log home: -e LOG_HOME=/data/applogs
# jvm options: -e JAVA_OPTS="-Xms128m -Xmx128m"
# app params: -e PARAMS="--server.port=8080"
ENTRYPOINT ["/docker-entrypoint.sh"]