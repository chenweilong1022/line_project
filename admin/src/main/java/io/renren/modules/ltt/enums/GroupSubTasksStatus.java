
package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum GroupSubTasksStatus implements BaseEnum {

    GroupStatus1(1,"发起拉群"),
    GroupStatus2(2,"子任务进行中"),
    GroupStatus4(4,"子任务完成"),
    GroupStatus5(5,"子任务失败"),
    ;

    GroupSubTasksStatus(int key, String value) {
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
