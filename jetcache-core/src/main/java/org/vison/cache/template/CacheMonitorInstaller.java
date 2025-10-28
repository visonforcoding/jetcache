/**
 * Created on 2019/6/7.
 */
package org.vison.cache.template;

import org.vison.cache.Cache;
import org.vison.cache.CacheManager;

/**
 * @author huangli
 */
public interface CacheMonitorInstaller {
    void addMonitors(CacheManager cacheManager, Cache cache, QuickConfig quickConfig);
}
