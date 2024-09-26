FROM openjdk:8-jre-slim
MAINTAINER xuxueli


ENV PARAMS=""

ENV SERVER_PORT=8080

ENV ACCESS_TOKEN=default_token

ENV STANDLONE=false

ENV DATABASE_INIT=true
ENV DATABASE_PLATEFORM=mysql

ENV LOGBACK_ENV=test

ENV DATASOURCE_URL="jdbc:mysql://localhost:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai"
ENV DATASOURCE_USERNAME=root
ENV DATASOURCE_PASSWORD=123456
ENV DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver

ENV DATASOURCE_TEST_QUERY="SELECT 1 FROM DUAL"

ENV MAIL_HOST=smtp.qq.com
ENV MAIL_PORT=25
ENV MAIL_USERNAME=xxx@qq.com
ENV MAIL_FROM=$MAIL_USERNAME
ENV MAIL_PASSWORD=xxx

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/xxl-job-admin-*.jar /xxl-job-admin/xxl-job-admin.jar

WORKDIR /xxl-job-admin

EXPOSE $SERVER_PORT

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS -Dlogback.app.env=$LOGBACK_ENV -Dspring.profiles.active=image xxl-job-admin.jar $PARAMS"]