package io.renren.modules.ltt.service.impl;

import io.renren.datasources.annotation.Game;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdSpeechSkillsDao;
import io.renren.modules.ltt.entity.CdSpeechSkillsEntity;
import io.renren.modules.ltt.dto.CdSpeechSkillsDTO;
import io.renren.modules.ltt.vo.CdSpeechSkillsVO;
import io.renren.modules.ltt.service.CdSpeechSkillsService;
import io.renren.modules.ltt.conver.CdSpeechSkillsConver;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@Service("cdSpeechSkillsService")
@Game
public class CdSpeechSkillsServiceImpl extends ServiceImpl<CdSpeechSkillsDao, CdSpeechSkillsEntity> implements CdSpeechSkillsService {

    @Override
    public PageUtils<CdSpeechSkillsVO> queryPage(CdSpeechSkillsDTO cdSpeechSkills) {
        IPage<CdSpeechSkillsEntity> page = baseMapper.selectPage(
                new Query<CdSpeechSkillsEntity>(cdSpeechSkills).getPage(),
                new QueryWrapper<CdSpeechSkillsEntity>().lambda().orderByDesc(CdSpeechSkillsEntity::getOrderIndex)
        );

        return PageUtils.<CdSpeechSkillsVO>page(page).setList(CdSpeechSkillsConver.MAPPER.conver(page.getRecords()));
    }

    @Override
    public List<CdSpeechSkillsVO> orderByList(CdSpeechSkillsDTO cdSpeechSkills) {
        List<CdSpeechSkillsEntity> list = this.list(new QueryWrapper<CdSpeechSkillsEntity>().lambda().orderByAsc(CdSpeechSkillsEntity::getOrderIndex));
        return CdSpeechSkillsConver.MAPPER.conver(list);
    }


    @Override
    public CdSpeechSkillsVO getById(Integer id) {
        return CdSpeechSkillsConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdSpeechSkillsDTO cdSpeechSkills) {
        CdSpeechSkillsEntity cdSpeechSkillsEntity = CdSpeechSkillsConver.MAPPER.converDTO(cdSpeechSkills);
        return this.save(cdSpeechSkillsEntity);
    }

    @Override
    public boolean updateById(CdSpeechSkillsDTO cdSpeechSkills) {
        CdSpeechSkillsEntity cdSpeechSkillsEntity = CdSpeechSkillsConver.MAPPER.converDTO(cdSpeechSkills);
        return this.updateById(cdSpeechSkillsEntity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

}
