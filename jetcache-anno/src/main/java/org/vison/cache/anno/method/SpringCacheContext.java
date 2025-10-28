package org.vison.cache.anno.method;

import org.vison.cache.CacheManager;
import org.vison.cache.anno.support.CacheContext;
import org.vison.cache.anno.support.GlobalCacheConfig;
import org.vison.cache.anno.support.SpringConfigProvider;
import org.springframework.context.ApplicationContext;

/**
 * Created on 2016/10/19.
 *
 * @author huangli
 */
public class SpringCacheContext extends CacheContext {

    private ApplicationContext applicationContext;

    public SpringCacheContext(CacheManager cacheManager, SpringConfigProvider configProvider,
                              GlobalCacheConfig globalCacheConfig, ApplicationContext applicationContext) {
        super(cacheManager, configProvider, globalCacheConfig);
        this.applicationContext = applicationContext;
    }

    @Override
    protected CacheInvokeContext newCacheInvokeContext() {
        return new SpringCacheInvokeContext(applicationContext);
    }

}
