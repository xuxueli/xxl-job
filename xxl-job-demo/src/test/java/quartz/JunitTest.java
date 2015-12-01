package quartz;


import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xxl.quartz.DynamicSchedulerUtil;
import com.xxl.quartz.JobModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationcontext-*.xml")
public class JunitTest {
	
    @Test
    public void addJob() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
    	boolean ret = DynamicSchedulerUtil.addJob(new JobModel("Jost-job", "0/1 * * * * ?", TestDynamicJob.class));
    	System.out.println(ret);
    	TimeUnit.SECONDS.sleep(30);
    }
    
    
    
    
}
