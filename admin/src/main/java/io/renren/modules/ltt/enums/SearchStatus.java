
package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum SearchStatus implements BaseEnum {

    SearchStatus1(1,"未搜索"),
    SearchStatus2(2,"已搜索"),
    ;

    SearchStatus(int key, String value) {
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
