

package io.renren.modules.ltt.enums;


import io.renren.common.base.interfaces.BaseEnum;
import lombok.Getter;

@Getter
public enum AddFriendType implements BaseEnum {

    AddFriendType1(1,"addFriendsByAddressBook"),
    AddFriendType2(2,"addFriendsByChatHeader"),
    AddFriendType3(3,"addFriendsByHomeTabCPF"),
    AddFriendType4(4,"addFriendsByHomeTabCPFV2"),
//    AddFriendType5(5,"addFriendsByMid"),
    AddFriendType6(6,"addFriendsByNotification"),
//    AddFriendType7(7,"addFriendsByReference"),
    AddFriendType8(8,"addFriendsByMid"),
    AddFriendType9(9,"addFriendsByTalkRoom"),
    AddFriendType10(10,"addFriendsByTalkRoomMenu"),
    AddFriendType11(11,"addFriendsByTimelineCPF"),
    AddFriendType12(12,"addFriendsByTimelineHome"),
    AddFriendType13(13,"addFriendsByUnifiedSearch"),
    AddFriendType14(14,"addFriendsByUnifiedSearchOATab"),
    AddFriendType15(15,"addFriendsByWalletTabCPF"),
    AddFriendType16(16,"addFriendsByHomeRecommend"),
    AddFriendType17(17,"addFriendsByFriendRecommend"),
    ;

    AddFriendType(int key, String value) {
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
