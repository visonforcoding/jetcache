/**
 * Created on  13-10-28 23:43
 */
package org.vison.cache.test.beans;

import org.vison.cache.anno.Cached;

/**
 * @author huangli
 */
public interface FactoryBeanTarget {
    @Cached
    int count();
}
