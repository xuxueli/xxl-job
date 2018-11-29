#!/bin/bash

set -e

sed -i \
  -e "s#^\(xxl\.job\.db\.driverClass\s*=\s*\).*\$#\1"$XXL_JOB_DB_DRIVERCLASS"#" \
  -e "s#^\(xxl\.job\.db\.url\s*=\s*\).*\$#\1"$XXL_JOB_DB_URL"#" \
  -e "s#^\(xxl\.job\.db\.user\s*=\s*\).*\$#\1"$XXL_JOB_DB_USER"#" \
  -e "s#^\(xxl\.job\.db\.password\s*=\s*\).*\$#\1"$XXL_JOB_DB_PASSWORD"#" \
  \
  -e "s#^\(xxl\.job\.mail\.host\s*=\s*\).*\$#\1"$XXL_JOB_MAIL_HOST"#" \
  -e "s#^\(xxl\.job\.mail\.port\s*=\s*\).*\$#\1"$XXL_JOB_MAIL_PORT"#" \
  -e "s#^\(xxl\.job\.mail\.username\s*=\s*\).*\$#\1"$XXL_JOB_MAIL_USERNAME"#" \
  -e "s#^\(xxl\.job\.mail\.password\s*=\s*\).*\$#\1"$XXL_JOB_MAIL_PASSWORD"#" \
  -e "s#^\(xxl\.job\.mail\.sendNick\s*=\s*\).*\$#\1"$XXL_JOB_MAIL_SENDNICK"#" \
  \
  -e "s#^\(xxl\.job\.login\.username\s*=\s*\).*\$#\1"$XXL_JOB_LOGIN_USERNAME"#" \
  -e "s#^\(xxl\.job\.login\.password\s*=\s*\).*\$#\1"$XXL_JOB_LOGIN_PASSWORD"#" \
  \
  -e "s#^\(xxl\.job\.accessToken\s*=\s*\).*\$#\1"$XXL_JOB_ACCESSTOKEN"#" \
  -e "s#^\(xxl\.job\.i18n\s*=\s*\).*\$#\1"$XXL_JOB_I18N"#" \
  \
  $TOMCAT_WEBAPPS/xxl-job-admin/WEB-INF/classes/xxl-job-admin.properties

## 启动 tomcat
$CATALINA_HOME/bin/catalina.sh run