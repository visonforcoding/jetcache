/**
 * Created on  13-10-17 22:34
 */
package org.vison.cache.embedded;

import java.util.Collection;
import java.util.Map;

/**
 * @author huangli
 */
public interface InnerMap {
    Object getValue(Object key);

    Map getAllValues(Collection keys);

    void putValue(Object key, Object value);

    void putAllValues(Map map);

    boolean removeValue(Object key);

    boolean putIfAbsentValue(Object key, Object value);

    void removeAllValues(Collection keys);
}
