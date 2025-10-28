/**
 * Created on 2018/5/11.
 */
package org.vison.cache.anno.method;

import org.vison.cache.Cache;
import org.vison.cache.CacheManager;
import org.vison.cache.anno.CacheConsts;
import org.vison.cache.anno.support.CacheUpdateAnnoConfig;
import org.vison.cache.anno.support.ConfigMap;
import org.vison.cache.anno.support.ConfigProvider;
import org.vison.cache.anno.support.GlobalCacheConfig;
import org.vison.cache.anno.support.JetCacheBaseBeans;
import org.vison.cache.embedded.LinkedHashMapCacheBuilder;
import org.vison.cache.support.FastjsonKeyConvertor;
import org.vison.cache.test.anno.TestUtil;
import org.vison.cache.testsupport.CountClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author huangli
 */
public class CacheHandlerUpdateTest {
    private ConfigProvider configProvider;
    private CacheManager cacheManager;
    private CacheInvokeConfig cacheInvokeConfig;
    private CountClass count;
    private Cache cache;
    private ConfigMap configMap;
    private CacheUpdateAnnoConfig updateAnnoConfig;
    private CacheInvokeContext cacheInvokeContext;

    @BeforeEach
    public void setup() throws Exception {
        GlobalCacheConfig globalCacheConfig = TestUtil.createGloableConfig();
        configProvider = new ConfigProvider();
        configProvider.setGlobalCacheConfig(globalCacheConfig);
        configProvider.init();
        cacheManager = new JetCacheBaseBeans().cacheManager(configProvider);
        cache = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .buildCache();


        cacheInvokeConfig = new CacheInvokeConfig();

        configMap = new ConfigMap();

        count = new CountClass();

        Method method = CountClass.class.getMethod("update", String.class, int.class);
        cacheInvokeContext = configProvider.newContext(cacheManager).createCacheInvokeContext(configMap);
        cacheInvokeContext.setCacheInvokeConfig(cacheInvokeConfig);
        updateAnnoConfig = new CacheUpdateAnnoConfig();
        updateAnnoConfig.setCondition(CacheConsts.UNDEFINED_STRING);
        updateAnnoConfig.setDefineMethod(method);
        cacheInvokeConfig.setUpdateAnnoConfig(updateAnnoConfig);

        updateAnnoConfig.setKey("args[0]");
        updateAnnoConfig.setValue("args[1]");
        cacheInvokeContext.setMethod(method);
        cacheInvokeContext.setArgs(new Object[]{"K1", 1000});
        cacheInvokeContext.setInvoker(() -> cacheInvokeContext.getMethod().invoke(count, cacheInvokeContext.getArgs()));
        cacheInvokeContext.setCacheFunction((a, b) -> cache);
    }

    @AfterEach
    public void tearDown() {
        configProvider.shutdown();
    }


    @Test
    public void testUpdate() throws Throwable {
        cache.put("K1", "V");
        CacheHandler.invoke(cacheInvokeContext);
        assertEquals(1000, cache.get("K1"));
    }


    @Test
    public void testConditionTrue() throws Throwable {
        cache.put("K1", "V");
        updateAnnoConfig.setCondition("args[1]==1000");
        CacheHandler.invoke(cacheInvokeContext);
        assertEquals(1000, cache.get("K1"));
    }

    @Test
    public void testConditionFalse() throws Throwable {
        cache.put("K1", "V");
        updateAnnoConfig.setCondition("args[1]!=1000");
        CacheHandler.invoke(cacheInvokeContext);
        assertEquals("V", cache.get("K1"));

    }

    @Test
    public void testBadCondition() throws Throwable {
        cache.put("K1", "V");
        updateAnnoConfig.setCondition("bad condition");
        CacheHandler.invoke(cacheInvokeContext);
        assertEquals("V", cache.get("K1"));
    }

    @Test
    public void testBadKey() throws Throwable {
        cache.put("K1", "V");
        updateAnnoConfig.setKey("bad key script");
        CacheHandler.invoke(cacheInvokeContext);
        assertEquals("V", cache.get("K1"));
    }

    @Test
    public void testBadValue() throws Throwable {
        cache.put("K1", "V");
        updateAnnoConfig.setValue("bad value script");
        CacheHandler.invoke(cacheInvokeContext);
        assertEquals("V", cache.get("K1"));
    }

    static class TestMulti {
        public void update(String keys, int[] values) {
        }

        public void update(String[] keys, int values) {
        }

        public void update(String[] keys, int[] values) {
        }
    }

    @Test
    public void testMulti() throws Throwable {
        {
            Method method = TestMulti.class.getMethod("update", String[].class, int[].class);
            updateAnnoConfig.setDefineMethod(method);
            updateAnnoConfig.setKey("args[0]");
            updateAnnoConfig.setValue("args[1]");
            cacheInvokeContext.setMethod(method);
            cacheInvokeContext.setArgs(new Object[]{new String[]{"K1", "K2"}, new int[]{10, 20}});
            cacheInvokeContext.setInvoker(() -> method.invoke(new TestMulti(), cacheInvokeContext.getArgs()));

            cache.put("K1", 1);
            cache.put("K2", 2);
            CacheHandler.invoke(cacheInvokeContext);
            assertEquals(1, cache.get("K1"));
            assertEquals(2, cache.get("K2"));

            updateAnnoConfig.setMulti(true);

            cacheInvokeContext.setArgs(new Object[]{null, new int[]{10, 20}});
            CacheHandler.invoke(cacheInvokeContext);
            assertEquals(1, cache.get("K1"));
            assertEquals(2, cache.get("K2"));

            cacheInvokeContext.setArgs(new Object[]{new String[]{"K1", "K2"}, null});
            CacheHandler.invoke(cacheInvokeContext);
            assertEquals(1, cache.get("K1"));
            assertEquals(2, cache.get("K2"));

            cacheInvokeContext.setArgs(new Object[]{new String[]{"K1", "K2"}, new int[]{10, 20}});
            CacheHandler.invoke(cacheInvokeContext);
            assertEquals(10, cache.get("K1"));
            assertEquals(20, cache.get("K2"));
        }
        {
            Method method = TestMulti.class.getMethod("update", String.class, int[].class);
            updateAnnoConfig.setDefineMethod(method);
            updateAnnoConfig.setKey("args[0]");
            updateAnnoConfig.setValue("args[1]");
            cacheInvokeContext.setMethod(method);
            cacheInvokeContext.setArgs(new Object[]{"K1", new int[]{10, 20}});
            cacheInvokeContext.setInvoker(() -> method.invoke(new TestMulti(), cacheInvokeContext.getArgs()));

            cache.put("K1", 1);
            cache.put("K2", 2);
            updateAnnoConfig.setMulti(true);
            CacheHandler.invoke(cacheInvokeContext);
            assertEquals(1, cache.get("K1"));
            assertEquals(2, cache.get("K2"));
        }
        {
            Method method = TestMulti.class.getMethod("update", String[].class, int.class);
            updateAnnoConfig.setDefineMethod(method);
            updateAnnoConfig.setKey("args[0]");
            updateAnnoConfig.setValue("args[1]");
            cacheInvokeContext.setMethod(method);
            cacheInvokeContext.setArgs(new Object[]{new String[]{"K1"}, 10});
            cacheInvokeContext.setInvoker(() -> method.invoke(new TestMulti(), cacheInvokeContext.getArgs()));

            cache.put("K1", 1);
            cache.put("K2", 2);
            updateAnnoConfig.setMulti(true);
            CacheHandler.invoke(cacheInvokeContext);
            assertEquals(1, cache.get("K1"));
            assertEquals(2, cache.get("K2"));
        }
    }
}
