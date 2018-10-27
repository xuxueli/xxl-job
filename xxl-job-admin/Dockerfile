FROM openjdk:7-jre-slim
MAINTAINER xuxueli

ENV PARAMS=""

ADD target/xxl-conf-admin-*.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar /app.jar $PARAMS"]