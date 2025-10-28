/**
 * Created on  13-09-18 20:33
 */
package org.vison.cache.anno.aop;

import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.vison.cache.CacheManager;
import org.vison.cache.anno.method.CacheHandler;
import org.vison.cache.anno.method.CacheInvokeConfig;
import org.vison.cache.anno.method.CacheInvokeContext;
import org.vison.cache.anno.support.ConfigMap;
import org.vison.cache.anno.support.ConfigProvider;
import org.vison.cache.anno.support.GlobalCacheConfig;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

/**
 * @author huangli
 */
public class JetCacheInterceptor implements MethodInterceptor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(JetCacheInterceptor.class);
    public static final String OBJECT_MAPPER_BEAN_NAME = "OBJECT_MAPPER_JETCACHE";

    @Autowired
    private ConfigMap cacheConfigMap;
    private ApplicationContext applicationContext;
    private GlobalCacheConfig globalCacheConfig;
    ConfigProvider configProvider;
    CacheManager cacheManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (configProvider == null) {
            configProvider = applicationContext.getBean(ConfigProvider.class);
        }
        if (configProvider != null && globalCacheConfig == null) {
            globalCacheConfig = configProvider.getGlobalCacheConfig();
        }
        if (globalCacheConfig == null || !globalCacheConfig.isEnableMethodCache()) {
            return invocation.proceed();
        }
        if (cacheManager == null) {
            cacheManager = applicationContext.getBean(CacheManager.class);
            if (cacheManager == null) {
                logger.error("There is no cache manager instance in spring context");
                return invocation.proceed();
            }
        }

        Method method = invocation.getMethod();
        Object obj = invocation.getThis();
        CacheInvokeConfig cac = null;
        if (obj != null) {
            String key = CachePointcut.getKey(method, obj.getClass());
            cac  = cacheConfigMap.getByMethodInfo(key);
        }

        /*
        if(logger.isTraceEnabled()){
            logger.trace("JetCacheInterceptor invoke. foundJetCacheConfig={}, method={}.{}(), targetClass={}",
                    cac != null,
                    method.getDeclaringClass().getName(),
                    method.getName(),
                    invocation.getThis() == null ? null : invocation.getThis().getClass().getName());
        }
        */

        if (cac == null || cac == CacheInvokeConfig.getNoCacheInvokeConfigInstance()) {
            return invocation.proceed();
        }

        CacheInvokeContext context = configProvider.newContext(cacheManager).createCacheInvokeContext(cacheConfigMap);
        context.setTargetObject(invocation.getThis());
        context.setInvoker(invocation::proceed);
        context.setMethod(method);
        context.setArgs(invocation.getArguments());
        context.setCacheInvokeConfig(cac);
        context.setHiddenPackages(globalCacheConfig.getHiddenPackages());
        Class<?> returnType = invocation.getMethod().getReturnType();
        Object result =  CacheHandler.invoke(context);
        try{
            Object typedResult = returnType.cast(result);
            return typedResult;
        }catch (ClassCastException e){
            ObjectMapper objectMapper = getObjectMapper();
            return objectMapper.convertValue(result,returnType);
        }
    }

    /**
     * 获取ObjectMapper bean
     * 逻辑：优先获取primary bean；若存在多个同类型bean且无primary，则获取名称为OBJECT_MAPPER_JETCACHE的bean
     *
     * @return ObjectMapper实例
     * @throws IllegalStateException 当未找到符合条件的bean时抛出
     */
    private ObjectMapper getObjectMapper() {
        try {
            // 第一步：尝试获取primary的ObjectMapper（Spring默认会优先返回primary bean）
            return applicationContext.getBean(ObjectMapper.class);
        } catch (NoUniqueBeanDefinitionException e) {
            // 第二步：若存在多个bean且无primary，尝试按指定名称获取
            try {
                return applicationContext.getBean(OBJECT_MAPPER_BEAN_NAME, ObjectMapper.class);
            } catch (Exception ex) {
                throw new IllegalStateException("未找到名称为[" + OBJECT_MAPPER_BEAN_NAME + "]的ObjectMapper bean", ex);
            }
        } catch (Exception e) {
            // 其他异常（如无任何ObjectMapper bean）
            throw new IllegalStateException("Spring容器中未找到ObjectMapper bean", e);
        }
    }

    public void setCacheConfigMap(ConfigMap cacheConfigMap) {
        this.cacheConfigMap = cacheConfigMap;
    }

}
