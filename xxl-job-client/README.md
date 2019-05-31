# introduction
offer a client to operate the schedule of jobs on the server

# quickstart
1. add a dependency
    ```xml
            <dependency>
                <groupId>com.xuxueli</groupId>
                <artifactId>xxl-job-client</artifactId>
            </dependency>
    ```
2. add properties
    ```text
    xxl.job.serverAddresses=http://localhost:7005/xxl-job-admin
    xxl.job.clientAppName=xxl-job-executor-sample
    ```
3. invoke the client apis
    ```java
    @RestController
    public class JobOpsDemoController {
        private final XxlJobClient xxlJobClient;
    
    
        public JobOpsDemoController(XxlJobClient xxlJobClient) {
            this.xxlJobClient = xxlJobClient;
        }
    
        @RequestMapping("/trigger")
        public ReturnT<String> trigger() throws IOException {
            return xxlJobClient.trigger("auto_created_job", "");
        }
    
    }
    ```

# sample
the sample is at the sub-project `xxl-job-executor-sample-springboot`