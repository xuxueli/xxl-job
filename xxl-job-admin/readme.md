## 运行

首先，创建初始化好数据库 xxl-job

然后，运行 docker run -e <XXL_JOB_*>=[value] 可以修改 xxl-job-admin 运行时的配置参数。

```
docker run --rm -p 8080:8080 --name xxl-job-admin \
-e XXL_JOB_DB_DRIVERCLASS=com.mysql.jdbc.Driver \
-e XXL_JOB_DB_URL='jdbc:mysql://localhost:3306/xxl-job?useUnicode=true\&characterEncoding=UTF-8' \
-e XXL_JOB_DB_USER=root \
-e XXL_JOB_DB_PASSWORD=root_pwd \
-e XXL_JOB_MAIL_HOST=smtp.163.com \
-e XXL_JOB_MAIL_PORT=25 \
-e XXL_JOB_MAIL_USERNAME=ovono802302@163.com \
-e XXL_JOB_MAIL_PASSWORD=asdfzxcv \
-e XXL_JOB_MAIL_SENDNICK=《任务调度平台XXL-JOB》 \
-e XXL_JOB_LOGIN_USERNAME=username \
-e XXL_JOB_LOGIN_PASSWORD=password \
-e XXL_JOB_ACCESSTOKEN= \
-e XXL_JOB_I18N= \
huahouye/xxl-job-admin:1.9.2
```

浏览器访问 http://localhost:8080/xxl-job-admin

用户名 username，密码 password

## 构建 Docker 镜像

可以自己构建镜像

```
cd xxl-job-admin && \
chmod +x build.sh && \
./build.sh
```
