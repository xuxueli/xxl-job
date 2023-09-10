# xxl-job执行器

## 1.参数传递方式
### 1.1 通过JobHelper.getJobParam()

```java
import com.xxl.job.executor.annotation.XxlJobJob;
import com.xxl.job.executor.context.JobHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobTest {

    @XxlJob(value = "JobTest")
    public void jobTest() {
        String jobParam = JobHelper.getJobParam();
        log.info("---------xxlJobTest定时任务执行成功-------- {}", jobParam);
    }
}
```
## 1.2 通过方法参数传递

```java
import com.xxl.job.executor.annotation.XxlJobJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobTest {

    @XxlJob(value = "JobTest")
    public void jobTest(JobInfoDTO data) {
        log.info("---------xxlJobTest定时任务执行成功 参数传递-------- {}", data);
    }
}
```








