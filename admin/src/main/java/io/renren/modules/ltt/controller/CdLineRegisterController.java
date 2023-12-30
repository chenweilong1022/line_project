package io.renren.modules.ltt.controller;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdLineRegisterDTO;
import io.renren.modules.ltt.vo.CdLineRegisterVO;
import io.renren.modules.ltt.service.CdLineRegisterService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;

import javax.servlet.http.HttpServletResponse;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-11-30 22:33:44
 */
@RestController
@RequestMapping("ltt/cdlineregister")
public class CdLineRegisterController {
    @Autowired
    private CdLineRegisterService cdLineRegisterService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdlineregister:list")
    public R list(CdLineRegisterDTO cdLineRegister){
        PageUtils page = cdLineRegisterService.queryPage(cdLineRegister);

        return R.ok().put("page", page);
    }

    /**
     * 根据taskid查询
     */
    @RequestMapping("/listByTaskId")
    @RequiresPermissions("ltt:cdlineregister:list")
    public R listByTaskId(CdLineRegisterDTO cdLineRegister){
        PageUtils page = cdLineRegisterService.listByTaskId(cdLineRegister);
        return R.ok().put("page", page);
    }

    /**
     * 清空代理
     */
    @RequestMapping("/clearProxy")
    @RequiresPermissions("ltt:cdlineregister:list")
    public R clearProxy(){
        cdLineRegisterService.clearProxy();
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdlineregister:info")
    public R info(@PathVariable("id") Integer id){
		CdLineRegisterVO cdLineRegister = cdLineRegisterService.getById(id);

        return R.ok().put("cdLineRegister", cdLineRegister);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdlineregister:save")
    public R save(@RequestBody CdLineRegisterDTO cdLineRegister){
		cdLineRegisterService.save(cdLineRegister);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdlineregister:update")
    public R update(@RequestBody CdLineRegisterDTO cdLineRegister){
		cdLineRegisterService.updateById(cdLineRegister);

        return R.ok();
    }

    /**
     * 检测是否封控
     */
    @RequestMapping("/issueLiffView")
    @RequiresPermissions("ltt:cdlineregister:delete")
    public R issueLiffView(@RequestBody Integer[] ids){
        cdLineRegisterService.issueLiffView(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * importToken
     */
    @RequestMapping("/importToken")
    @RequiresPermissions("ltt:cdlineregister:delete")
    public void importToken(Integer[] ids, HttpServletResponse response) throws IOException {
        byte[] bytes = cdLineRegisterService.importToken(Arrays.asList(ids));
        response.reset();
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.zip\"",java.net.URLEncoder.encode("token","UTF-8")));
        response.addHeader("Content-Length", "" + bytes.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(bytes, response.getOutputStream());
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdlineregister:delete")
    public R delete(@RequestBody Integer[] ids){
		cdLineRegisterService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
