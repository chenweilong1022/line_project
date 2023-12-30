package io.renren.modules.ltt.service;

import io.renren.modules.ltt.entity.CdLineRegisterEntity;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 22:02
 */
public interface ProxyService {

    String getflowip(CdLineRegisterEntity cdLineRegisterEntity);
    String getflowip1(CdLineRegisterEntity cdLineRegisterEntity);
    String getflowip2();
}
