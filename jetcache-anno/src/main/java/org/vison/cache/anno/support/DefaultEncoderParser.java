/**
 * Created on 2019/6/7.
 */
package org.vison.cache.anno.support;

import org.vison.cache.CacheConfigException;
import org.vison.cache.anno.SerialPolicy;
import org.vison.cache.support.JavaValueDecoder;
import org.vison.cache.support.JavaValueEncoder;
import org.vison.cache.support.Kryo5ValueDecoder;
import org.vison.cache.support.Kryo5ValueEncoder;
import org.vison.cache.support.KryoValueDecoder;
import org.vison.cache.support.KryoValueEncoder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author huangli
 */
public class DefaultEncoderParser implements EncoderParser {
    protected static Map<String, String> parseQueryParameters(String query) {
        Map<String, String> m = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = idx > 0 ? pair.substring(0, idx) : pair;
                String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
                if (key != null && value != null) {
                    m.put(key, value);
                }
            }
        }
        return m;
    }

    JavaValueDecoder javaValueDecoder(boolean useIdentityNumber) {
        return new JavaValueDecoder(useIdentityNumber);
    }

    @Override
    public Function<Object, byte[]> parseEncoder(String valueEncoder) {
        if (valueEncoder == null) {
            throw new CacheConfigException("no serialPolicy");
        }
        valueEncoder = valueEncoder.trim();
        URI uri = URI.create(valueEncoder);
        valueEncoder = uri.getPath();
        boolean useIdentityNumber = isUseIdentityNumber(uri);
        if (SerialPolicy.KRYO.equalsIgnoreCase(valueEncoder)) {
            return new KryoValueEncoder(useIdentityNumber);
        } else if (SerialPolicy.JAVA.equalsIgnoreCase(valueEncoder)) {
            return new JavaValueEncoder(useIdentityNumber);
        } else if (SerialPolicy.KRYO5.equalsIgnoreCase(valueEncoder)) {
            return new Kryo5ValueEncoder(useIdentityNumber);
        }/* else if (SerialPolicy.FASTJSON2.equalsIgnoreCase(valueEncoder)) {
            return new Fastjson2ValueEncoder(useIdentityNumber);
        }*/ else {
            throw new CacheConfigException("not supported:" + valueEncoder);
        }
    }

    private boolean isUseIdentityNumber(URI uri) {
        Map<String, String> params = parseQueryParameters(uri.getQuery());
        boolean useIdentityNumber = true;
        if ("false".equalsIgnoreCase(params.get("useIdentityNumber"))) {
            useIdentityNumber = false;
        }
        return useIdentityNumber;
    }

    @Override
    public Function<byte[], Object> parseDecoder(String valueDecoder) {
        if (valueDecoder == null) {
            throw new CacheConfigException("no serialPolicy");
        }
        valueDecoder = valueDecoder.trim();
        URI uri = URI.create(valueDecoder);
        valueDecoder = uri.getPath();
        boolean useIdentityNumber = isUseIdentityNumber(uri);
        if (SerialPolicy.KRYO.equalsIgnoreCase(valueDecoder)) {
            return new KryoValueDecoder(useIdentityNumber);
        } else if (SerialPolicy.JAVA.equalsIgnoreCase(valueDecoder)) {
            return javaValueDecoder(useIdentityNumber);
        } else if (SerialPolicy.KRYO5.equalsIgnoreCase(valueDecoder)) {
            return new Kryo5ValueDecoder(useIdentityNumber);
        }/* else if (SerialPolicy.FASTJSON2.equalsIgnoreCase(valueDecoder)) {
            return new Kryo5ValueDecoder(useIdentityNumber);
        }*/ else {
            throw new CacheConfigException("not supported:" + valueDecoder);
        }
    }
}
