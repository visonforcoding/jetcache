import org.vison.cache.Cache;
import org.vison.cache.CacheManager;
import org.vison.cache.MultiLevelCacheBuilder;
import org.vison.cache.SimpleCacheManager;
import org.vison.cache.embedded.CaffeineCacheBuilder;
import org.vison.cache.redis.RedisCacheBuilder;
import org.vison.cache.support.BroadcastManager;
import org.vison.cache.support.CacheNotifyMonitor;
import org.vison.cache.support.Fastjson2KeyConvertor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2022/07/14.
 *
 * @author huangli
 */
public class LocalCacheSyncExample {

    private static Cache<Object, Object> createMultiLevelCache() {
        Cache<Object, Object> l1Cache = CaffeineCacheBuilder.createCaffeineCacheBuilder()
                .expireAfterWrite(1000, TimeUnit.SECONDS)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .buildCache();

        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        JedisPool pool = new JedisPool(pc, "127.0.0.1", 6379);
        Cache<Object, Object> l2Cache = RedisCacheBuilder.createRedisCacheBuilder()
                .jedisPool(pool)
                .expireAfterWrite(1000, TimeUnit.SECONDS)
                .keyPrefix("projectC")
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .buildCache();

        // each cache area has only one BroadcastManager instance
        CacheManager cacheManager = new SimpleCacheManager();
        BroadcastManager broadcastManager = RedisCacheBuilder.createRedisCacheBuilder()
                .jedisPool(pool)
                .broadcastChannel("projectA")
                .createBroadcastManager(cacheManager);
        broadcastManager.startSubscribe();
        cacheManager.putBroadcastManager(broadcastManager);


        Cache<Object, Object> multiLevelCache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder()
                .addCache(l1Cache, l2Cache)
                .buildCache();
        cacheManager.putCache("cacheName", multiLevelCache);
        multiLevelCache.config().getMonitors().add(
                new CacheNotifyMonitor(cacheManager, "cacheName"));
        return multiLevelCache;
    }

    public static class CacheUpdater {
        public static void main(String[] args) throws Exception {
            Cache<Object, Object> multiLevelCache = createMultiLevelCache();
            while (true) {
                int value = new Random().nextInt();
                multiLevelCache.put("KEY", value);
                System.out.println("set " + value);
                Thread.sleep(1000);
            }
        }
    }

    public static class CacheReader {
        public static void main(String[] args) throws Exception {
            Cache<Object, Object> multiLevelCache = createMultiLevelCache();
            while (true) {
                System.out.println("get " + multiLevelCache.get("KEY"));
                Thread.sleep(1000);
            }
        }
    }


}
