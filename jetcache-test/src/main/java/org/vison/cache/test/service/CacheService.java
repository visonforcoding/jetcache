package org.vison.cache.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vison.cache.anno.Cached;

@Slf4j
@Service
public class CacheService {


    @Cached(name = "getString",key = "#name",expire = 30)
    public String getString(String name) {
        return "Hello, " + name;
    }

     @Cached(name = "user:",key = "#user.userId",expire = 50)
    public User getUser(User user) {
        log.debug("方法getUser,参数user:{}",user);
        return user;
    }


}
