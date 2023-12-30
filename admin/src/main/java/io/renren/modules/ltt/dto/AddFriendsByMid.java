package io.renren.modules.ltt.dto;

import lombok.Data;

@Data
public class AddFriendsByMid {

    private String proxy;
    private String phone;
    private String mid;
    private String friendAddType;
    private String token;
}
