package io.renren.app;

import io.renren.common.base.vo.EnumVo;
import io.renren.common.utils.EnumUtil;
import io.renren.modules.ltt.enums.AddFriendType;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2024/3/26 15:14
 */
public class App2 {
    public static void main(String[] args) {
        List<EnumVo> enumVos = EnumUtil.enumToVo(AddFriendType.values());
        Queue<EnumVo> queue = new LinkedList<>(enumVos);
        EnumVo frontElement = null;
        // 将队头元素移动到队尾
        if (!queue.isEmpty()) {
            frontElement = queue.poll();// 移除队头元素
            queue.offer(frontElement); // 将这个元素添加到队尾
        }


        System.out.println(frontElement.getValue());

    }
}
