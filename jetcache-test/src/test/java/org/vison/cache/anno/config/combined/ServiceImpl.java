package org.vison.cache.anno.config.combined;

import org.vison.cache.anno.Cached;

/**
 * Created on 2017/2/14.
 *
 * @author huangli
 */
//@Component
public class ServiceImpl implements Service {

    private int count;

    @Override
    public int combinedTest1() {
        return count++;
    }

    @Override
    @Cached
    public int combinedTest2() {
        return count++;
    }
}
