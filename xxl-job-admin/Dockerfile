FROM tomcat:8.5

ENV TOMCAT_WEBAPPS=/usr/local/tomcat/webapps \
    TIME_ZONE=Asia/Shanghai \
    JAVA_OPTS="-Duser.timezone=Asia/Shanghai -Xmx256m" \
    \
    ### xxl-job db  (use &amp; replace & in xml)
    XXL_JOB_DB_DRIVERCLASS='com.mysql.jdbc.Driver' \
	XXL_JOB_DB_URL='jdbc:mysql://localhost:3306/xxl-job?useUnicode=true\&characterEncoding=UTF-8' \
	XXL_JOB_DB_USER='root' \
	XXL_JOB_DB_PASSWORD='root_pwd' \
	\
	### xxl-job email
	XXL_JOB_MAIL_HOST=smtp.163.com \
	XXL_JOB_MAIL_PORT=25 \
	XXL_JOB_MAIL_USERNAME=ovono802302@163.com \
	XXL_JOB_MAIL_PASSWORD=asdfzxcv \
	XXL_JOB_MAIL_SENDNICK=《任务调度平台XXL-JOB》 \
	\
	### xxl-job login
	XXL_JOB_LOGIN_USERNAME='admin' \
	XXL_JOB_LOGIN_PASSWORD='123456' \
	\
	### xxl-job, access token
	XXL_JOB_ACCESSTOKEN= \
	\
	### xxl-job, i18n (default empty as chinese, "en" as english)
	XXL_JOB_I18N=

COPY xxl-job-admin-1.9.2 $TOMCAT_WEBAPPS/xxl-job-admin
COPY docker-entrypoint.sh /

RUN rm -rf $TOMCAT_WEBAPPS/ROOT && \
	rm -rf $TOMCAT_WEBAPPS/docs && \
	rm -rf $TOMCAT_WEBAPPS/examples && \
	rm -rf $TOMCAT_WEBAPPS/host-manager && \
	rm -rf $TOMCAT_WEBAPPS/manager && \
    ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && \
    echo $TIME_ZONE > /etc/timezone && \
    chmod +x /docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
