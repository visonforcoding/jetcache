package org.vison.cache.redis.springdata;

import org.vison.cache.CacheManager;
import org.vison.cache.external.ExternalCacheBuilder;
import org.vison.cache.support.BroadcastManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Created on 2019/4/21.
 *
 * @author huangli
 */
public class RedisSpringDataCacheBuilder<T extends ExternalCacheBuilder<T>> extends ExternalCacheBuilder<T> {
    public static class RedisSpringDataCacheBuilderImpl extends RedisSpringDataCacheBuilder<RedisSpringDataCacheBuilderImpl> {
    }

    public static RedisSpringDataCacheBuilderImpl createBuilder() {
        return new RedisSpringDataCacheBuilderImpl();
    }

    protected RedisSpringDataCacheBuilder() {
        buildFunc(config -> new RedisSpringDataCache((RedisSpringDataCacheConfig) config));
    }

    @Override
    public RedisSpringDataCacheConfig getConfig() {
        if (config == null) {
            config = new RedisSpringDataCacheConfig();
        }
        return (RedisSpringDataCacheConfig) config;
    }

    @Override
    public boolean supportBroadcast() {
        return true;
    }

    @Override
    public BroadcastManager createBroadcastManager(CacheManager cacheManager) {
        RedisSpringDataCacheConfig c = (RedisSpringDataCacheConfig) getConfig().clone();
        return new SpringDataBroadcastManager(cacheManager, c);
    }

    public T connectionFactory(RedisConnectionFactory connectionFactory) {
        getConfig().setConnectionFactory(connectionFactory);
        return self();
    }

    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        getConfig().setConnectionFactory(connectionFactory);
    }

    public T listenerContainer(RedisMessageListenerContainer listenerContainer) {
        getConfig().setListenerContainer(listenerContainer);
        return self();
    }

    public void setListenerContainer(RedisMessageListenerContainer listenerContainer) {
        getConfig().setListenerContainer(listenerContainer);
    }
}
