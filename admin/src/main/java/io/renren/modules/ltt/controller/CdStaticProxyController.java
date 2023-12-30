package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdStaticProxyDTO;
import io.renren.modules.ltt.vo.CdStaticProxyVO;
import io.renren.modules.ltt.service.CdStaticProxyService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-13 22:33:36
 */
@RestController
@RequestMapping("ltt/cdstaticproxy")
public class CdStaticProxyController {
    @Autowired
    private CdStaticProxyService cdStaticProxyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdstaticproxy:list")
    public R list(CdStaticProxyDTO cdStaticProxy){
        PageUtils page = cdStaticProxyService.queryPage(cdStaticProxy);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdstaticproxy:info")
    public R info(@PathVariable("id") Integer id){
		CdStaticProxyVO cdStaticProxy = cdStaticProxyService.getById(id);

        return R.ok().put("cdStaticProxy", cdStaticProxy);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdstaticproxy:save")
    public R save(@RequestBody CdStaticProxyDTO cdStaticProxy){
		cdStaticProxyService.save(cdStaticProxy);

        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/saveBatch")
    @RequiresPermissions("ltt:cdstaticproxy:save")
    public R saveBatch(@RequestBody CdStaticProxyDTO cdStaticProxy){
        cdStaticProxyService.saveBatchTxt(cdStaticProxy);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdstaticproxy:update")
    public R update(@RequestBody CdStaticProxyDTO cdStaticProxy){
		cdStaticProxyService.updateById(cdStaticProxy);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdstaticproxy:delete")
    public R delete(@RequestBody Integer[] ids){
		cdStaticProxyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
