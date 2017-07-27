package com.xxl.job.admin.core.jobbean;

import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * http job bean
 * “@DisallowConcurrentExecution” disable concurrent, thread size can not be only one, better given more
 *
 * @author xuxueli 2015-12-17 18:20:34
 */
//@DisallowConcurrentExecution
@EqualsAndHashCode(callSuper = true)
@Component
@Data
@NoArgsConstructor
public class RemoteHttpJobBean extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);

    @Autowired
    private XxlJobTrigger xxlJobTrigger;

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        // load jobId
        JobKey jobKey = context.getTrigger().getJobKey();
        Integer jobId = Integer.valueOf(jobKey.getName());

        xxlJobTrigger.trigger(jobId);
    }

}