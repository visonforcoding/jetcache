/**
 * Created on 2018/8/11.
 */
package jetcache.samples.spring;

import org.vison.cache.anno.Cached;

/**
 * @author huangli
 */
public interface UserService {
    @Cached(name = "loadUser", expire = 10)
    User loadUser(long userId);
}
