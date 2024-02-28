package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdMaterialPhoneSendRecordDTO;
import io.renren.modules.ltt.vo.CdMaterialPhoneSendRecordVO;
import io.renren.modules.ltt.service.CdMaterialPhoneSendRecordService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2024-01-29 22:59:57
 */
@RestController
@RequestMapping("ltt/cdmaterialphonesendrecord")
public class CdMaterialPhoneSendRecordController {
    @Autowired
    private CdMaterialPhoneSendRecordService cdMaterialPhoneSendRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdmaterialphonesendrecord:list")
    public R list(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord){
        PageUtils page = cdMaterialPhoneSendRecordService.queryPage(cdMaterialPhoneSendRecord);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdmaterialphonesendrecord:info")
    public R info(@PathVariable("id") Integer id){
		CdMaterialPhoneSendRecordVO cdMaterialPhoneSendRecord = cdMaterialPhoneSendRecordService.getById(id);

        return R.ok().put("cdMaterialPhoneSendRecord", cdMaterialPhoneSendRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdmaterialphonesendrecord:save")
    public R save(@RequestBody CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord){
		cdMaterialPhoneSendRecordService.save(cdMaterialPhoneSendRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdmaterialphonesendrecord:update")
    public R update(@RequestBody CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord){
		cdMaterialPhoneSendRecordService.updateById(cdMaterialPhoneSendRecord);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdmaterialphonesendrecord:delete")
    public R delete(@RequestBody Integer[] ids){
		cdMaterialPhoneSendRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
