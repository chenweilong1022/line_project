package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdMaterialPhoneDTO;
import io.renren.modules.ltt.vo.CdMaterialPhoneVO;
import io.renren.modules.ltt.service.CdMaterialPhoneService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-12 12:30:34
 */
@RestController
@RequestMapping("ltt/cdmaterialphone")
public class CdMaterialPhoneController {
    @Autowired
    private CdMaterialPhoneService cdMaterialPhoneService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdmaterialphone:list")
    public R list(CdMaterialPhoneDTO cdMaterialPhone){
        PageUtils page = cdMaterialPhoneService.queryPage(cdMaterialPhone);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdmaterialphone:info")
    public R info(@PathVariable("id") Integer id){
		CdMaterialPhoneVO cdMaterialPhone = cdMaterialPhoneService.getById(id);

        return R.ok().put("cdMaterialPhone", cdMaterialPhone);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdmaterialphone:save")
    public R save(@RequestBody CdMaterialPhoneDTO cdMaterialPhone){
		cdMaterialPhoneService.save(cdMaterialPhone);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdmaterialphone:update")
    public R update(@RequestBody CdMaterialPhoneDTO cdMaterialPhone){
		cdMaterialPhoneService.updateById(cdMaterialPhone);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdmaterialphone:delete")
    public R delete(@RequestBody Integer[] ids){
		cdMaterialPhoneService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
