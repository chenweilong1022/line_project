package io.renren.modules.ltt.service.impl;

import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.dto.CanSendListByGroupTaskIdDTO;
import io.renren.modules.ltt.vo.CanSendListByGroupTaskIdVO;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdMaterialPhoneDao;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
import io.renren.modules.ltt.dto.CdMaterialPhoneDTO;
import io.renren.modules.ltt.vo.CdMaterialPhoneVO;
import io.renren.modules.ltt.service.CdMaterialPhoneService;
import io.renren.modules.ltt.conver.CdMaterialPhoneConver;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@Service("cdMaterialPhoneService")
@Game
public class CdMaterialPhoneServiceImpl extends ServiceImpl<CdMaterialPhoneDao, CdMaterialPhoneEntity> implements CdMaterialPhoneService {

    @Override
    public PageUtils<CdMaterialPhoneVO> queryPage(CdMaterialPhoneDTO cdMaterialPhone) {
        IPage<CdMaterialPhoneEntity> page = baseMapper.selectPage(
                new Query<CdMaterialPhoneEntity>(cdMaterialPhone).getPage(),
                new QueryWrapper<CdMaterialPhoneEntity>()
        );

        return PageUtils.<CdMaterialPhoneVO>page(page).setList(CdMaterialPhoneConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdMaterialPhoneVO getById(Integer id) {
        return CdMaterialPhoneConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdMaterialPhoneDTO cdMaterialPhone) {
        CdMaterialPhoneEntity cdMaterialPhoneEntity = CdMaterialPhoneConver.MAPPER.converDTO(cdMaterialPhone);
        return this.save(cdMaterialPhoneEntity);
    }

    @Override
    public boolean updateById(CdMaterialPhoneDTO cdMaterialPhone) {
        CdMaterialPhoneEntity cdMaterialPhoneEntity = CdMaterialPhoneConver.MAPPER.converDTO(cdMaterialPhone);
        return this.updateById(cdMaterialPhoneEntity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    @Override
    public List<CanSendListByGroupTaskIdVO> canSendListByGroupTaskId(CanSendListByGroupTaskIdDTO dto) {
        return baseMapper.canSendListByGroupTaskId(dto);
    }

}
