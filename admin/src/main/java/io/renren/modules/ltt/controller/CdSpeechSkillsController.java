package io.renren.modules.ltt.controller;

import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.ltt.dto.CdSpeechSkillsDTO;
import io.renren.modules.ltt.vo.CdSpeechSkillsVO;
import io.renren.modules.ltt.service.CdSpeechSkillsService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2024-01-29 17:14:58
 */
@RestController
@RequestMapping("ltt/cdspeechskills")
public class CdSpeechSkillsController {
    @Autowired
    private CdSpeechSkillsService cdSpeechSkillsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ltt:cdspeechskills:list")
    public R list(CdSpeechSkillsDTO cdSpeechSkills){
        PageUtils page = cdSpeechSkillsService.queryPage(cdSpeechSkills);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ltt:cdspeechskills:info")
    public R info(@PathVariable("id") Integer id){
		CdSpeechSkillsVO cdSpeechSkills = cdSpeechSkillsService.getById(id);

        return R.ok().put("cdSpeechSkills", cdSpeechSkills);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ltt:cdspeechskills:save")
    public R save(@RequestBody CdSpeechSkillsDTO cdSpeechSkills){
		cdSpeechSkillsService.save(cdSpeechSkills);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ltt:cdspeechskills:update")
    public R update(@RequestBody CdSpeechSkillsDTO cdSpeechSkills){
		cdSpeechSkillsService.updateById(cdSpeechSkills);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ltt:cdspeechskills:delete")
    public R delete(@RequestBody Integer[] ids){
		cdSpeechSkillsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
