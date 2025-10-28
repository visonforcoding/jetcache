package org.vison.cache.autoconfigure;

import org.vison.cache.CacheBuilder;
import org.vison.cache.anno.CacheConsts;
import org.vison.cache.embedded.EmbeddedCacheBuilder;

/**
 * Created on 2016/12/2.
 *
 * @author huangli
 */
public abstract class EmbeddedCacheAutoInit extends AbstractCacheAutoInit {

    public EmbeddedCacheAutoInit(String... cacheTypes) {
        super(cacheTypes);
    }

    @Override
    protected void parseGeneralConfig(CacheBuilder builder, ConfigTree ct) {
        super.parseGeneralConfig(builder, ct);
        EmbeddedCacheBuilder ecb = (EmbeddedCacheBuilder) builder;

        ecb.limit(Integer.parseInt(ct.getProperty("limit", String.valueOf(CacheConsts.DEFAULT_LOCAL_LIMIT))));
    }
}
