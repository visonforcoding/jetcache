/**
 * Created on 2018/8/11.
 */
package jetcache.samples.spring;

import org.vison.cache.anno.CacheConsts;
import org.vison.cache.anno.config.EnableMethodCache;
import org.vison.cache.anno.support.GlobalCacheConfig;
import org.vison.cache.anno.support.JetCacheBaseBeans;
import org.vison.cache.embedded.EmbeddedCacheBuilder;
import org.vison.cache.embedded.LinkedHashMapCacheBuilder;
import org.vison.cache.redis.RedisCacheBuilder;
import org.vison.cache.support.Fastjson2KeyConvertor;
import org.vison.cache.support.JavaValueDecoder;
import org.vison.cache.support.JavaValueEncoder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.Pool;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMethodCache(basePackages = "jetcache.samples.spring")
@Import(JetCacheBaseBeans.class)
public class JetCacheConfig {

    @Bean
    public Pool<Jedis> pool(){
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        return new JedisPool(pc, "127.0.0.1", 6379);
    }

    @Bean
    public GlobalCacheConfig config(Pool<Jedis> pool){
        Map localBuilders = new HashMap();
        EmbeddedCacheBuilder localBuilder = LinkedHashMapCacheBuilder
                .createLinkedHashMapCacheBuilder()
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE);
        localBuilders.put(CacheConsts.DEFAULT_AREA, localBuilder);

        Map remoteBuilders = new HashMap();
        RedisCacheBuilder remoteCacheBuilder = RedisCacheBuilder.createRedisCacheBuilder()
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .jedisPool(pool);
        remoteBuilders.put(CacheConsts.DEFAULT_AREA, remoteCacheBuilder);

        GlobalCacheConfig globalCacheConfig = new GlobalCacheConfig();
        globalCacheConfig.setLocalCacheBuilders(localBuilders);
        globalCacheConfig.setRemoteCacheBuilders(remoteBuilders);
        globalCacheConfig.setStatIntervalMinutes(1);

        return globalCacheConfig;
    }
}
