
package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum MaterialPhoneStatus implements BaseEnum {

    MaterialPhoneStatus1(1,"未分配账号"),
    MaterialPhoneStatus2(2,"已分配任务"),
    MaterialPhoneStatus3(3,"通讯录已同步"),
    MaterialPhoneStatus4(4,"通讯录同步失败"),
    MaterialPhoneStatus5(5,"成功拉群数据"),
    ;

    MaterialPhoneStatus(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private final int key;
    private final String value;


    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
