package com.xxl.job.core.handler.impl;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlParam;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ConcurrentLruCache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xuxueli 2019-12-11 21:12:18
 */
public class MethodJobHandler extends IJobHandler {
    public static ParameterNameDiscoverer nameDiscoverer=new LocalVariableTableParameterNameDiscoverer();
    public static ConcurrentLruCache<Method,String[]> cacheMethodParamNames=new ConcurrentLruCache<>(1024,(method)->{
        Parameter[] parameters = method.getParameters();
        String[] paramNames=new String[parameters.length];
        String[] names = nameDiscoverer.getParameterNames(method);
        if(names!=null){
            for (int i = 0; i < paramNames.length; i++) {
                if(names[i]!=null &&!names[i].isEmpty()) {
                    paramNames[i] = names[i];
                }
            }
        }
        for (int i = 0; i < parameters.length; i++) {
            try{
                XxlParam ann = parameters[i].getDeclaredAnnotation(XxlParam.class);
                if(ann!=null){
                    String value = ann.value();
                    if(value!=null && !value.isEmpty()){
                        paramNames[i]=value;
                        continue;
                    }
                }
            }catch(Exception e){

            }
            if(paramNames[i]==null) {
                paramNames[i] = parameters[i].getName();
            }
        }
        return paramNames;
    });
    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;

        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void execute() throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 0) {
           Object[] args=injectArgs(method);
            method.invoke(target, args);       // method-param can not be primitive-types
        } else {
            method.invoke(target);
        }
    }


    public Object[] injectArgs(Method method){
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        if(args.length==0){
            return args;
        }

        String[] paramNames= cacheMethodParamNames.get(method);

        XxlJobContext jobContext = XxlJobContext.getXxlJobContext();
        String jobParam = jobContext.getJobParam();
        Map<String,String> paramValueMap=null;

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String paramName = paramNames[i];
            Class<?> type = parameter.getType();
            // inject context
            if(XxlJobContext.class.isAssignableFrom(type)){
                args[i]=jobContext;
                continue;
            }
            // inject context field value
            try{
                Field field = XxlJobContext.class.getDeclaredField(paramName);
                field.setAccessible(true);
                args[i]=field.get(jobContext);
                continue;
            }catch(Exception e){

            }
            // parse jobParam as properties map
            if(paramValueMap==null){
                paramValueMap=new LinkedHashMap<>();
                if(jobParam!=null){
                    String[] lines = jobParam.split("\n");
                    for (String line : lines) {
                        if(line.isEmpty()){
                            continue;
                        }
                        String[] arr = line.split("=", 2);
                        String key = arr[0].trim();
                        if(key.isEmpty()){
                            continue;
                        }
                        if(arr.length>1){
                            paramValueMap.put(key,arr[1]);
                        }else{
                            paramValueMap.put(key,null);
                        }
                    }
                }
            }

            // inject param value field
            if(paramValueMap!=null){
                String str = paramValueMap.get(paramName);
                if(str!=null){
                    try{
                        if(String.class.isAssignableFrom(type)){
                            args[i]=str;
                        }else if(Integer.class.isAssignableFrom(type) || int.class.equals(type)){
                            args[i]=Integer.valueOf(((String)str).trim());
                        }else if(Boolean.class.isAssignableFrom(type) || boolean.class.equals(type)){
                            args[i]=Boolean.valueOf(((String)str).trim());
                        }else if(Long.class.isAssignableFrom(type) || long.class.equals(type)){
                            args[i]=Long.valueOf(((String)str).trim());
                        }else if(Double.class.isAssignableFrom(type) || double.class.equals(type)){
                            args[i]=Double.valueOf(((String)str).trim());
                        }else if(Float.class.isAssignableFrom(type) || float.class.equals(type)){
                            args[i]=Float.valueOf(((String)str).trim());
                        }
                    }catch(Exception e){

                    }
                }
            }

        }

        for (int i = 0; i < parameters.length; i++) {
            if(args[i]==null){
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                if(String.class.isAssignableFrom(type)){
                    args[i]=jobParam;
                }else if(Map.class.isAssignableFrom(type)){
                    args[i]=paramValueMap;
                }
            }
        }

        return args;
    }

    @Override
    public void init() throws Exception {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws Exception {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}
