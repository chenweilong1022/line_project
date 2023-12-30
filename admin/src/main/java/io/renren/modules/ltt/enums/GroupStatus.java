
package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum GroupStatus implements BaseEnum {

    GroupStatus1(1,"发起拉群"),
    GroupStatus2(2,"加好友中"),
    GroupStatus3(3,"正在拉群"),
    GroupStatus4(4,"拉群失败"),
    GroupStatus5(5,"拉群成功"),
    GroupStatus6(6,"同步通讯录"),
    GroupStatus7(7,"同步通讯录成功"),
    GroupStatus8(8,"同步通讯录失败"),
    GroupStatus9(9,"群人数同步"),
    ;

    GroupStatus(int key, String value) {
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
