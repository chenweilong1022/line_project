

package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum MaterialType implements BaseEnum {

    MaterialType1(1,"料子"),
    MaterialType2(2,"水军"),
    MaterialType3(3,"已添加"),
    MaterialType4(4,"已添加"),
    ;

    MaterialType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private final Integer key;
    private final String value;


    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
