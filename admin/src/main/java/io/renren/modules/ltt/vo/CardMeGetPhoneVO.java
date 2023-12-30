// CardMeGetPhoneVO.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package io.renren.modules.ltt.vo;
import lombok.Data;

import java.util.List;

@lombok.Data
public class CardMeGetPhoneVO {

    private String msg;

    private long code;

    private Data data;

    @lombok.Data
    public class Data {

        private String iccid;

        private String phone;

    }
}
