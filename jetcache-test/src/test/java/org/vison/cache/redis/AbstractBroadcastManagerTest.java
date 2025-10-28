/**
 * Created on 2022/07/10.
 */
package org.vison.cache.redis;

import org.vison.cache.Cache;
import org.vison.cache.CacheResult;
import org.vison.cache.MultiLevelCache;
import org.vison.cache.MultiLevelCacheBuilder;
import org.vison.cache.embedded.LinkedHashMapCacheBuilder;
import org.vison.cache.support.BroadcastManager;
import org.vison.cache.support.CacheMessage;
import org.vison.cache.test.anno.TestUtil;
import org.junit.jupiter.api.Assertions;

/**
 * @author huangli
 */
public class AbstractBroadcastManagerTest {
    protected void testBroadcastManager(BroadcastManager manager) throws Exception {
        CacheMessage cm = new CacheMessage();
        cm.setArea("area");
        cm.setCacheName("cacheName");
        cm.setKeys(new String[]{"K"});
        cm.setValues(new String[]{"V1"});
        cm.setType(100);

        Cache c1 = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder().buildCache();
        Cache c2 = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder().buildCache();
        MultiLevelCache mc = (MultiLevelCache) MultiLevelCacheBuilder
                .createMultiLevelCacheBuilder()
                .addCache(c1, c2)
                .buildCache();
        mc.put("K", "V1");
        Assertions.assertEquals("V1", c1.get("K"));
        manager.getCacheManager().putCache("area", "cacheName", mc);

        manager.startSubscribe();
        Thread.sleep(50);
        CacheResult result = manager.publish(cm);
        Assertions.assertTrue(result.isSuccess());

        TestUtil.waitUtil(() -> c1.get("K") == null);

        manager.close();
    }
}
