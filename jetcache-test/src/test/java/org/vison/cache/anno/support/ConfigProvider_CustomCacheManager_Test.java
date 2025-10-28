/**
 * Created on 2019/2/2.
 */
package org.vison.cache.anno.support;

import org.vison.cache.CacheManager;
import org.vison.cache.SimpleCacheManager;
import org.vison.cache.anno.Cached;
import org.vison.cache.anno.config.EnableMethodCache;
import org.vison.cache.test.anno.TestUtil;
import org.vison.cache.test.spring.SpringTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author huangli
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConfigProvider_CustomCacheManager_Test.class)
@Configuration
@EnableMethodCache(basePackages = {"org.vison.cache.anno.support.ConfigProvider_CustomCacheManager_Test"})
public class ConfigProvider_CustomCacheManager_Test extends SpringTestBase {

    @Bean
    public GlobalCacheConfig config() {
        GlobalCacheConfig pc = TestUtil.createGloableConfig();
        return pc;
    }

    @Bean
    public SpringConfigProvider springConfigProvider(
            @Autowired ApplicationContext context,
            @Autowired GlobalCacheConfig config) {
        return new JetCacheBaseBeans().springConfigProvider(context, config, null, null, null);
    }

    @Bean
    public SimpleCacheManager cacheManager(@Autowired ConfigProvider configProvider) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCacheBuilderTemplate(configProvider.getCacheBuilderTemplate());
        return cacheManager;
    }

    public static class CountBean {
        private int i;

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

        context.getBean(CacheManager.class).getCache("C1").remove("K1");
        Assert.assertNotEquals(value, bean.count("K1"));
    }

}
