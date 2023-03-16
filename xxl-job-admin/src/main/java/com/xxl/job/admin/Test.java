// package com.xxl.job.admin;
//
// import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
// import com.xxl.job.core.handler.annotation.XxlJob;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import java.util.Map;
//
// /**
//  * @Author ximplez
//  * @Description
//  * @Date 2023/03/16 17:37
//  **/
// @Configuration
// public class Test {
//     private static Logger logger = LoggerFactory.getLogger(Test.class);
//
//     @XxlJob("test")
//     public void test(TestParams params) {
//         logger.info("#test# params={}", params);
//     }
//
//     public static class TestParams {
//         int num;
//         String str;
//         Map<String, Object> map;
//
//         public int getNum() {
//             return num;
//         }
//
//         public void setNum(int num) {
//             this.num = num;
//         }
//
//         public String getStr() {
//             return str;
//         }
//
//         public void setStr(String str) {
//             this.str = str;
//         }
//
//         public Map<String, Object> getMap() {
//             return map;
//         }
//
//         public void setMap(Map<String, Object> map) {
//             this.map = map;
//         }
//     }
//
//     @Bean
//     public XxlJobSpringExecutor xxlJobExecutor() {
//         logger.info(">>>>>>>>>>> xxl-job config init.");
//         XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//         xxlJobSpringExecutor.setAdminAddresses("http://127.0.0.1:8080/xxl-job-admin");
//         xxlJobSpringExecutor.setAppname("local-test");
//         xxlJobSpringExecutor.setAddress(null);
//         xxlJobSpringExecutor.setIp(null);
//         xxlJobSpringExecutor.setPort(0);
//         xxlJobSpringExecutor.setAccessToken("default_token");
//         xxlJobSpringExecutor.setLogPath("./log/xxl-job/jobhandler");
//         xxlJobSpringExecutor.setLogRetentionDays(1);
//
//         return xxlJobSpringExecutor;
//     }
//
// }
