package io.renren.modules.ltt.controller;

import java.io.IOException;
import java.util.Arrays;

import io.renren.modules.ltt.dto.AutoAssignGroupsDTO;
import io.renren.modules.ltt.dto.ImportZipDTO;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdMaterialDTO;
import io.renren.modules.ltt.vo.CdMaterialVO;
import io.renren.modules.ltt.service.CdMaterialService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;

import javax.servlet.http.HttpServletResponse;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-12 12:30:34
 */
@RestController
@RequestMapping("ltt/cdmaterial")
public class CdMaterialController {
    @Autowired
    private CdMaterialService cdMaterialService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdmaterial:list")
    public R list(CdMaterialDTO cdMaterial){
        PageUtils page = cdMaterialService.queryPage(cdMaterial);

        return R.ok().put("page", page);
    }


    /**
     * 导出zip
     */
    @RequestMapping("importZip")
    @RequiresPermissions("ltt:cdmaterial:save")
    public void importZip(ImportZipDTO importZipDTO, HttpServletResponse response) throws IOException {
        byte[] bytes = cdMaterialService.importZip(importZipDTO);

        response.reset();
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.zip\"",java.net.URLEncoder.encode("输出","UTF-8")));
        response.addHeader("Content-Length", "" + bytes.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(bytes, response.getOutputStream());
    }


    /**
     * 自动分配群
     */
    @RequestMapping("autoAssignGroups")
    @RequiresPermissions("ltt:cdmaterial:save")
    public R autoAssignGroups(@RequestBody AutoAssignGroupsDTO cdMaterial){
        cdMaterialService.autoAssignGroups(cdMaterial);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdmaterial:info")
    public R info(@PathVariable("id") Integer id){
		CdMaterialVO cdMaterial = cdMaterialService.getById(id);

        return R.ok().put("cdMaterial", cdMaterial);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdmaterial:save")
    public R save(@RequestBody CdMaterialDTO cdMaterial){
		cdMaterialService.save(cdMaterial);
        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/reallocateToken")
    @RequiresPermissions("ltt:cdmaterial:save")
    public R reallocateToken(@RequestBody Integer[] ids){
        cdMaterialService.reallocateToken(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/exportSy")
    @RequiresPermissions("ltt:cdmaterial:save")
    public void exportSy(CdMaterialDTO cdMaterial, HttpServletResponse response) throws IOException {
        byte[] bytes = cdMaterialService.exportSy(cdMaterial);
        response.reset();
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.txt\"",java.net.URLEncoder.encode("导出手机号","UTF-8")));
        response.addHeader("Content-Length", "" + bytes.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(bytes, response.getOutputStream());
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdmaterial:update")
    public R update(@RequestBody CdMaterialDTO cdMaterial){
		cdMaterialService.updateById(cdMaterial);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdmaterial:delete")
    public R delete(@RequestBody Integer[] ids){
		cdMaterialService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
