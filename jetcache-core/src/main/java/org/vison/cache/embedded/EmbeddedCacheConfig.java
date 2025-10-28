package org.vison.cache.embedded;

import org.vison.cache.CacheConfig;
import org.vison.cache.anno.CacheConsts;

/**
 * Created on 16/9/7.
 *
 * @author huangli
 */
public class EmbeddedCacheConfig<K, V> extends CacheConfig<K, V> {
    private int limit = CacheConsts.DEFAULT_LOCAL_LIMIT;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
