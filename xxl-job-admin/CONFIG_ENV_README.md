# xxl-job-admin

环境变量说明

配置文件属性|环境变量|默认值|说明
---|---|---|---
server.port|SERVER_PORT|8080|服务端口号
server.context-path|SERVER_CONTEXT_PATH|/xxl-job-admin|应用路径
spring.datasource.url|SPRING_DATASOURCE_URL|jdbc:mysql://127.0.0.1:3306/xxl_job?Unicode=true&characterEncoding=UTF-8|填写自己的数据库链接
spring.datasource.username|SPRING_DATASOURCE_USERNAME|root|数据库账号
spring.datasource.password|SPRING_DATASOURCE_USERNAME|root_pwd|数据库密码
spring.mail.host|SPRING_MAIL_HOST|smtp.qq.com|邮箱服务器host
spring.mail.port|SPRING_MAIL_PORT|25|邮箱服务器端口号
spring.mail.username|SPRING_MAIL_USERNAME|xxx@qq.com|邮件发送账号
spring.mail.password|SPRING_MAIL_PASSWORD|xxx|邮件发送账号密码
xxl.job.accessToken|XXL_JOB_ACCESS_TOKEN| |access token
xxl.job.i18n|XXL_JOB_I18N| |default empty as chinese, "en" as english
