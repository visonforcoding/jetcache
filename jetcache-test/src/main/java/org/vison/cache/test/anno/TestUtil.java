/**
 * Created on  13-09-23 09:36
 */
package org.vison.cache.test.anno;

import org.vison.cache.anno.CacheConsts;
import org.vison.cache.anno.support.GlobalCacheConfig;
import org.vison.cache.anno.support.JetCacheBaseBeans;
import org.vison.cache.anno.support.SpringConfigProvider;
import org.vison.cache.embedded.EmbeddedCacheBuilder;
import org.vison.cache.embedded.LinkedHashMapCacheBuilder;
import org.vison.cache.external.MockRemoteCacheBuilder;
import org.vison.cache.support.Fastjson2KeyConvertor;
import org.vison.cache.support.KryoValueDecoder;
import org.vison.cache.support.KryoValueEncoder;
import junit.framework.AssertionFailedError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author huangli
 */
public class TestUtil {
    public static GlobalCacheConfig createGloableConfig() {
        Map localBuilders = new HashMap();
        EmbeddedCacheBuilder localBuilder = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder();
        localBuilder.setKeyConvertor(Fastjson2KeyConvertor.INSTANCE);
        localBuilders.put(CacheConsts.DEFAULT_AREA, localBuilder);
        localBuilders.put("A1", localBuilder);

        Map remoteBuilders = new HashMap();

        MockRemoteCacheBuilder remoteBuilder = MockRemoteCacheBuilder.createMockRemoteCacheBuilder();
        remoteBuilder.setKeyConvertor(Fastjson2KeyConvertor.INSTANCE);
        remoteBuilder.setValueEncoder(KryoValueEncoder.INSTANCE);
        remoteBuilder.setValueDecoder(KryoValueDecoder.INSTANCE);
        remoteBuilder.setBroadcastChannel("mockBroadcastChannel");
        remoteBuilders.put(CacheConsts.DEFAULT_AREA, remoteBuilder);

        remoteBuilder = MockRemoteCacheBuilder.createMockRemoteCacheBuilder();
        remoteBuilder.setKeyConvertor(Fastjson2KeyConvertor.INSTANCE);
        remoteBuilder.setValueEncoder(KryoValueEncoder.INSTANCE);
        remoteBuilder.setValueDecoder(KryoValueDecoder.INSTANCE);
        remoteBuilder.setBroadcastChannel("mockBroadcastChannel_A1");
        remoteBuilders.put("A1", remoteBuilder);

        GlobalCacheConfig globalCacheConfig = new GlobalCacheConfig();
        globalCacheConfig.setLocalCacheBuilders(localBuilders);
        globalCacheConfig.setRemoteCacheBuilders(remoteBuilders);

        return globalCacheConfig;
    }

    public static SpringConfigProvider createConfigProvider() {
        JetCacheBaseBeans baseBeans = new JetCacheBaseBeans();
        SpringConfigProvider configProvider = baseBeans.springConfigProvider(
                null, createGloableConfig(), null, null, null);
        baseBeans.cacheManager(configProvider);
        return configProvider;
    }

    public static void waitUtil(Supplier<Boolean> condition) {
        waitUtil(Boolean.TRUE, condition);
    }

    public static void waitUtil(Object expectValue, Supplier<? extends Object> actual) {
        waitUtil(expectValue, actual, 5000);
    }

    public static void waitUtil(Object expectValue, Supplier<? extends Object> actual, long timeoutMillis) {
        long start = System.nanoTime();
        long deadline = start + timeoutMillis * 1000 * 1000;
        Object obj = actual.get();
        if (Objects.equals(expectValue, obj)) {
            return;
        }
        int waitCount = 0;
        while (deadline - System.nanoTime() > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            waitCount++;
            obj = actual.get();
            if(Objects.equals(expectValue, obj)){
                return;
            }
        }
        throw new AssertionFailedError("expect: " + expectValue + ", actual:" + obj + ", timeout="
                + timeoutMillis + "ms, cost=" + (System.nanoTime() - start) / 1000 / 1000 + "ms, waitCount=" + waitCount);
    }

}
