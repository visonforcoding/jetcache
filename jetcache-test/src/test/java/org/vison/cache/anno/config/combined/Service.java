package org.vison.cache.anno.config.combined;

import org.vison.cache.anno.Cached;

/**
 * Created on 2017/2/14.
 *
 * @author huangli
 */
public interface Service {
    @Cached
    int combinedTest1();

    int combinedTest2();
}
