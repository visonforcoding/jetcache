package org.vison.cache.anno.config.combined;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created on 2017/2/14.
 *
 * @author huangli
 */
//@Component
public class ServiceDelegate implements Service {

    @Autowired
    private Service service;

    @Override
    public int combinedTest1() {
        return service.combinedTest1();
    }

    @Override
    public int combinedTest2() {
        return service.combinedTest2();
    }
}
