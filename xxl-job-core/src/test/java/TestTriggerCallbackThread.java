import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.thread.TriggerCallbackThread;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: yusy
 * @Date: 2021/12/23
 */
public class TestTriggerCallbackThread {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InterruptedException {
        t1();
    }


    public static void t1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InterruptedException {
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setAdminAddresses("http://192.168.0.248:8080/xxl-job-admin");

        Method initAdminBizList = xxlJobExecutor.getClass().getDeclaredMethod("initAdminBizList", String.class, String.class);
        initAdminBizList.setAccessible(true);


        initAdminBizList.invoke(xxlJobExecutor, "http://192.168.0.248:8080/xxl-job-admin", "a");





        TriggerCallbackThread instance = TriggerCallbackThread.getInstance();

        for (Field field : TriggerCallbackThread.class.getDeclaredFields()) {
            if (field.getName().equals("failCallbackFilePath")) {
                field.setAccessible(true);
                field.set(null, "d:/tmp/callback/");
            } else if (field.getName().equals("failCallbackFileName")) {
                field.setAccessible(true);
                field.set(null, "d:/tmp/callback/".concat("xxl-job-callback-{x}").concat(".log"));
            }
        }

        Method appendFailCallbackFile = instance.getClass().getDeclaredMethod("appendFailCallbackFile", List.class);
        appendFailCallbackFile.setAccessible(true);

        HandleCallbackParam h = new HandleCallbackParam();
        h.setLogId(1L);
        h.setHandleCode(200);
        h.setHandleMsg("sssss");
        h.setLogDateTim(System.currentTimeMillis());


        appendFailCallbackFile.invoke(instance, Collections.singletonList(h));


        instance.start();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
