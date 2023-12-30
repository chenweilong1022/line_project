package io.renren.modules.ltt.service.impl;

import io.renren.modules.ltt.service.ProxyService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 22:05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LunaProxyImplTest extends TestCase {


    @Autowired
    private ProxyService proxyService;

    @Test
    public void testGetflowip() {

//        proxyService.getflowip("");
    }
}
