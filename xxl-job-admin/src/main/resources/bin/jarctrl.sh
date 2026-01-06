#!/bin/bash

# 菜单选项
Option=$1

# jar包文件名
JarName=$2

# jar包相对本脚本的路径
# 脚本将会先cd到此路径作为工作路径
# 有配置则使用
JarPath=

# 查询日志的最后多少行
TAIL_LOG_LINES=1000

# 查询异常的最后多少行
TAIL_EXCEPT_LINES=3000

# 查询异常之后的多少行
TAIL_EXCEPT_BEFORE_LINES=5
TAIL_EXCEPT_AFTER_LINES=30

# ##################################################################################################################
# 常量定义区
# ##################################################################################################################
BOOL_TRUE=1
BOOL_FALSE=0

# ##################################################################################################################
# 常用配置区
# ##################################################################################################################

# Springboot 配置常见配置区
# 是否开启Springboot配置
# 必须是定义的BOOL常量
ENABLE_SPRING_CFG=$BOOL_FALSE
# 应用启动的端口重定义
# 有值/非空则使用
SPRING_SERVER_PORT=8080
# 应用使用的配置文件重定义
# 有值/非空则使用
SPRING_PROFILES_ACTIVE=test
# 应用名称重定义
# 有值/非空则使用
SPRING_APPLICATION_NAME=
# 应用的根日志级别重定义
# 有值/非空则使用
SPRING_LOGGING_LEVEL_ROOT=info

# logback 配置区
# 是否开启Logback配置
# 必须是定义的BOOL常量
ENABLE_LOGBACK_CFG=$BOOL_FALSE
# 指定logback的启动配置文件重定义
# 有值/非空则使用
LOGBACK_CONFIG_FILE=classpath:logback-spring.xml
# 指定logback的应用名称重定义，也就是启动参数 -Dlogback.app.name
# 有值/非空则使用
LOGBACK_APP_NAME=
# 指定logback的应用环境重定义，也就是启动参数 -Dlogback.app.env
# 有值/非空则使用
LOGBACK_APP_ENV=test
# 指定logback的最大日志大小重定义，也就是启动参数 -Dlogback.app.log.max.size
# 有值/非空则使用
LOGBACK_APP_LOG_MAX_SIZE=500MB

# Java 配置区
# 指定已用所使用的的JAVA_HOME
# 有值/非空则使用
APP_JAVA_HOME=

# JVM 常见配置区
# JVM 启动参数定义，此参数不受 ENABLE_JVM_CFG 控制
JVM_OPTS=
# 是否开启Jvm配置
# 必须是定义的BOOL常量
ENABLE_JVM_CFG=$BOOL_FALSE
# 指定jvm的时区
# 有值/非空则使用
USER_TIME_ZONE=Asia/Shanghai
# 指定jvm的最小堆
# 有值/非空则使用
XMS_SIZE=512M
# 指定jvm的最大堆
# 有值/非空则使用
XMX_SIZE=2048M
# 指定线程空间大小
# 有值/非空则使用
XSS_SIZE=1M
# 指定堆外内存
# 有值/非空则使用
PERM_SIZE=256M
# 指定最大堆外内存
# 有值/非空则使用
MAX_PERM_SIZE=2048M
# 指定是否开启OOM是生产dump文件
# 必须是定义的BOOL值
# 有值/非空则使用
DUMP_OOM=$BOOL_TRUE
# 指定是否开启打印GC日志
# 必须是定义的BOOL值
# 有值/非空则使用
PRINT_GC=$BOOL_TRUE
# 指定是否适用ParallelGC
# 必须是定义的BOOL值
# 有值/非空则使用
PARALLEL_GC=$BOOL_TRUE
# 指定新生代的占比
# 有值/非空则使用
NEW_RATIO=1
# 指定幸存取的占比
# 有值/非空则使用
SURVIVOR_RATIO=30
# 指定是否适用G1GC
# 必须是定义的BOOL值
# 有值/非空则使用
G1_GC=$BOOL_FALSE

# jmx 配置，可以使用 visualvm 监控堆栈
# 指定是否开启JMX
# 必须是定义的BOOL值
ENABLE_JMX_CFG=$BOOL_FALSE
# 指定JMX的连接端口
# 有值/非空则使用
JMX_PORT=9440
# 如果出现visualvm连接不上，则这里设置为主机的IP地址
JMX_HOST=


# 是否开启xrebel分析代理
ENABLE_XREBEL=$BOOL_FALSE
# xrebel的agent的jar名称，这里是固定的，不用更改
XREBEL_AGENT_JAR=xrebel.jar
# 需要为完整路径，因为agent的依赖包需要通过完整路径的方式才能找到
XREBEL_AGEN_PATH=/home/xrebel/agent

# 是否开启Skywalking链路追踪
ENABLE_SKYWALKING=$BOOL_FALSE
# skywalking的agent的jar名称，这里是固定的，不用更改
SKYWALKING_AGENT_JAR=skywalking-agent.jar
# skywalking的agent的路径，也就是下面存放SKYWALKING_AGENT_JAR的路径
# 需要为完整路径，因为agent的依赖包需要通过完整路径的方式才能找到
SKYWALKING_AGENT_PATH=/home/skywalking/agent
# 指定skywalking的应用名称，不指定默认使用jar名称
SKYWALKING_SERVICE_NAME=
# 指定skywalking的采集主机
SKYWALKING_BACKENT_SERIVCE_ADDR=127.0.0.1:11800

# ##################################################################################################################
# 内部函数定义区
# ##################################################################################################################


# 定义函数的入参和返回值
# 公共变量，在函数调用时使用
# 自行控制堆栈变量
_func_arg1=
_func_arg2=
_func_arg3=
_func_arg4=
_func_ret=

# 清空函数的入参和返回值
# 用在准备调用函数之前执行
function cleanFuncParams() {
    _func_arg1=
    _func_arg2=
    _func_arg3=
    _func_arg4=
    _func_ret=
}

# 从指定的目录中查找指定后缀的文件
# 入参：后缀，路径
# 返回值：文件名
function findOneSuffixFileInPath() {
  _p_suffix=$_func_arg1
  _p_path=$_func_arg2
  _func_ret=

  for _p_file in $(ls -aS $_p_path | grep -v grep | grep $_p_suffix)
  do
      _p_fix=${_p_file##*.}
      if [ x"$_p_suffix" == x".$_p_fix" ]; then
         _func_ret=$_p_file
         break
      fi
  done
}

# 根据端口号查询进程号，仅针对java进程
# 入参：端口号
# 返回值：PID
function findPidByPort() {
  _p_port=$_func_arg1
  _func_ret=

  _p_pid=`netstat -nlp | grep java | grep -v grep | grep :${_p_port} | awk '{print $7}' | awk -F '[ / ]' '{print $1}'`
  _func_ret=$_p_pid
}

# 根据jar包文件名，查找进程号，仅针对java进程
# 入参：jar文件名
# 返回值：PID
function findPidByJarName() {
  _p_jar_name=$_func_arg1
  _func_ret=

  _p_pid=`ps -ef | grep java | grep -v grep | grep ${_p_jar_name} | awk '{print $2}'`
  _func_ret=$_p_pid
}

# 根据进程号文件、端口号、jar文件名三个条件，综合查找进程号，仅针对java进程
# 入参：存放PID的文件，对应的应用端口，jar文件名
# 返回值：PID
function getPid() {
    _p_pid_file=$_func_arg1
    _p_port=$_func_arg2
    _p_jar_name=$_func_arg3

    _p_pid=

    # 从PID文件查找PID
    if [[ -n "${_p_pid_file}" ]]; then
      _p_pid=$(cat ${_p_pid_file})
      echo -e "\033[0;34m pid file \033[0m  find pid= \033[0;34m $_p_pid \033[0m "
    fi

    # 检查PID是正在运行，在运行则退出
    if [[ -n "${_p_pid}" ]]; then
      cleanFuncParams
      _func_arg1=$_p_pid
      _func_ret=$BOOL_FALSE
      verifyPidIsRunning
      _p_running=$_func_ret
      if [ $_p_running == $BOOL_TRUE ];then
        _func_ret=$_p_pid
        return
      fi
    fi

    _p_pid=

    # 如果启用了Spring配置并且指定了端口
    # 根据端口查找PID
    if [[ -n "${_p_port}" ]]; then
      cleanFuncParams
      _func_arg1=$_p_port
      _func_ret=
      findPidByPort
      _p_pid=$_func_ret
      echo -e "\033[0;34m port \033[0m find pid= \033[0;34m $_p_pid \033[0m"
    fi

    # 检查PID是正在运行，在运行则退出
    if [[ -n "${_p_pid}" ]]; then
      cleanFuncParams
      _func_arg1=$_p_pid
      _func_ret=$BOOL_FALSE
      verifyPidIsRunning
      _p_running=$_func_ret
      if [ $_p_running == $BOOL_TRUE ];then
        _func_ret=$_p_pid
        return
      fi
    fi

    _p_pid=

    # 如果还是没有PID，则直接使用jar包名称查找PID
    # 根据端口查找PID
    if [[ -n "${_p_jar_name}" ]]; then
        cleanFuncParams
        _func_arg1=$_p_jar_name
        _func_ret=
        findPidByJarName
        _p_pid=$_func_ret
        echo -e "\033[0;34m jar file \033[0m find pid= \033[0;34m $_p_pid \033[0m"
    fi

    # 检查PID是正在运行，在运行则退出
    if [[ -n "${_p_pid}" ]]; then
      cleanFuncParams
      _func_arg1=$_p_pid
      _func_ret=$BOOL_FALSE
      verifyPidIsRunning
      _p_running=$_func_ret
      if [ $_p_running == $BOOL_TRUE ];then
        _func_ret=$_p_pid
        return
      fi
    fi

    _func_ret=
}

# 验证指定的进程号是否在运行
# 入参：pid
# 返回值：定义的BOOL值
function verifyPidIsRunning() {
  _p_pid=$_func_arg1
  _func_ret=$BOOL_FALSE

  for _p_item in $(ps -ef | grep -v grep | awk '{print $2}' | xargs echo)
  do
    if [ x"$_p_item" == x"$_p_pid" ]; then
       _func_ret=$BOOL_TRUE
       break
    fi
  done
}

# 打印帮助信息
function help()
{
    echo -e "\033[0;31m please input 1st arg:Option \033[0m"
    echo -e "    options: \033[0;34m {start|u|stop|d|restart|r|status|t|log|l|except|e|backup|b|rollback|o} \033[0m"
    echo -e "\033[0;34m start/u    \033[0m : to run(up/u) a jar which called JarName"
    echo -e "\033[0;34m stop/d     \033[0m : to stop(down/d) a jar which called JarName"
    echo -e "\033[0;34m restart/r  \033[0m : to stop and run(restart/r) a jar which called JarName"
    echo -e "\033[0;34m status/t   \033[0m : to check run status(status/t) for a jar which called JarName"
    echo -e "\033[0;34m log/l      \033[0m : to lookup the log(log/l) for a jar which called JarName"
    echo -e "\033[0;34m except/e   \033[0m : to lookup the exception(exception/e) log for a jar which called JarName"
    echo -e "\033[0;34m backup/b   \033[0m : to backup(backup/b) to jar to backup dir for a jar which called JarName"
    echo -e "\033[0;34m rollback/o \033[0m : to rollback(rollback/o) from backup dir and backup to rollback dir for a jar which called JarName"

    exit 1
}

# ##################################################################################################################
# 脚本定义区
# ##################################################################################################################
# java 定义
JAVA_PATH=java
# 应用名定义
AppName=
# 工作路径定义
WORK_DIR=
# 日志路径定义
LOG_DIR=
# 日志文件定义
LOG_FILE=
# PID文件定义
PID_FILE=
# 启动信息文件定义
BOOT_META_FILE=


# 其他文件路径
META_DIR=
# 备份路径
BACKUP_DIR=
# 回滚路径
ROLLBACK_DIR=

# 准备Spring的启动参数
function prepareSpringCfg(){
    if [[ -n "${SPRING_APPLICATION_NAME}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dspring.application.name=${SPRING_APPLICATION_NAME}"
    fi
    if [[ -n "${SPRING_SERVER_PORT}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dserver.port=${SPRING_SERVER_PORT}"
    fi
    if [[ -n "${SPRING_PROFILES_ACTIVE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"
    fi
    if [[ -n "${SPRING_LOGGING_LEVEL_ROOT}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dlogging.level.root=${SPRING_LOGGING_LEVEL_ROOT}"
    fi
}
# 准备logback的启动参数
function prepareLogbackCfg() {
    if [[ -n "${LOGBACK_CONFIG_FILE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dlogging.config=${LOGBACK_CONFIG_FILE}"
    fi
    if [[ -n "${LOGBACK_APP_NAME}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dlogback.app.name=${LOGBACK_APP_NAME}"
    fi
    if [[ -n "${LOGBACK_APP_ENV}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dlogback.app.env=${LOGBACK_APP_ENV}"
    fi
    if [[ -n "${LOGBACK_APP_LOG_MAX_SIZE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Dlogback.app.log.max.size=${LOGBACK_APP_LOG_MAX_SIZE}"
    fi
}
# 准备jvm的启动参数
function prepareJvmCfg() {
    if [[ -n "${USER_TIME_ZONE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Duser.timezone=${USER_TIME_ZONE}"
    fi
    if [[ -n "${XMS_SIZE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Xms${XMS_SIZE}"
    fi
    if [[ -n "${XMX_SIZE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Xmx${XMX_SIZE}"
    fi
    if [[ -n "${XSS_SIZE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -Xss${XSS_SIZE}"
    fi
    if [[ -n "${PERM_SIZE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -XX:PermSize=${PERM_SIZE}"
    fi
    if [[ -n "${MAX_PERM_SIZE}" ]]; then
      JVM_OPTS="${JVM_OPTS} -XX:MaxPermSize=${MAX_PERM_SIZE}"
    fi
    if [ $DUMP_OOM == $BOOL_TRUE ];then
      JVM_OPTS="${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
    fi
    if [ $PRINT_GC == $BOOL_TRUE ];then
      JVM_OPTS="${JVM_OPTS} -XX:+PrintGCDateStamps  -XX:+PrintGCDetails"
    fi
    if [ $PARALLEL_GC == $BOOL_TRUE ];then
      JVM_OPTS="${JVM_OPTS} -XX:+UseParallelGC -XX:+UseParallelOldGC"
    fi
    if [[ -n "${NEW_RATIO}" ]]; then
      JVM_OPTS="${JVM_OPTS} -XX:NewRatio=${NEW_RATIO}"
    fi
    if [[ -n "${SURVIVOR_RATIO}" ]]; then
      JVM_OPTS="$JVM_OPTS -XX:SurvivorRatio=${SURVIVOR_RATIO}"
    fi
    if [ $G1_GC == $BOOL_TRUE ];then
      JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"
    fi
}
# 准备jmx的启动参数
function prepareJmxCfg(){
  if [[ -n "${JMX_PORT}" ]]; then
    JVM_OPTS="${JVM_OPTS} -Dcom.sun.management.jmxremote"
    JVM_OPTS="${JVM_OPTS} -Dcom.sun.management.jmxremote.port=${JMX_PORT}"
    JVM_OPTS="${JVM_OPTS} -Dcom.sun.management.jmxremote.authenticate=false"
    JVM_OPTS="${JVM_OPTS} -Dcom.sun.management.jmxremote.ssl=false"
    JVM_OPTS="${JVM_OPTS} -Djava.net.preferIPv4Stack=true"
  fi
  if [[ -n "${JMX_HOST}" ]]; then
    JVM_OPTS="${JVM_OPTS} -Djava.rmi.server.hostname=${JMX_HOST}"
  fi
}

# 准备xrebel的启动参数
# -javaagent:/home/xrebel/agent/xrebel.jar
function prepareXrebelCfg(){
  if [ ! -d "${XREBEL_AGENT_PATH}" ];then
    echo xrebel config fail, cause by xrebel agent path ${XREBEL_AGENT_PATH} not exists.
    return
  fi

  _p_xrebel_agent_jar_full_path=$XREBEL_AGENT_PATH/$XREBEL_AGENT_JAR
  if [ ! -f "${_p_xrebel_agent_jar_full_path}" ];then
    echo xrebel config fail, cause by xrebel agent jar ${_p_xrebel_agent_jar_full_path} not exists.
    return
  fi

  _p_xrebel_args="-javaagent:${_p_xrebel_agent_jar_full_path}"

  JVM_OPTS="${JVM_OPTS} ${_p_xrebel_args}"
}

# 准备skywalking的启动参数
# -javaagent:/home/skywalking/agent/skywalking-agent.jar=agent.service_name=appname,collector.backend_service=127.0.0.1:11800
function prepareSkywalkingCfg(){
  if [ ! -d "${SKYWALKING_AGENT_PATH}" ];then
    echo skywalking config fail, cause by skywalking agent path ${SKYWALKING_AGENT_PATH} not exists.
    return
  fi

  _p_skywalking_agent_jar_full_path=$SKYWALKING_AGENT_PATH/$SKYWALKING_AGENT_JAR
  if [ ! -f "${_p_skywalking_agent_jar_full_path}" ];then
    echo skywalking config fail, cause by skywalking agent jar ${_p_skywalking_agent_jar_full_path} not exists.
    return
  fi

  _p_skywalking_service_name=$SKYWALKING_SERVICE_NAME

  if [ "$_p_skywalking_service_name" = "" ];then
    _p_skywalking_service_name=$AppName
  fi

  _p_skywalking_args="-javaagent:${_p_skywalking_agent_jar_full_path}=agent.service_name=${_p_skywalking_service_name}"

  if [[ -n "${SKYWALKING_BACKENT_SERIVCE_ADDR}" ]]; then
    _p_skywalking_args="${_p_skywalking_args},collector.backend_service=${SKYWALKING_BACKENT_SERIVCE_ADDR}"
  fi

  JVM_OPTS="${JVM_OPTS} ${_p_skywalking_args}"
}

# 准备JVM的所有启动参数
function prepareJvmOpts() {
    JVM_OPTS="${JVM_OPTS} -Djar.name=$JarName"

    if [ $ENABLE_SPRING_CFG == $BOOL_TRUE ];then
      prepareSpringCfg
    fi

    if [ $ENABLE_LOGBACK_CFG == $BOOL_TRUE ];then
      prepareLogbackCfg
    fi

    if [ $ENABLE_JVM_CFG == $BOOL_TRUE ];then
      prepareJvmCfg
    fi

    if [ $ENABLE_JMX_CFG == $BOOL_TRUE ];then
       prepareJmxCfg
    fi

    if [ $ENABLE_SKYWALKING == $BOOL_TRUE ];then
       prepareSkywalkingCfg
    fi

    if [ $ENABLE_XREBEL == $BOOL_TRUE ];then
      prepareXrebelCfg
    fi
}

# 启动jar
function start() {
  mkdir -p $META_DIR

  if [ ! -d ${PID_FILE} ]; then
    echo "not pid file,create..."
    touch ${PID_FILE}
  fi

  cleanFuncParams
  _func_arg1=$PID_FILE
  _func_arg2=
  _func_arg3=$JarName
  if [ $ENABLE_SPRING_CFG == $BOOL_TRUE ];then
    _func_arg2=$SPRING_SERVER_PORT
  fi
  getPid
  _p_pid=$_func_ret

  if [[ -n "$_p_pid" ]]; then
      echo -e "\033[0;31m process has running ... \033[0m"
      return
  fi

  prepareJvmOpts
  _p_now=$(date "+%Y-%m-%d %H:%M:%S")

  echo -e "\033[0;34m JAVA_PATH  \033[0m : $JAVA_PATH"
  echo -e "\033[0;34m JVM_OPTS   \033[0m : $JVM_OPTS"
  echo -e "\033[0;34m START_TIME \033[0m : $_p_now"

  echo "" > $PID_FILE

  mkdir -p ${LOG_DIR}

  if [ $ENABLE_LOGBACK_CFG == $BOOL_TRUE ];then
    nohup $JAVA_PATH -jar  $JVM_OPTS $JarName > /dev/null 2>&1 & echo $! > $PID_FILE
    echo -e "\033[0;34m logback \033[0m start ..."
  else
    nohup $JAVA_PATH -jar  $JVM_OPTS $JarName > $LOG_FILE 2>&1 & echo $! > $PID_FILE
    echo -e "\033[0;34m sysout \033[0m start ..."
  fi

  echo "JAVA_PATH  : $JAVA_PATH" > $BOOT_META_FILE
  echo "JVM_OPTS   : $JVM_OPTS" >> $BOOT_META_FILE
  echo "START_TIME : $_p_now" >> $BOOT_META_FILE

  chmod a+r $LOG_DIR/*.log
  chmod a+r $META_DIR/*.txt

  _p_pid=`cat $PID_FILE`
  echo -e "start \033[0;34m $JarName \033[0m success on pid \033[0;34m $_p_pid \033[0m ..."
}

# 停止jar
function stop() {

    cleanFuncParams
    _func_arg1=$PID_FILE
    _func_arg2=
    _func_arg3=$JarName
    if [ $ENABLE_SPRING_CFG == $BOOL_TRUE ];then
      _func_arg2=$SPRING_SERVER_PORT
    fi
    getPid
    _p_pid=$_func_ret

    if [[ -n "$_p_pid" ]]; then
        echo -e "kill pid is: \033[0;34m $_p_pid \033[0m"
        kill -9 $_p_pid
        echo "" > $PID_FILE
    else
      echo -e "\033[0;31m not pid found, app already stopped. \033[0m"
    fi
}

# 重启jar
function restart() {
    stop
    sleep 2
    start
}

# 查看应用状态
function status() {
    cleanFuncParams
    _func_arg1=$PID_FILE
    _func_arg2=
    _func_arg3=$JarName
    if [ $ENABLE_SPRING_CFG == $BOOL_TRUE ];then
      _func_arg2=$SPRING_SERVER_PORT
    fi
    getPid
    _p_pid=$_func_ret

    if [[ -n "$_p_pid" ]]; then
        echo -e "app is \033[0;34m running \033[0m on pid: \033[0;34m $_p_pid \033[0m"
    else
      echo -e "app was \033[0;31m stopped \033[0m ."
    fi
}

function findLogFile() {
      _func_ret=

      _p_log_file=`ls -t ${LOG_DIR} | grep .log | grep -v grep | grep ${AppName} | head -n 1`
      if [[ "${_p_log_file}" = "" ]]; then
        echo -e "\033[0;31m not found ${AppName}*.log , try find most newest log file... \033[0m"
        _p_log_file=`ls -t ${LOG_DIR} | grep .log | grep -v grep | head -n 1`
      fi

      _func_ret=$_p_log_file
}

# 查看应用日志
function log() {
    cleanFuncParams
    findLogFile
    _p_log_file=$_func_ret

    if [[ -n "$_p_log_file" ]]; then
        echo -e "\033[0;34m found log file ${LOG_DIR}/$_p_log_file \033[0m"
        tail -f -n $TAIL_LOG_LINES ${LOG_DIR}/$_p_log_file
    else
      echo -e "\033[0;31m not found log file like ${AppName}*.log. \033[0m"
    fi
}

# 查看应用的异常日志
function except() {
    cleanFuncParams
    findLogFile
    _p_log_file=$_func_ret

    if [[ -n "$_p_log_file" ]]; then
        echo -e "\033[0;34m found log file ${LOG_DIR}/$_p_log_file \033[0m"
        tail -f -n $TAIL_EXCEPT_LINES ${LOG_DIR}/$_p_log_file | grep -in -B $TAIL_EXCEPT_BEFORE_LINES -A $TAIL_EXCEPT_AFTER_LINES exception
    else
      echo -e "\033[0;31m not found log file like ${AppName}*.log. \033[0m"
    fi
}

# 备份应用
function backup() {
    _p_now=$(date "+%Y%m%d%H%M%S")
    _p_backup_now=$BACKUP_DIR/$_p_now
    mkdir -p $_p_backup_now
    cp ${JarName} ${_p_backup_now}/${JarName}
    echo "$_p_backup_now/${JarName}" > $BACKUP_DIR/_last
    echo -e "\033[0;34m $JarName \033[0m has backup to \033[0;34m $_p_backup_now/$JarName \033[0m"
}

# 回滚应用
function rollback() {
    mkdir -p $ROLLBACK_DIR
    mv ${JarName} ${ROLLBACK_DIR}/${JarName}

    _p_backup_path=`cat $BACKUP_DIR/_last`
    if [[ -n "$_p_backup_path" ]]; then
      echo -e "\033[0;34m $JarName \033[0m has rollback from \033[0;34m $_p_backup_path \033[0m"
      cp $_p_backup_path ./${JarName}
    else
      echo -e "\033[0;31m not found file $BACKUP_DIR/_last \033[0m"
    fi
}

# ##################################################################################################################
# 脚本正式处理逻辑
# ##################################################################################################################

# 准备运行Jar包的上下文
function prepareAppContext(){
      # 如果指定了APP_JAVA_HOME,则使用APP_JAVA_HOME下面的JAVA
      if [[ "$APP_JAVA_HOME" -ne "" ]]; then
        JAVA_PATH=${APP_JAVA_HOME}/bin/java
      fi

      # 如果定义了jar的路径，则先进入路径
      if [[ -n "${JarPath}" ]]; then
        cd $JarPath
      fi

      # 从当前文件夹查找jar文件
      if [ "$JarName" == "" ];
      then
        cleanFuncParams
        _func_arg1=.jar
        _func_arg2=
        findOneSuffixFileInPath
        JarName=$_func_ret
      fi

      # 如果依旧没有jar文件，则失败退出
      if [ "$JarName" = "" ];
      then
          echo -e "\033[0;31m please input 2nd arg:jarName \033[0m"
          exit 1
      fi

      # 根据JarName截取AppName
      #AppName=`basename $JarName .jar`
      AppName=${JarName%.*}

      # 处理Logback的AppName
      # 如果配置了Spring配置，
      # 且有配置SpringApplicationName，
      # 则使用此名称作为Logback的AppName
      # 否则使用jar包的AppName
      if [[ "${LOGBACK_APP_NAME}" = "" ]]; then
        if [ $ENABLE_SPRING_CFG == $BOOL_TRUE ];then
          if [[ -n "${SPRING_APPLICATION_NAME}" ]]; then
            LOGBACK_APP_NAME=$SPRING_APPLICATION_NAME
          fi
        fi
      fi

      if [[ "${LOGBACK_APP_NAME}" = "" ]]; then
        LOGBACK_APP_NAME=$AppName
      fi

      # 初始化工作路径以及相关的路径
      WORK_DIR=`pwd`
      LOG_DIR=${WORK_DIR}/logs
      LOG_FILE=${LOG_DIR}/${AppName}.log
      META_DIR=${WORK_DIR}/metas

      BOOT_META_FILE=${META_DIR}/meta.${AppName}.txt
      PID_FILE=${META_DIR}/pid.${AppName}.txt
      BACKUP_DIR=${META_DIR}/backup
      ROLLBACK_DIR=${META_DIR}/rollback

      cleanFuncParams
      findLogFile
      _p_log_file=$_func_ret

      if [[ -n "$_p_log_file" ]]; then
        LOG_FILE=${LOG_DIR}/$_p_log_file
      fi
}

# 打印运行Jar包的上下文
function printAppContext(){
      # 打印基本配置信息
      echo "-----------------------"
      echo -e "\033[0;34m AppName     \033[0m : $AppName"
      echo -e "\033[0;34m JarName     \033[0m : $JarName"
      echo -e "\033[0;34m Option      \033[0m : $Option"
      echo -e "\033[0;34m WorkDir     \033[0m : $WORK_DIR"
      echo -e "\033[0;34m LogDir      \033[0m : $LOG_DIR"
      echo -e "\033[0;34m LogFile     \033[0m : $LOG_FILE"
      echo -e "\033[0;34m PidFile     \033[0m : $PID_FILE"
      echo -e "\033[0;34m BootFile    \033[0m : $BOOT_META_FILE"
      echo -e "\033[0;34m BackupDir   \033[0m : $BACKUP_DIR"
      echo -e "\033[0;34m RollbackDir \033[0m : $ROLLBACK_DIR"
      echo "-----------------------"
}



# ##################################################################################################################
# 函数分配区
# ##################################################################################################################

function mainApp(){
  # 如果没有指定选项，给出帮助并退出
  if [ "$Option" = "" ];
  then
      help
      exit 1
  fi

  prepareAppContext
  printAppContext

  case $Option in
    start)
    start;;
    u)
    start;;
    stop)
    stop;;
    d)
    stop;;
    restart)
    restart;;
    r)
    restart;;
    status)
    status;;
    t)
    status;;
    log)
    log;;
    l)
    log;;
    except)
    except;;
    e)
    except;;
    backup)
    backup;;
    b)
    backup;;
    rollback)
    rollback;;
    o)
    rollback;;
    *)
    help;;
  esac
}

mainApp
