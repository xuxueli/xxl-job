# 从哪个基础镜像开始构建
FROM bellsoft/liberica-openjdk-alpine:17.0.2-9
# 镜像作者
MAINTAINER ximplez
# 环境构建参数，可以外部传入: docker build --build-arg BUILD_ENV=prod
ARG BUILD_ENV=prod
#ARG PORT=8080
ARG APP_ID=xxl-job-admin
# 构建的时候设置环境变量
ENV PARAMS=""
ENV WORKDIR=/home/applogs/$APP_ID
ENV LOG_PATH=${WORKDIR}/log VM_LOG="" GC_PARAM=""
# 设置虚拟机日志命令参数
ENV VM_LOG=${VM_LOG}" -Xloggc:${LOG_PATH}/$APP_ID.gc.log.$(date +%Y%m%d%H%M)"
ENV VM_LOG=${VM_LOG}" -XX:ErrorFile=${LOG_PATH}/$APP_ID.vmerr.log.$(date +%Y%m%d%H%M)"
ENV VM_LOG=${VM_LOG}" -XX:HeapDumpPath=${LOG_PATH}/$APP_ID.heaperr.log.$(date +%Y%m%d%H%M)"
# 设置虚拟机GC参数
ENV GC_PARAM=${GC_PARAM}" -XX:+HeapDumpOnOutOfMemoryError"
ENV GC_PARAM=${GC_PARAM}" -XX:+DisableExplicitGC"
ENV GC_PARAM=${GC_PARAM}" -XX:+PrintGCDetails"
ENV GC_PARAM=${GC_PARAM}" -XX:+PrintGCDateStamps"
ENV GC_PARAM=${GC_PARAM}" -XX:+PrintGCApplicationStoppedTime"
ENV GC_PARAM=${GC_PARAM}" -XX:+PrintGCApplicationConcurrentTime"
ENV GC_PARAM=${GC_PARAM}" -XX:+PrintCommandLineFlags"
ENV VM_CMD=""${VM_LOG}${GC_PARAM}
# 构建镜像需要运行的命令
# 切换工作目录，docker默认工作目录是 /
WORKDIR ${WORKDIR}
# 调整时区
RUN rm -f /etc/localtime \
    && ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone
# 将文件从路径<src> 复制添加到容器路径 <dest>
ADD target/${APP_ID}-*.jar server.jar
# 挂载的目录，一般用来存放需要保持的数据
VOLUME ${LOG_PATH}
# 容器暴露的端口，默认暴露端口，并不是映射
#EXPOSE ${PORT}
# 容器启动运行的命令，可以追加 不可覆盖
ENTRYPOINT ["sh","-c", "java -jar server.jar $PARAMS"]
# 容器启动运行的命令，有多个的话，只有最后一个生效 可被覆盖
CMD ["${VM_CMD}", "-Dspring.profiles.active=${BUILD_ENV}", "-Dfile.encoding=UTF-8"]
# 让指令延迟执行，延迟到下一个使用FROM的dockerfile建立image时执行，只延迟一次
#ONBUILD
# 与ADD作用相同，不过ADD会解压文件
#COPY start.sh ${WORKDIR}
#HEALTHCHECK --interval=1s --timeout=3s --retries=150 \
#    CMD sh ${WORKDIR}/start.sh