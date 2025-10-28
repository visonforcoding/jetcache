/**
 * Created on 2019/6/7.
 */
package org.vison.cache.anno.support;

import org.vison.cache.CacheConfigException;
import org.vison.cache.anno.KeyConvertor;
import org.vison.cache.support.FastjsonKeyConvertor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Function;

/**
 * @author huangli
 */
public class DefaultSpringKeyConvertorParser extends DefaultKeyConvertorParser implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Function<Object, Object> parseKeyConvertor(String convertor) {
        String beanName = DefaultSpringEncoderParser.parseBeanName(convertor);
        if (beanName == null) {
            return super.parseKeyConvertor(convertor);
        } else {
            return (Function<Object, Object>) applicationContext.getBean(beanName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
