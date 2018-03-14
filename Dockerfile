FROM tomcat
MAINTAINER youji@ebay.com

ADD xxl-job-admin/target/xxl-job-admin.*.war /usr/local/tomcat/webapps/scheduler.war
ADD xxl-job-core/target/xxl-job-core.*.jar /usr/local/tomcat/webapps/
ADD xxl-job-executor-samples/xxl-job-executor-sample-springboot/target/xxl-job-executor-sample*..jar /usr/local/tomcat/webapps/
ADD xxl-job-executor-samples/xxl-job-executor-sample-spring/target/xxl-job-executor-sample-spring.*.war /usr/local/tomcat/webapps/executor.war

CMD ["catalina.sh", "run"]
