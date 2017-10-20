FROM tomcat:8.5.23-jre8-alpine
MAINTAINER Kevin Zou <kevinz@weghst.com>

# log4j configuration file location
ENV LOG4J_CONFIG log4j.xml
ENV JAVA_OPTS $JAVA_OPTS -Dlog4j.configuration=$LOG4J_CONFIG

RUN rm -rf $CATALINA_HOME/webapps/ROOT
ADD target/ROOT.war $CATALINA_HOME/webapps/ROOT.war
