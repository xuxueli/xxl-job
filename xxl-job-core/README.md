# xxl-job-core

## 自定义的线程处理类加载方式
1. 继承`AbstractThreadListener`类并重写 `start()`, `stop()`即可
```java
import org.springframework.stereotype.Component;

@Component
public class DemoJobThread extends AbstractThreadListener {

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
```
2. 若需要控制多个自定义任务线程`start()`顺序, 实现`org.springframework.core.Ordered`类并重新`getOrder()`即可

```java
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class DemoJobThread extends AbstractThreadListener implements Ordered {

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public int getOrder() {
        
        // 设定值
        return 11111;
    }
    
}
```







