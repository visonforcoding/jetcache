/**
 * Created on 2019/6/13.
 */
package org.vison.cache;

import org.vison.cache.support.FastjsonKeyConvertor;
import org.vison.cache.support.JavaValueDecoder;
import org.vison.cache.support.JavaValueEncoder;
import org.vison.cache.test.AbstractCacheTest;
import org.vison.cache.external.MockRemoteCacheBuilder;
import org.junit.Test;

/**
 * @author huangli
 */
public class MockRemoteCacheTest extends AbstractCacheTest {
    @Test
    public void Test() throws Exception {
        MockRemoteCacheBuilder b = new MockRemoteCacheBuilder();
        b.setKeyConvertor(FastjsonKeyConvertor.INSTANCE);
        b.setValueDecoder(JavaValueDecoder.INSTANCE);
        b.setValueEncoder(JavaValueEncoder.INSTANCE);
        cache = b.buildCache();
        baseTest();
    }
}
