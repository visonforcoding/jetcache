package org.vison.cache.autoconfigure;

import org.vison.cache.Cache;
import org.vison.cache.anno.CreateCache;
import org.vison.cache.anno.config.EnableCreateCacheAnnotation;
import org.vison.cache.anno.config.EnableMethodCache;
import org.vison.cache.support.FastjsonKeyConvertor;
import org.vison.cache.support.JavaValueDecoder;
import org.vison.cache.support.JavaValueEncoder;
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

import javax.annotation.PostConstruct;
import java.util.function.Function;

/**
 * Created on 2019/6/21.
 *
 * @author huangli
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
@EnableMethodCache(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
@EnableCreateCacheAnnotation
public class MockStarterTest extends SpringTest {

    @Test
    public void tests() throws Exception {
        System.setProperty("spring.profiles.active", "redisson");
        context = SpringApplication.run(MockStarterTest.class);
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
    public Function<Object, byte[]> myEncoder() {
        return new JavaValueEncoder(true);
    }

    @Bean
    public Function<byte[], Object> myDecoder() {
        return new JavaValueDecoder(true);
    }

    @Bean
    public Function<Object, Object> myConvertor() {
        return FastjsonKeyConvertor.INSTANCE;
    }

}
