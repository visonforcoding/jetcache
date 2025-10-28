/**
 * Created on 2018/1/19.
 */
package org.vison.cache.anno.method;

import org.vison.cache.CacheConfigException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huangli
 */
public class ExpressionEvaluatorTest {

    public void targetMethod(String p1, int p2) {
    }

    @Test
    public void test() throws Exception {
        Method m = ExpressionEvaluatorTest.class.getMethod("targetMethod", String.class, int.class);

        ExpressionEvaluator e = new ExpressionEvaluator("1+1", m);
        assertTrue(e.getTarget() instanceof SpelEvaluator);
        e = new ExpressionEvaluator("spel{1+1}", m);
        assertTrue(e.getTarget() instanceof SpelEvaluator);
        e = new ExpressionEvaluator("mvel{1+1}", m);
        assertTrue(e.getTarget() instanceof MvelEvaluator);
        assertThrows(CacheConfigException.class,() -> new ExpressionEvaluator("xxx{1+1}", m));
    }
}


