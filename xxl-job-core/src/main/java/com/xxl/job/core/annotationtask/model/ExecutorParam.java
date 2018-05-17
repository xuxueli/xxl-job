package com.xxl.job.core.annotationtask.model;

import com.xxl.job.core.annotationtask.enums.ExecutorType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExecutorParam implements Serializable{

    private ExecutorType executorType;
    private Map<String,Object> param;
    public static final String DB_EXECUTOR_PARAM ="executorParam";

    public static final String ANNOTATION_IDENTITY = "annotationIdentity";

    public ExecutorType getExecutorType() {
        return executorType;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    private  ExecutorParam() {
    }

    private ExecutorParam(ExecutorType executorType, Map<String, Object> param) {
        this.executorType = executorType;
        this.param = param;
    }

    public static ExecutorParamBuilder builder = new ExecutorParamBuilder();

    private static class ExecutorParamBuilder{
        private ExecutorType executorType;
        private Map<String,Object> map;

        public ExecutorParamBuilder executorType(ExecutorType executorType){
            this.executorType = executorType;
            return this;
        }


        public ExecutorParamBuilder map(Map<String,Object> params){
            if(map==null){
                map=new HashMap<>();
            }
            map.putAll(params);
            return this;
        }

        public ExecutorParamBuilder put(String key,Object value){
            if(map==null){
                map = new HashMap<>();
            }
            map.put(key,value);
            return this;
        }


        public ExecutorParam build(){
           ExecutorParam executorParam = new ExecutorParam();
           executorParam.executorType = this.executorType;
           if(executorParam.executorType==null){
               throw new RuntimeException("please pick a executor type");
           }
           executorParam.param = this.map;
            if(executorParam.param == null){
                executorParam.param = new HashMap<>();
            }
           return executorParam;
        }
    }

}
