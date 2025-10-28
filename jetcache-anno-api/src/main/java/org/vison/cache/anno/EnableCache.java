/**
 * Created on  13-09-04
 */
package org.vison.cache.anno;

import java.lang.annotation.*;

/**
 * @author huangli
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableCache {
}
