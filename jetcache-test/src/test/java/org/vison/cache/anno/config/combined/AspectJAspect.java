package org.vison.cache.anno.config.combined;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Created on 2017/2/15.
 *
 * @author huangli
 */
@Aspect
public class AspectJAspect {
    @Before("(target(org.vison.cache.anno.config.combined.ServiceImpl) && execution(* *()))")
    public void after(JoinPoint jointPoint) {
        System.out.println("before " + jointPoint.getSignature());
    }
}
