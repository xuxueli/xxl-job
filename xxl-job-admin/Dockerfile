FROM tomcat:8.5.23-jre8-alpine
MAINTAINER kevinz@weghst.com

RUN rm -rf $CATALINA_HOME/webapps/ROOT
ADD target/ROOT.war $CATALINA_HOME/webapps/ROOT.war
