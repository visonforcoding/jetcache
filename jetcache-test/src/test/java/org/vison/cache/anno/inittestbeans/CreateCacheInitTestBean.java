package org.vison.cache.anno.inittestbeans;

import org.vison.cache.Cache;
import org.vison.cache.anno.CreateCache;
import org.junit.Assert;

/**
 * Created on 2017/5/5.
 *
 * @author huangli
 */
public class CreateCacheInitTestBean {
    @CreateCache
    private Cache cache;

    public void doTest() {
        Assert.assertNotNull(cache);
        Assert.assertTrue(cache.PUT("K1", "V1").isSuccess());
        Assert.assertEquals("V1", cache.get("K1"));

    }

}
