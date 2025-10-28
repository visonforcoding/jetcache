package org.vison.cache.test.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.vison.cache.SpringContext;
import org.vison.cache.anno.config.EnableMethodCache;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@ComponentScan(basePackages = {"org.vison.cache.test.service"})
@EnableMethodCache(basePackages = {"org.vison.cache.test.service"})
@SpringBootTest(classes = SpringContext.class)
class CacheServiceTest {

    @Resource
    public CacheService cacheService;

    @BeforeAll
    static void tearUp() {
        System.setProperty("spring.profiles.active", "redisson");
    }

    @Test
    void getString() {
        String result = cacheService.getString("vison");
        assertEquals("Hello, vison", result);
    }
    @Test
    void getUser() {
        User user = new User();
        user.setUserId(123);
        user.setUserName("vison");
        user = cacheService.getUser(user);
        assertEquals("vison", user.getUserName());
    }
}