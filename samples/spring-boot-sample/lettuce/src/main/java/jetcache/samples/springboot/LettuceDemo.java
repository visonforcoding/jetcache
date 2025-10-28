/**
 * Created on 2018/8/11.
 */
package jetcache.samples.springboot;

import org.vison.cache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author huangli
 */
@SpringBootApplication
@EnableMethodCache(basePackages = "jetcache")
public class LettuceDemo {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LettuceDemo.class);
        MyService myService = context.getBean(MyService.class);
        myService.createCacheDemo();
        myService.cachedDemo();
    }
}
