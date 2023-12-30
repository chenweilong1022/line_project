package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.DeptDTO;
import io.renren.modules.ltt.vo.DeptVO;
import io.renren.modules.ltt.service.DeptService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-06-08 11:34:05
 */
@RestController
@RequestMapping("ltt/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:dept:list")
    public R list(DeptDTO dept){
        PageUtils page = deptService.queryPage(dept);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{deptid}")
    @RequiresPermissions("ltt:dept:info")
    public R info(@PathVariable("deptid") Integer deptid){
		DeptVO dept = deptService.getById(deptid);

        return R.ok().put("dept", dept);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:dept:save")
    public R save(@RequestBody DeptDTO dept){
		deptService.save(dept);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:dept:update")
    public R update(@RequestBody DeptDTO dept){
		deptService.updateById(dept);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:dept:delete")
    public R delete(@RequestBody Integer[] deptids){
		deptService.removeByIds(Arrays.asList(deptids));

        return R.ok();
    }

}
