/**
 * Created on 2022/08/06.
 */
package jetcache.samples.sync;

import org.vison.cache.Cache;
import org.vison.cache.CacheManager;
import org.vison.cache.anno.CacheType;
import org.vison.cache.template.QuickConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;

/**
 * @author huangli
 */
@SpringBootApplication
public class CacheReader {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(CacheReader.class);
        CacheManager cm = context.getBean(CacheManager.class);
        QuickConfig qc = QuickConfig.newBuilder("spring-boot-sync-test")
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .expire(Duration.ofSeconds(200))
                .build();
        Cache<String, Integer> c = cm.getOrCreateCache(qc);
        for (int i = 0; i < 100; i++) {
            System.out.println("get " + c.get("MyKey"));
            Thread.sleep(1000);
        }
    }
}
