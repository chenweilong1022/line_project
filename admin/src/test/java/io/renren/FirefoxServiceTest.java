package io.renren;

import cn.hutool.core.util.ObjectUtil;
import io.renren.modules.ltt.service.FirefoxService;
import io.renren.modules.ltt.vo.GetPhoneVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 21:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FirefoxServiceTest {


    @Resource(name = "cardMeServiceImpl")
    private FirefoxService firefoxService;

    @Test
    public void getPhone() {
        GetPhoneVo phone = firefoxService.getPhone();
        if (ObjectUtil.isNotNull(phone)) {
            System.out.println(phone.getPhone());
            firefoxService.getPhoneCode(phone.getPkey());
        }
    }

}
