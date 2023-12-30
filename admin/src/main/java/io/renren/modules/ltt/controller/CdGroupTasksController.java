package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdGroupTasksDTO;
import io.renren.modules.ltt.vo.CdGroupTasksVO;
import io.renren.modules.ltt.service.CdGroupTasksService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-02 19:02:28
 */
@RestController
@RequestMapping("ltt/cdgrouptasks")
public class CdGroupTasksController {
    @Autowired
    private CdGroupTasksService cdGroupTasksService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdgrouptasks:list")
    public R list(CdGroupTasksDTO cdGroupTasks){
        PageUtils page = cdGroupTasksService.queryPage(cdGroupTasks);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdgrouptasks:info")
    public R info(@PathVariable("id") Integer id){
		CdGroupTasksVO cdGroupTasks = cdGroupTasksService.getById(id);

        return R.ok().put("cdGroupTasks", cdGroupTasks);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdgrouptasks:save")
    public R save(@RequestBody CdGroupTasksDTO cdGroupTasks){
		cdGroupTasksService.save(cdGroupTasks);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdgrouptasks:update")
    public R update(@RequestBody CdGroupTasksDTO cdGroupTasks){
		cdGroupTasksService.updateById(cdGroupTasks);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdgrouptasks:delete")
    public R delete(@RequestBody Integer[] ids){
		cdGroupTasksService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
