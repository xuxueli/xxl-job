package com.xxl.job.core.annotationtask.spring;

import com.xxl.job.core.annotationtask.annotations.DestroyJob;
import com.xxl.job.core.annotationtask.annotations.Xxl;
import com.xxl.job.core.annotationtask.annotations.XxlJob;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class XxlScanner extends ClassPathBeanDefinitionScanner {

    public XxlScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        try {
            Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
            if(beanDefinitionHolders.isEmpty()){
                logger.warn("No xxl was found in '"+ Arrays.toString(basePackages)+"' package. Please check your configuration.");
            }else{
                for (BeanDefinitionHolder holder: beanDefinitionHolders){
                    GenericBeanDefinition beanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
                    String name = beanDefinition.getBeanClassName();
                    Class<?> clzz = Class.forName(name);
                    initJobDetails(clzz);
                    beanDefinition.setBeanClass(XxlJobFactoryBean.class);
                    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,clzz);
                }
            }
            return beanDefinitionHolders;
        }catch (Exception e){

        }
        return null;
    }

    @Override
    protected void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(Xxl.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition definition) {
        return definition.getMetadata().isInterface()&&definition.getMetadata().isIndependent();
    }

    private void initJobDetails(Class xxl){
        Method[] methods = xxl.getDeclaredMethods();
        for (Method method:methods){
            if(method.isAnnotationPresent(XxlJob.class)&&!method.isAnnotationPresent(DestroyJob.class)){
                XxlJob xxlJob = method.getAnnotation(XxlJob.class);
                String identity =  method.getDeclaringClass().getCanonicalName()+"."+method.getName();
                XxlJobExecutor.getXXLJOBS().add(jobDetail(xxlJob,identity));
            }
        }
    }

    /**
     * xxlJob to jobDetail
     * @param xxlJob
     * @param identity
     * @return
     */
    private XxlJobInfo jobDetail(XxlJob xxlJob, String identity){
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        //xxlJobInfo.setJobGroup(appName);
        xxlJobInfo.setAlarmEmail(xxlJob.alarmEmail());
        xxlJobInfo.setGlueType(GlueTypeEnum.BEAN.name());
        xxlJobInfo.setAnnotationIdentity(identity);
        xxlJobInfo.setAuthor(xxlJob.author());
        xxlJobInfo.setExecutorBlockStrategy(xxlJob.executorBlockStrategy().name());
        xxlJobInfo.setExecutorFailStrategy(xxlJob.executorFailStrategy().name());
        JobHandler jobHandler = xxlJob.executorHandler().getAnnotation(JobHandler.class);
        String executorHandler;
        if(jobHandler!=null){
            executorHandler = jobHandler.value();
        }else{
            executorHandler = "";
        }
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(xxlJob.executorParam());
        xxlJobInfo.setExecutorRouteStrategy(xxlJob.executorRouteStrategy().name());
        xxlJobInfo.setJobCron(xxlJob.jobCron());
        xxlJobInfo.setJobDesc(xxlJob.jobDesc());
        xxlJobInfo.setOnStart(xxlJob.onStart());
        return xxlJobInfo;
    }



}
