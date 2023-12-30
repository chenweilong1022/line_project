package io.renren.modules.ltt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import io.renren.datasources.annotation.Game;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdGroupSubtasksDao;
import io.renren.modules.ltt.entity.CdGroupSubtasksEntity;
import io.renren.modules.ltt.dto.CdGroupSubtasksDTO;
import io.renren.modules.ltt.vo.CdGroupSubtasksVO;
import io.renren.modules.ltt.service.CdGroupSubtasksService;
import io.renren.modules.ltt.conver.CdGroupSubtasksConver;

import java.io.Serializable;
import java.util.Collection;


@Service("cdGroupSubtasksService")
@Game
public class CdGroupSubtasksServiceImpl extends ServiceImpl<CdGroupSubtasksDao, CdGroupSubtasksEntity> implements CdGroupSubtasksService {

    @Override
    public PageUtils<CdGroupSubtasksVO> queryPage(CdGroupSubtasksDTO cdGroupSubtasks) {
        IPage<CdGroupSubtasksEntity> page = baseMapper.selectPage(
                new Query<CdGroupSubtasksEntity>(cdGroupSubtasks).getPage(),
                new QueryWrapper<CdGroupSubtasksEntity>().lambda()
                        .eq(ObjectUtil.isNotNull(cdGroupSubtasks.getGroupTasksId()),CdGroupSubtasksEntity::getGroupTasksId,cdGroupSubtasks.getGroupTasksId())
                        .eq(ObjectUtil.isNotNull(cdGroupSubtasks.getSubtaskStatus()),CdGroupSubtasksEntity::getSubtaskStatus,cdGroupSubtasks.getSubtaskStatus())
        );

        return PageUtils.<CdGroupSubtasksVO>page(page).setList(CdGroupSubtasksConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdGroupSubtasksVO getById(Integer id) {
        return CdGroupSubtasksConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdGroupSubtasksDTO cdGroupSubtasks) {
        CdGroupSubtasksEntity cdGroupSubtasksEntity = CdGroupSubtasksConver.MAPPER.converDTO(cdGroupSubtasks);
        return this.save(cdGroupSubtasksEntity);
    }

    @Override
    public boolean updateById(CdGroupSubtasksDTO cdGroupSubtasks) {
        CdGroupSubtasksEntity cdGroupSubtasksEntity = CdGroupSubtasksConver.MAPPER.converDTO(cdGroupSubtasks);
        return this.updateById(cdGroupSubtasksEntity);
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
