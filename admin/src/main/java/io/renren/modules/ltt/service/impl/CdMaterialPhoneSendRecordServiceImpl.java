package io.renren.modules.ltt.service.impl;

import io.renren.datasources.annotation.Game;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdMaterialPhoneSendRecordDao;
import io.renren.modules.ltt.entity.CdMaterialPhoneSendRecordEntity;
import io.renren.modules.ltt.dto.CdMaterialPhoneSendRecordDTO;
import io.renren.modules.ltt.vo.CdMaterialPhoneSendRecordVO;
import io.renren.modules.ltt.service.CdMaterialPhoneSendRecordService;
import io.renren.modules.ltt.conver.CdMaterialPhoneSendRecordConver;

import java.io.Serializable;
import java.util.Collection;


@Service("cdMaterialPhoneSendRecordService")
@Game
public class CdMaterialPhoneSendRecordServiceImpl extends ServiceImpl<CdMaterialPhoneSendRecordDao, CdMaterialPhoneSendRecordEntity> implements CdMaterialPhoneSendRecordService {

    @Override
    public PageUtils<CdMaterialPhoneSendRecordVO> queryPage(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord) {
        IPage<CdMaterialPhoneSendRecordEntity> page = baseMapper.selectPage(
                new Query<CdMaterialPhoneSendRecordEntity>(cdMaterialPhoneSendRecord).getPage(),
                new QueryWrapper<CdMaterialPhoneSendRecordEntity>()
        );

        return PageUtils.<CdMaterialPhoneSendRecordVO>page(page).setList(CdMaterialPhoneSendRecordConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdMaterialPhoneSendRecordVO getById(Integer id) {
        return CdMaterialPhoneSendRecordConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord) {
        CdMaterialPhoneSendRecordEntity cdMaterialPhoneSendRecordEntity = CdMaterialPhoneSendRecordConver.MAPPER.converDTO(cdMaterialPhoneSendRecord);
        return this.save(cdMaterialPhoneSendRecordEntity);
    }

    @Override
    public boolean updateById(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord) {
        CdMaterialPhoneSendRecordEntity cdMaterialPhoneSendRecordEntity = CdMaterialPhoneSendRecordConver.MAPPER.converDTO(cdMaterialPhoneSendRecord);
        return this.updateById(cdMaterialPhoneSendRecordEntity);
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
