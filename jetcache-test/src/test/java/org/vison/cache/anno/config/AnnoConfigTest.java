package org.vison.cache.anno.config;

import org.vison.cache.anno.support.GlobalCacheConfig;
import org.vison.cache.anno.support.JetCacheBaseBeans;
import org.vison.cache.test.anno.TestUtil;
import org.vison.cache.test.beans.MyFactoryBean;
import org.vison.cache.test.spring.SpringTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created on 2016/11/16.
 *
 * @author huangli
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AnnoConfigTest.A.class)
public class AnnoConfigTest extends SpringTest {
    @Test
    public void test() throws Exception {
        doTest();
    }

    @Configuration
    @ComponentScan(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
    @EnableMethodCache(basePackages = {"org.vison.cache.test.beans", "org.vison.cache.anno.inittestbeans"})
    @EnableCreateCacheAnnotation
    @Import(JetCacheBaseBeans.class)
    public static class A {

        @Bean
        public GlobalCacheConfig config() {
            GlobalCacheConfig pc = TestUtil.createGloableConfig();
            return pc;
        }

        @Bean(name = "factoryBeanTarget")
        public MyFactoryBean factoryBean() {
            return new MyFactoryBean();
        }
    }


}
