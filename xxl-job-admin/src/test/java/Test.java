import java.util.Date;
import java.util.Timer;

import org.apache.commons.lang.time.FastDateFormat;


public class Test {
	
	static class DemoTimeTask extends java.util.TimerTask {
		public void run() {
			System.out.println("run:" + FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
	} 
	
	// ??? 某一个时间段内，重复执行
	
	// runTime:第一次执行时间
	// delay： 延迟执行的毫秒数，即在delay毫秒之后第一次执行
	// period：重复执行的时间间隔
	public static Timer mainTimer;
	public static void main(String[] args) {
		// 调度器
		mainTimer = new Timer();
		// Demo任务
		DemoTimeTask timeTask = new DemoTimeTask();
		System.out.println("now:" + FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
		
		// 1、在特定时间执行任务，只执行一次
		//Date runTime = DateUtils.addSeconds(new Date(), 1);
		//mainTimer.schedule(timeTask, runTime);				// runTime
		
		// 2、在特定时间之后执行任务，只执行一次
		//long delay = 1000;
		//mainTimer.schedule(timeTask, delay);					// delay/ms
		
		// 3、指定第一次执行的时间，然后按照间隔时间，重复执行
		//Date firstTime = DateUtils.addSeconds(new Date(), 1);
		//long period = 1000;
		//mainTimer.schedule(timeTask, firstTime, period);		// "period/ms" after "firstTime"
		
		// 4、在特定延迟之后第一次执行，然后按照间隔时间，重复执行
		//long delay = 1000;
		//long period = 1000;
		//mainTimer.schedule(timeTask, delay, period);			// "period/ms" after "delay/ms"
		
		// 5、第一次执行之后，特定频率执行，与3同
		//Date firstTime = DateUtils.addSeconds(new Date(), 1);
		//long period = 1000;
		//mainTimer.scheduleAtFixedRate(timeTask, firstTime, period);
		
		// 6、在delay毫秒之后第一次执行，后按照特定频率执行
		long delay = 1000;
		long period = 1000;
		mainTimer.scheduleAtFixedRate(timeTask, delay, period);
		/**
		 * <1>schedule()方法更注重保持间隔时间的稳定：保障每隔period时间可调用一次
		 * <2>scheduleAtFixedRate()方法更注重保持执行频率的稳定：保障多次调用的频率趋近于period时间，如果任务执行时间大于period，会在任务执行之后马上执行下一次任务
		 */

		// Timer注销
		mainTimer.cancel();
		
	}
	 
}
