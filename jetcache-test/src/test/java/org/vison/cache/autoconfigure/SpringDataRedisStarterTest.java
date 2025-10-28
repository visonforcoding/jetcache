package org.vison.cache.autoconfigure;

import org.vison.cache.Cache;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created on 2019/5/1.
 *
 * @author huangli
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
@EnableMethodCache(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
@EnableCreateCacheAnnotation
public class SpringDataRedisStarterTest extends SpringTest {

    @Test
    public void tests() throws Exception {
        System.setProperty("spring.profiles.active", "redis-springdata");
        context = SpringApplication.run(SpringDataRedisStarterTest.class);
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

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory connectionFactory =  new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("127.0.0.1", 6379));
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }
}
