/**
 * Created on 2022/07/30.
 */
package org.vison.cache.template;

import org.vison.cache.AbstractCacheBuilder;
import org.vison.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huangli
 */
public class CacheBuilderTemplate {
    private final boolean penetrationProtect;
    private final Map<String, CacheBuilder>[] cacheBuilders;
    private final List<CacheMonitorInstaller> cacheMonitorInstallers = new ArrayList<>();

    @SafeVarargs
    public CacheBuilderTemplate(boolean penetrationProtect, Map<String, CacheBuilder>... cacheBuilders) {
        this.penetrationProtect = penetrationProtect;
        this.cacheBuilders = cacheBuilders;
    }

    public boolean isPenetrationProtect() {
        return penetrationProtect;
    }

    public CacheBuilder getCacheBuilder(int level, String area) {
        CacheBuilder cb = cacheBuilders[level].get(area);
        if (cb instanceof AbstractCacheBuilder) {
            return (CacheBuilder) ((AbstractCacheBuilder<?>) cb).clone();
        } else {
            return cb;
        }
    }

    public List<CacheMonitorInstaller> getCacheMonitorInstallers() {
        return cacheMonitorInstallers;
    }
}
