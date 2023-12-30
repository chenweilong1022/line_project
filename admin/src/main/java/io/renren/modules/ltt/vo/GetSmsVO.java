package io.renren.modules.ltt.vo;

import lombok.Data;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/12/28 22:17
 */
@Data
public class GetSmsVO {
    private String msg;
    private int code;
    private String data;
}
