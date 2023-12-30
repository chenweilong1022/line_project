package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdLineErrLogsDTO;
import io.renren.modules.ltt.vo.CdLineErrLogsVO;
import io.renren.modules.ltt.service.CdLineErrLogsService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-05 19:11:08
 */
@RestController
@RequestMapping("ltt/cdlineerrlogs")
public class CdLineErrLogsController {
    @Autowired
    private CdLineErrLogsService cdLineErrLogsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdlineerrlogs:list")
    public R list(CdLineErrLogsDTO cdLineErrLogs){
        PageUtils page = cdLineErrLogsService.queryPage(cdLineErrLogs);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdlineerrlogs:info")
    public R info(@PathVariable("id") Integer id){
		CdLineErrLogsVO cdLineErrLogs = cdLineErrLogsService.getById(id);

        return R.ok().put("cdLineErrLogs", cdLineErrLogs);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdlineerrlogs:save")
    public R save(@RequestBody CdLineErrLogsDTO cdLineErrLogs){
		cdLineErrLogsService.save(cdLineErrLogs);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdlineerrlogs:update")
    public R update(@RequestBody CdLineErrLogsDTO cdLineErrLogs){
		cdLineErrLogsService.updateById(cdLineErrLogs);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdlineerrlogs:delete")
    public R delete(@RequestBody Integer[] ids){
		cdLineErrLogsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
