/**
 * Created on  13-10-07 23:30
 */
package org.vison.cache.test.beans;

import org.springframework.stereotype.Component;

/**
 * @author huangli
 */
@Component
public class ConfigBean {
    public boolean isTrueProp() {
         return true;
    }

    public boolean isFalseProp() {
        return false;
    }
}
