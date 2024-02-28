package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdSpeechSkillsDTO;
import io.renren.modules.ltt.vo.CdSpeechSkillsVO;
import io.renren.modules.ltt.entity.CdSpeechSkillsEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2024-01-29 17:14:58
 */
public interface CdSpeechSkillsService extends IService<CdSpeechSkillsEntity> {

    /**
     * 分页查询
     * @param cdSpeechSkills
     * @return
     */
    PageUtils queryPage(CdSpeechSkillsDTO cdSpeechSkills);
    /**
     * 排序查询
     * @param cdSpeechSkills
     * @return
     */
    List<CdSpeechSkillsVO> orderByList(CdSpeechSkillsDTO cdSpeechSkills);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdSpeechSkillsVO getById(Integer id);
    /**
     * 保存
     * @param cdSpeechSkills
     * @return
     */
    boolean save(CdSpeechSkillsDTO cdSpeechSkills);
    /**
     * 根据id修改
     * @param cdSpeechSkills
     * @return
     */
    boolean updateById(CdSpeechSkillsDTO cdSpeechSkills);
    /**
     * 根据id删除
     * @param id
     * @return
     */
    @Override
    boolean removeById(Serializable id);

    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    @Override
    boolean removeByIds(Collection<? extends Serializable> ids);
}

