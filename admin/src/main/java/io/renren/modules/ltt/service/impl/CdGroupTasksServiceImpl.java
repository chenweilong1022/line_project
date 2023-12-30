package io.renren.modules.ltt.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpUtil;
import io.renren.common.validator.Assert;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.entity.CdGroupSubtasksEntity;
import io.renren.modules.ltt.enums.AddStatus;
import io.renren.modules.ltt.enums.GroupStatus;
import io.renren.modules.ltt.enums.GroupSubTasksStatus;
import io.renren.modules.ltt.enums.SearchStatus;
import io.renren.modules.ltt.service.CdGroupSubtasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdGroupTasksDao;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;
import io.renren.modules.ltt.dto.CdGroupTasksDTO;
import io.renren.modules.ltt.vo.CdGroupTasksVO;
import io.renren.modules.ltt.service.CdGroupTasksService;
import io.renren.modules.ltt.conver.CdGroupTasksConver;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service("cdGroupTasksService")
@Game
public class CdGroupTasksServiceImpl extends ServiceImpl<CdGroupTasksDao, CdGroupTasksEntity> implements CdGroupTasksService {

    @Autowired
    private CdGroupSubtasksService cdGroupSubtasksService;

    @Override
    public PageUtils<CdGroupTasksVO> queryPage(CdGroupTasksDTO cdGroupTasks) {
        IPage<CdGroupTasksEntity> page = baseMapper.selectPage(
                new Query<CdGroupTasksEntity>(cdGroupTasks).getPage(),
                new QueryWrapper<CdGroupTasksEntity>()
        );

        return PageUtils.<CdGroupTasksVO>page(page).setList(CdGroupTasksConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdGroupTasksVO getById(Integer id) {
        return CdGroupTasksConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CdGroupTasksDTO cdGroupTasks) {
        String s = HttpUtil.downloadString(cdGroupTasks.getTxtUrl(), "UTF-8");
        String[] split = s.split("\n");
        Assert.isTrue(ArrayUtil.isEmpty(split),"txt不能为空");
        Assert.isTrue(split.length > 95,"拉群数量不能超过96");
        cdGroupTasks.setUploadGroupNumber(split.length);
        cdGroupTasks.setSuccessfullyAttractGroupsNumber(0);
        cdGroupTasks.setCurrentExecutionsNumber(0);
        cdGroupTasks.setGroupStatus(GroupStatus.GroupStatus1.getKey());
        CdGroupTasksEntity cdGroupTasksEntity = CdGroupTasksConver.MAPPER.converDTO(cdGroupTasks);
        boolean save = this.save(cdGroupTasksEntity);
        DateTime date = DateUtil.date();
        List<CdGroupSubtasksEntity> cdGroupSubtasksEntities = new ArrayList<>();
        for (String string : split) {
            string = string.replace("\r","").trim();
            CdGroupSubtasksEntity cdGroupSubtasksEntity = new CdGroupSubtasksEntity();
            cdGroupSubtasksEntity.setGroupTasksId(cdGroupTasksEntity.getId());
            cdGroupSubtasksEntity.setPhone(string);
            cdGroupSubtasksEntity.setExecutionTime(date);
            cdGroupSubtasksEntity.setAddStatus(AddStatus.AddStatus1.getKey());
            cdGroupSubtasksEntity.setSearchStatus(SearchStatus.SearchStatus1.getKey());
            cdGroupSubtasksEntity.setSubtaskStatus(GroupSubTasksStatus.GroupStatus1.getKey());
            cdGroupSubtasksEntities.add(cdGroupSubtasksEntity);
            date = DateUtil.offsetMinute(date,cdGroupTasksEntity.getIntervals());
        }
        cdGroupSubtasksService.saveBatch(cdGroupSubtasksEntities);
        return save;
    }

    @Override
    public boolean updateById(CdGroupTasksDTO cdGroupTasks) {
        CdGroupTasksEntity cdGroupTasksEntity = CdGroupTasksConver.MAPPER.converDTO(cdGroupTasks);
        return this.updateById(cdGroupTasksEntity);
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
