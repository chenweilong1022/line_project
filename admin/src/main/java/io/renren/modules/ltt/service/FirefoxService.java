package io.renren.modules.ltt.service;

import io.renren.modules.ltt.vo.GetPhoneVo;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 21:28
 */
public interface FirefoxService {

    public GetPhoneVo getPhone();
    public String getPhoneCode(String pKey);
    public boolean setRel(String pKey);
    public boolean withBlackMobile(String pKey);
}
