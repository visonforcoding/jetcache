/**
 * Created on  13-09-19 20:40
 */
package org.vison.cache.anno.aop;

import org.vison.cache.anno.support.ConfigMap;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author huangli
 */
public class CacheAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    public static final String CACHE_ADVISOR_BEAN_NAME = "jetcache2.internalCacheAdvisor";

    @Autowired
    private ConfigMap cacheConfigMap;

    private String[] basePackages;

    @Override
    public Pointcut getPointcut() {
        CachePointcut pointcut = new CachePointcut(basePackages);
        pointcut.setCacheConfigMap(cacheConfigMap);
        return pointcut;
    }

    public void setCacheConfigMap(ConfigMap cacheConfigMap) {
        this.cacheConfigMap = cacheConfigMap;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }
}
