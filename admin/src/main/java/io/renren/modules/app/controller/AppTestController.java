package io.renren.modules.app.controller;


import io.renren.common.utils.R;
import io.renren.common.validator.AssertI18n;
import io.renren.modules.app.code.UserCode;
import io.renren.modules.app.dto.UserUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * APP测试接口
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:47
 */
@RestController
@RequestMapping("/app")
@Validated
@Slf4j
public class AppTestController {


    @GetMapping("update")
    public R update(@Valid UserUpdateDTO updateDTO) {
        log.info("[update][updateDTO: {}]", updateDTO);
        AssertI18n.isTrue(updateDTO.getId() < 10, UserCode.USER_ID_MIN_ERROR);
        return R.ok();
    }

}
