package org.vison.cache;

import org.vison.cache.event.CacheEvent;

/**
 * Created on 2016/10/25.
 *
 * @author huangli
 */
@FunctionalInterface
public interface CacheMonitor {

    void afterOperation(CacheEvent event);

}
