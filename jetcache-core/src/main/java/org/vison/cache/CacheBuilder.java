package org.vison.cache;

/**
 * Created on 2016/11/17.
 *
 * @author huangli
 */
public interface CacheBuilder {
    <K, V> Cache<K, V> buildCache();
}
