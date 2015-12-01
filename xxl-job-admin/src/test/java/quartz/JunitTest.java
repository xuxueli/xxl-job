package quartz;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xxl.quartz.DynamicSchedulerUtil;
import com.xxl.service.job.TestDynamicJob;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationcontext-*.xml")
public class JunitTest {
	
	@Test
    public void getJobKeys() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
		List<Map<String, Object>> list = DynamicSchedulerUtil.getJobList();
    	System.out.println(list);
    	TimeUnit.SECONDS.sleep(30);
    }
	
    @Test
    public void addJob() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
    	boolean ret = DynamicSchedulerUtil.addJob("demo-job02", "0/2 * * * * ?", TestDynamicJob.class, null);
    	System.out.println(ret);
    	TimeUnit.SECONDS.sleep(30);
    }
    
    @Test
    public void removeJob() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
    	boolean ret = DynamicSchedulerUtil.removeJob("demo-job02");
    	System.out.println(ret);
    	TimeUnit.SECONDS.sleep(30);
    }
    
    @Test
    public void rescheduleJob() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
    	boolean ret = DynamicSchedulerUtil.rescheduleJob("demo-job02", "0/3 * * * * ?");
    	System.out.println(ret);
    	TimeUnit.SECONDS.sleep(30);
    }
    
    @Test
    public void pauseJob() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
    	boolean ret = DynamicSchedulerUtil.pauseJob("demo-job02");
    	System.out.println(ret);
    	TimeUnit.SECONDS.sleep(30);
    }
    
    @Test
    public void resumeTrigger() throws SchedulerException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
    	boolean ret = DynamicSchedulerUtil.resumeTrigger("demo-job02");
    	System.out.println(ret);
    	TimeUnit.SECONDS.sleep(30);
    }
    
}
