package com.xxl.job.core.rpc.serialize;


import com.xxl.job.core.biz.model.XxlJobInfo;
import org.junit.Before;
import org.junit.Test;

public class JsonSerializerTest {
    XxlJobInfo xxlJobInfo;
    String xxlString ;
    @Before
    public void before(){
      xxlJobInfo= new XxlJobInfo();
      xxlJobInfo.setAuthor("demo");
      xxlString = "{\n" +
              "  \"id\" : 0,\n" +
              "  \"jobGroup\" : 0,\n" +
              "  \"jobCron\" : null,\n" +
              "  \"jobDesc\" : null,\n" +
              "  \"addTime\" : null,\n" +
              "  \"updateTime\" : null,\n" +
              "  \"author\" : \"demo\",\n" +
              "  \"alarmEmail\" : null,\n" +
              "  \"executorRouteStrategy\" : null,\n" +
              "  \"executorHandler\" : null,\n" +
              "  \"executorParam\" : null,\n" +
              "  \"executorBlockStrategy\" : null,\n" +
              "  \"executorFailStrategy\" : null,\n" +
              "  \"glueType\" : null,\n" +
              "  \"glueSource\" : null,\n" +
              "  \"glueRemark\" : null,\n" +
              "  \"glueUpdatetime\" : null,\n" +
              "  \"childJobId\" : null,\n" +
              "  \"jobStatus\" : null,\n" +
              "  \"annotationIdentity\" : null,\n" +
              "  \"onStart\" : false\n" +
              "}\n";
    }
   @Test
    public void serialize() throws Exception {
       String str = JsonSerializer.toString(xxlJobInfo);
       System.out.println(str);
    }

    @Test
    public void deserialize() throws Exception {
        XxlJobInfo jobInfo =JsonSerializer.deserialize(xxlString.getBytes(),XxlJobInfo.class);
        System.out.println(jobInfo.getAuthor());
    }

}
