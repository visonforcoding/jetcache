/**
 * Created on  13-09-19 21:49
 */
package org.vison.cache.test.beans;

import org.vison.cache.anno.CacheInvalidate;
import org.vison.cache.anno.CacheUpdate;
import org.vison.cache.anno.Cached;
import org.vison.cache.anno.EnableCache;

/**
 * @author huangli
 */
public interface Service {

    int notCachedCount();

    int countWithAnnoOnClass();

    @Cached
    int countWithAnnoOnInterface();

    @EnableCache
    int enableCacheWithAnnoOnInterface(TestBean bean);

    int enableCacheWithAnnoOnClass(TestBean bean);

    int enableCacheWithNoCacheCount(TestBean bean);

    @Cached(name = "c1", key = "args[0]")
    int count(String id);

    @Cached(name="foo",expire = 60)
    String foo();

    @CacheUpdate(name = "c1", key = "#id", value = "args[1]")
    void update(String id, int value);

    @CacheUpdate(name = "c2", key = "args[0]", value = "args[1]")
    void update2(String id, int value);

    @CacheInvalidate(name = "c1", key = "args[0]")
    void delete(String id);

    @CacheInvalidate(name = "c2", key = "args[0]")
    void delete2(String id);
}
