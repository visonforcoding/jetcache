package org.vison.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.vison.cache.test.support.JetcacheEncoder;

@EnableAutoConfiguration
@ContextConfiguration
public class SpringContext {

    @Bean(name = "cacheJackson2")
    JetcacheEncoder jetcacheEncoder() {
        Jackson2JsonRedisSerializer<CacheValueHolder> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(CacheValueHolder.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.deactivateDefaultTyping();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        JetcacheEncoder serialPolicy = new JetcacheEncoder();
        serialPolicy.setJackson2JsonRedisSerializer(jackson2JsonRedisSerializer);
        return serialPolicy;
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
