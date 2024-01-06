

package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum ExportStatus implements BaseEnum {

    ExportStatus1(1,"未导出"),
    ExportStatus2(2,"已导出"),
    ;

    ExportStatus(int key, String value) {
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
