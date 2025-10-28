package org.vison.cache.autoconfigure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.vison.cache.Cache;
import org.vison.cache.CacheValueHolder;
import org.vison.cache.anno.CreateCache;
import org.vison.cache.anno.config.EnableCreateCacheAnnotation;
import org.vison.cache.anno.config.EnableMethodCache;
import org.vison.cache.test.beans.MyFactoryBean;
import org.vison.cache.test.spring.SpringTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.vison.cache.test.support.JetcacheEncoder;

import javax.annotation.PostConstruct;

/**
 * Created on 2022/7/13.
 *
 * @author <a href="mailto:jeason1914@qq.com">yangyong</a>
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
@EnableMethodCache(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
public class RedissonStarterTest extends SpringTest {

    @Test
    public void tests() throws Exception {
        System.setProperty("spring.profiles.active", "redisson");
        context = SpringApplication.run(RedissonStarterTest.class);
        doTest();
    }

    @Component
    public static class A {
        @CreateCache
        private Cache cache;

        @PostConstruct
        public void test() {
            Assert.assertTrue(cache.PUT("K", "V").isSuccess());
        }
    }


    @Bean(name = "factoryBeanTarget")
    public MyFactoryBean factoryBean() {
        return new MyFactoryBean();
    }
}
