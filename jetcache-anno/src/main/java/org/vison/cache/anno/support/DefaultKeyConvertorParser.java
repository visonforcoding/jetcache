/**
 * Created on 2019/6/7.
 */
package org.vison.cache.anno.support;

import org.vison.cache.CacheConfigException;
import org.vison.cache.anno.KeyConvertor;
import org.vison.cache.support.Fastjson2KeyConvertor;
import org.vison.cache.support.FastjsonKeyConvertor;
import org.vison.cache.support.JacksonKeyConvertor;

import java.util.function.Function;

/**
 * @author huangli
 */
public class DefaultKeyConvertorParser implements KeyConvertorParser {
    @Override
    public Function<Object, Object> parseKeyConvertor(String convertor) {
        if (convertor == null) {
            return null;
        }
        if (KeyConvertor.FASTJSON.equalsIgnoreCase(convertor)) {
            return FastjsonKeyConvertor.INSTANCE;
        } else if (KeyConvertor.FASTJSON2.equalsIgnoreCase(convertor)) {
            return Fastjson2KeyConvertor.INSTANCE;
        } else if (KeyConvertor.JACKSON.equalsIgnoreCase(convertor)) {
            return JacksonKeyConvertor.INSTANCE;
        } else if (KeyConvertor.NONE.equalsIgnoreCase(convertor)) {
            return KeyConvertor.NONE_INSTANCE;
        }
        throw new CacheConfigException("not supported:" + convertor);
    }
}
