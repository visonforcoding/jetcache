/**
 * Created on 2019/2/2.
 */
package org.vison.cache.anno.support;

import org.vison.cache.Cache;
import org.vison.cache.CacheManager;
import org.vison.cache.anno.Cached;
import org.vison.cache.anno.CreateCache;
import org.vison.cache.anno.config.EnableCreateCacheAnnotation;
import org.vison.cache.anno.config.EnableMethodCache;
import org.vison.cache.test.anno.TestUtil;
import org.vison.cache.test.spring.SpringTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author huangli
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConfigProvider_DefaultCacheManager_Test.class)
@Configuration
@EnableMethodCache(basePackages = {"org.vison.cache.anno.support.ConfigProvider_DefaultCacheManager_Test"})
@EnableCreateCacheAnnotation
@Import(JetCacheBaseBeans.class)
public class ConfigProvider_DefaultCacheManager_Test extends SpringTestBase {

    @Bean
    public GlobalCacheConfig config() {
        GlobalCacheConfig pc = TestUtil.createGloableConfig();
        return pc;
    }

    public static class CountBean {
        private int i;

        @CreateCache(name = "C2")
        private Cache c2;

        @Cached(name = "C1", expire = 3, key = "#key")
        public String count(String key) {
            return key + i++;
        }
    }

    @Bean
    public CountBean countBean() {
        return new CountBean();
    }

    @Test
    public void test() {
        CountBean bean = context.getBean(CountBean.class);
        String value = (bean.count("K1"));
        Assert.assertEquals(value, bean.count("K1"));
        CacheManager cm = context.getBean(CacheManager.class);
        cm.getCache("C1").remove("K1");
        Assert.assertNotEquals(value, bean.count("K1"));
    }

    @Test
    public void test2() {
        CacheManager cm = context.getBean(CacheManager.class);
        Assert.assertNotNull(cm.getCache("C1"));
        Assert.assertNotNull(cm.getCache("C2"));
    }

}
