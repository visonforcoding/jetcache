/**
 * Created on  13-10-02 22:14
 */
package org.vison.cache.anno.method;

import org.vison.cache.anno.CacheConsts;
import org.vison.cache.anno.support.CacheUpdateAnnoConfig;
import org.vison.cache.anno.support.CachedAnnoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huangli
 */
public class ExpressionUtilTest {
    private CacheInvokeContext context;
    private CachedAnnoConfig cachedAnnoConfig;
    private CacheInvokeConfig cic;
    private Method method;

    public void targetMethod(String p1, int p2) {
    }

    @BeforeEach
    public void setup() throws Exception {
        method = ExpressionUtilTest.class.getMethod("targetMethod", String.class, int.class);
        context = new CacheInvokeContext();
        cachedAnnoConfig = new CachedAnnoConfig();
        cachedAnnoConfig.setDefineMethod(method);
        cic = new CacheInvokeConfig();
        context.setCacheInvokeConfig(cic);
        context.getCacheInvokeConfig().setCachedAnnoConfig(cachedAnnoConfig);
    }

    @Test
    public void testCondition1() {
        cachedAnnoConfig.setCondition("args[0]==null");
        assertFalse(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
        context.setArgs(new Object[2]);
        assertTrue(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
    }
    @Test
    public void testCondition2() {
        cachedAnnoConfig.setCondition("args[0].length()==4");
        context.setArgs(new Object[]{"1234", 5678});
        assertTrue(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testCondition3() {
        cachedAnnoConfig.setCondition(CacheConsts.UNDEFINED_STRING);
        assertTrue(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testPostCondition1() {
        cachedAnnoConfig.setPostCondition("result==null");
        context.setArgs(new Object[]{"1234", 5678});
        assertTrue(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testPostCondition2() {
        cachedAnnoConfig.setPostCondition("#p2==1000");
        context.setArgs(new Object[]{"1234", 5678});
        assertFalse(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
        context.setArgs(new Object[]{"1234", 1000});
        assertTrue(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testPostCondition3() {
        cachedAnnoConfig.setPostCondition(CacheConsts.UNDEFINED_STRING);
        context.setArgs(new Object[]{"1234", 5678});
        assertTrue(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testKey1() {
        cachedAnnoConfig.setKey("#p2");
        context.setArgs(new Object[]{"1234", 5678});
        assertEquals(5678, ExpressionUtil.evalKey(context, cachedAnnoConfig));
    }

    @Test
    public void testKey2() {
        cachedAnnoConfig.setKey("#p3");
        context.setArgs(new Object[]{"1234", 5678});
        assertNull(ExpressionUtil.evalKey(context, cachedAnnoConfig));
    }

    @Test
    public void testValue1() {
        cic.setCachedAnnoConfig(null);
        CacheUpdateAnnoConfig updateAnnoConfig = new CacheUpdateAnnoConfig();
        updateAnnoConfig.setDefineMethod(method);
        updateAnnoConfig.setValue("#p2");

        context.setArgs(new Object[]{"1234", 5678});
        assertEquals(5678, ExpressionUtil.evalValue(context, updateAnnoConfig));
    }

    @Test
    public void testValue2() {
        cic.setCachedAnnoConfig(null);
        CacheUpdateAnnoConfig updateAnnoConfig = new CacheUpdateAnnoConfig();
        updateAnnoConfig.setDefineMethod(method);
        updateAnnoConfig.setValue("#p3");

        context.setArgs(new Object[]{"1234", 5678});
        assertNull(ExpressionUtil.evalValue(context, updateAnnoConfig));
    }

}
