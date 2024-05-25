1. /etc/default/xxl-job-executor-mgmt-9999

XXL_JOB_EXECUTOR_APPNAME=mgmt
XXL_JOB_EXECUTOR_PORT=9999

2. /etc/systemd/system/xxl-job-executor@.service

[Unit]
Description=xxl-job-executor service

[Service]
ExecStart=/usr/bin/java -jar /opt/xxl-job-executor-2.4.1.jar
Restart=always
User=root
EnvironmentFile=/etc/default/xxl-job-executor-%i
Environment=XXL_JOB_ACCESS_TOKEN=default_token
Environment=XXL_JOB_ADMIN_ADDRESSES=http://127.0.0.1:8080/xxl-job-admin
Environment=JAVA_OPTS="-Xmx512m"

[Install]
WantedBy=default.target

3. systemctl enable xxl-job-executor@mgmt-9999.service

4. systemctl start xxl-job-executor@mgmt-9999.service