package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.ProvincesDTO;
import io.renren.modules.ltt.vo.ProvincesVO;
import io.renren.modules.ltt.service.ProvincesService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 省份信息表
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-06-08 11:35:41
 */
@RestController
@RequestMapping("ltt/provinces")
public class ProvincesController {
    @Autowired
    private ProvincesService provincesService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:provinces:list")
    public R list(ProvincesDTO provinces){
        PageUtils page = provincesService.queryPage(provinces);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:provinces:info")
    public R info(@PathVariable("id") Integer id){
		ProvincesVO provinces = provincesService.getById(id);

        return R.ok().put("provinces", provinces);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:provinces:save")
    public R save(@RequestBody ProvincesDTO provinces){
		provincesService.save(provinces);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:provinces:update")
    public R update(@RequestBody ProvincesDTO provinces){
		provincesService.updateById(provinces);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:provinces:delete")
    public R delete(@RequestBody Integer[] ids){
		provincesService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
