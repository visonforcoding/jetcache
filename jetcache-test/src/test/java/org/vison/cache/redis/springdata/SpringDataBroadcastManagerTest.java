/**
 * Created on 2022/7/16.
 */
package org.vison.cache.redis.springdata;

import org.vison.cache.SimpleCacheManager;
import org.vison.cache.redis.AbstractBroadcastManagerTest;
import org.vison.cache.support.BroadcastManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.lang.reflect.Constructor;
import java.util.UUID;

/**
 * @author huangli
 */
public class SpringDataBroadcastManagerTest extends AbstractBroadcastManagerTest {

    private void doTest(RedisConnectionFactory f) throws Exception {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(f);
        listenerContainer.afterPropertiesSet();
        listenerContainer.start();
        BroadcastManager bm = RedisSpringDataCacheBuilder
                .createBuilder()
                .keyPrefix(UUID.randomUUID().toString())
                .broadcastChannel(UUID.randomUUID().toString())
                .connectionFactory(f)
                .listenerContainer(listenerContainer)
                .createBroadcastManager(new SimpleCacheManager());
        testBroadcastManager(bm);
    }

    @Test
    public void testLettuce() throws Exception {
        LettuceConnectionFactory f = new LettuceConnectionFactory(new RedisStandaloneConfiguration("127.0.0.1", 6379));
        f.afterPropertiesSet();
        doTest(f);
    }

    @Test
    @DisabledForJreRange(max = JRE.JAVA_16,
            disabledReason = "in profile for java8 to 16, we use spring boot 2.x, it need jedis 3")
    public void testJedis() throws Exception {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("127.0.0.1", 6379);
        Constructor<JedisConnectionFactory> c = JedisConnectionFactory.class.getConstructor(RedisStandaloneConfiguration.class);
        JedisConnectionFactory f = c.newInstance(config);
        f.afterPropertiesSet();
        doTest(f);
    }


}
