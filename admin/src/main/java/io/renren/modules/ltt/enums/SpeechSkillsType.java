

package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum SpeechSkillsType implements BaseEnum {

    SpeechSkillsType1(1,"落地页"),
    SpeechSkillsType2(2,"图片"),
    SpeechSkillsType3(3,"文字"),
    ;

    SpeechSkillsType(int key, String value) {
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
