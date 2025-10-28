package org.vison.cache.redis.springdata;

import org.vison.cache.LoadingCacheTest;
import org.vison.cache.RefreshCacheTest;
import org.vison.cache.support.FastjsonKeyConvertor;
import org.vison.cache.support.JavaValueDecoder;
import org.vison.cache.support.JavaValueEncoder;
import org.vison.cache.support.KryoValueDecoder;
import org.vison.cache.support.KryoValueEncoder;
import org.vison.cache.test.external.AbstractExternalCacheTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2016/10/8.
 *
 * @author huangli
 */
public class RedisSpringDataCacheTest extends AbstractExternalCacheTest {

    @Test
    @DisabledForJreRange(max = JRE.JAVA_16,
            disabledReason = "in profile for java8 to 16, we use spring boot 2.x, it need jedis 3")
    public void jedisTest() throws Exception {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.afterPropertiesSet();
        doTest(connectionFactory);
    }

    @Test
    public void lettuceTest() throws Exception {
        LettuceConnectionFactory connectionFactory =  new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("127.0.0.1", 6379));
        connectionFactory.afterPropertiesSet();
        doTest(connectionFactory);
    }


    private void doTest(RedisConnectionFactory connectionFactory) throws Exception {


        cache = RedisSpringDataCacheBuilder.createBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .connectionFactory(connectionFactory)
                .keyPrefix(new Random().nextInt() + "")
                .expireAfterWrite(500, TimeUnit.MILLISECONDS)
                .buildCache();

        baseTest();
        fastjsonKeyCoverterTest();
        expireAfterWriteTest(cache.config().getExpireAfterWriteInMillis());

        LoadingCacheTest.loadingCacheTest(RedisSpringDataCacheBuilder.createBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .connectionFactory(connectionFactory)
                .keyPrefix(new Random().nextInt() + ""), 0);
        RefreshCacheTest.refreshCacheTest(RedisSpringDataCacheBuilder.createBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .connectionFactory(connectionFactory)
                .keyPrefix(new Random().nextInt() + ""), 200, 100);


        cache = RedisSpringDataCacheBuilder.createBuilder()
                .keyConvertor(null)
                .valueEncoder(KryoValueEncoder.INSTANCE)
                .valueDecoder(KryoValueDecoder.INSTANCE)
                .connectionFactory(connectionFactory)
                .keyPrefix(new Random().nextInt() + "")
                .buildCache();
        nullKeyConvertorTest();

        int thread = 10;
        int time = 3000;
        cache = RedisSpringDataCacheBuilder.createBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(KryoValueEncoder.INSTANCE)
                .valueDecoder(KryoValueDecoder.INSTANCE)
                .connectionFactory(connectionFactory)
                .keyPrefix(new Random().nextInt() + "")
                .buildCache();
        concurrentTest(thread, 500, time);
    }


}
