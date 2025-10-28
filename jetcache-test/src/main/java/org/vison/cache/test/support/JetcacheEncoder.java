package org.vison.cache.test.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.vison.cache.CacheValueHolder;
import org.vison.cache.anno.SerialPolicy;

import java.util.function.Function;

public class JetcacheEncoder implements SerialPolicy {
    private  Jackson2JsonRedisSerializer<CacheValueHolder> jackson2JsonRedisSerializer;
    private final Logger logger = LoggerFactory.getLogger(JetcacheEncoder.class);


    public void setJackson2JsonRedisSerializer(Jackson2JsonRedisSerializer<CacheValueHolder> jackson2JsonRedisSerializer) {
        this.jackson2JsonRedisSerializer = jackson2JsonRedisSerializer;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        logger.debug("JetcacheEncoder encoder");
        return this::serialize;
    }

    public  byte[] serialize(Object value) {
        // 直接调用 Jackson2JsonRedisSerializer 序列化
        return jackson2JsonRedisSerializer.serialize(value);
    }

    @Override
    public Function<byte[], Object> decoder() {
        return bytes -> jackson2JsonRedisSerializer.deserialize(bytes);
    }

}
