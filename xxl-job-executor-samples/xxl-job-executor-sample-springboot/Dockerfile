FROM openjdk:8-jre

LABEL name="caryyu/xxl-job-executor-sample-springboot" \
			maintainer="Caryyu <343194291@qq.com>" \
			version="0.1" \
			description="A common xxl-job executor image for easily shipping via Docker"

ENV jarName=xxl-job-executor-sample-springboot-2.0.1.jar

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/TZ /etc/localtime && echo TZ /etc/localtime && echo TZ > /etc/timezone

ADD ./target/$jarName /

ADD ./docker-entrypoint.sh /

RUN chmod u+x /docker-entrypoint.sh

WORKDIR /

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]