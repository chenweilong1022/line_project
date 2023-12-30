package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdGroupSubtasksDTO;
import io.renren.modules.ltt.vo.CdGroupSubtasksVO;
import io.renren.modules.ltt.service.CdGroupSubtasksService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-02 19:02:29
 */
@RestController
@RequestMapping("ltt/cdgroupsubtasks")
public class CdGroupSubtasksController {
    @Autowired
    private CdGroupSubtasksService cdGroupSubtasksService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdgroupsubtasks:list")
    public R list(CdGroupSubtasksDTO cdGroupSubtasks){
        PageUtils page = cdGroupSubtasksService.queryPage(cdGroupSubtasks);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdgroupsubtasks:info")
    public R info(@PathVariable("id") Integer id){
		CdGroupSubtasksVO cdGroupSubtasks = cdGroupSubtasksService.getById(id);

        return R.ok().put("cdGroupSubtasks", cdGroupSubtasks);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdgroupsubtasks:save")
    public R save(@RequestBody CdGroupSubtasksDTO cdGroupSubtasks){
		cdGroupSubtasksService.save(cdGroupSubtasks);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdgroupsubtasks:update")
    public R update(@RequestBody CdGroupSubtasksDTO cdGroupSubtasks){
		cdGroupSubtasksService.updateById(cdGroupSubtasks);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdgroupsubtasks:delete")
    public R delete(@RequestBody Integer[] ids){
		cdGroupSubtasksService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
