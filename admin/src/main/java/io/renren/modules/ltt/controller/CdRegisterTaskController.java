package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdRegisterTaskDTO;
import io.renren.modules.ltt.vo.CdRegisterTaskVO;
import io.renren.modules.ltt.service.CdRegisterTaskService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-02 00:31:50
 */
@RestController
@RequestMapping("ltt/cdregistertask")
public class CdRegisterTaskController {
    @Autowired
    private CdRegisterTaskService cdRegisterTaskService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdregistertask:list")
    public R list(CdRegisterTaskDTO cdRegisterTask){
        PageUtils page = cdRegisterTaskService.queryPage(cdRegisterTask);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdregistertask:info")
    public R info(@PathVariable("id") Integer id){
		CdRegisterTaskVO cdRegisterTask = cdRegisterTaskService.getById(id);

        return R.ok().put("cdRegisterTask", cdRegisterTask);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdregistertask:save")
    public R save(@RequestBody CdRegisterTaskDTO cdRegisterTask){
		cdRegisterTaskService.save(cdRegisterTask);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdregistertask:update")
    public R update(@RequestBody CdRegisterTaskDTO cdRegisterTask){
		cdRegisterTaskService.updateById(cdRegisterTask);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdregistertask:delete")
    public R delete(@RequestBody Integer[] ids){
		cdRegisterTaskService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
