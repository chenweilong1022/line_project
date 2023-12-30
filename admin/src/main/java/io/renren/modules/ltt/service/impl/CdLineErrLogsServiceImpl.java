package io.renren.modules.ltt.service.impl;

import io.renren.datasources.annotation.Game;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdLineErrLogsDao;
import io.renren.modules.ltt.entity.CdLineErrLogsEntity;
import io.renren.modules.ltt.dto.CdLineErrLogsDTO;
import io.renren.modules.ltt.vo.CdLineErrLogsVO;
import io.renren.modules.ltt.service.CdLineErrLogsService;
import io.renren.modules.ltt.conver.CdLineErrLogsConver;

import java.io.Serializable;
import java.util.Collection;


@Service("cdLineErrLogsService")
@Game
public class CdLineErrLogsServiceImpl extends ServiceImpl<CdLineErrLogsDao, CdLineErrLogsEntity> implements CdLineErrLogsService {

    @Override
    public PageUtils<CdLineErrLogsVO> queryPage(CdLineErrLogsDTO cdLineErrLogs) {
        IPage<CdLineErrLogsEntity> page = baseMapper.selectPage(
                new Query<CdLineErrLogsEntity>(cdLineErrLogs).getPage(),
                new QueryWrapper<CdLineErrLogsEntity>()
        );

        return PageUtils.<CdLineErrLogsVO>page(page).setList(CdLineErrLogsConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdLineErrLogsVO getById(Integer id) {
        return CdLineErrLogsConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdLineErrLogsDTO cdLineErrLogs) {
        CdLineErrLogsEntity cdLineErrLogsEntity = CdLineErrLogsConver.MAPPER.converDTO(cdLineErrLogs);
        return this.save(cdLineErrLogsEntity);
    }

    @Override
    public boolean updateById(CdLineErrLogsDTO cdLineErrLogs) {
        CdLineErrLogsEntity cdLineErrLogsEntity = CdLineErrLogsConver.MAPPER.converDTO(cdLineErrLogs);
        return this.updateById(cdLineErrLogsEntity);
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
