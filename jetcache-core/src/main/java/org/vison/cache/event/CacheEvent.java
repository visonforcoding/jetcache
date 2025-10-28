/**
 * Created on 2017/2/22.
 */
package org.vison.cache.event;

import org.vison.cache.Cache;
import org.vison.cache.support.Epoch;

/**
 * The CacheEvent is used in single JVM while CacheMessage used for distributed message.
 *
 * @author huangli
 */
public class CacheEvent {

    private final long epoch = Epoch.get();

    protected Cache cache;

    public CacheEvent(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

    public long getEpoch() {
        return epoch;
    }
}
