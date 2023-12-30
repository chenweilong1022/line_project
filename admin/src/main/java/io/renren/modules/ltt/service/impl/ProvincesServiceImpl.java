package io.renren.modules.ltt.service.impl;

import io.renren.datasources.annotation.Game;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.ProvincesDao;
import io.renren.modules.ltt.entity.ProvincesEntity;
import io.renren.modules.ltt.dto.ProvincesDTO;
import io.renren.modules.ltt.vo.ProvincesVO;
import io.renren.modules.ltt.service.ProvincesService;
import io.renren.modules.ltt.conver.ProvincesConver;

import java.io.Serializable;
import java.util.Collection;


@Service("provincesService")
@Game
public class ProvincesServiceImpl extends ServiceImpl<ProvincesDao, ProvincesEntity> implements ProvincesService {

    @Override
    public PageUtils<ProvincesVO> queryPage(ProvincesDTO provinces) {
        IPage<ProvincesEntity> page = baseMapper.selectPage(
                new Query<ProvincesEntity>(provinces).getPage(),
                new QueryWrapper<ProvincesEntity>()
        );

        return PageUtils.<ProvincesVO>page(page).setList(ProvincesConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public ProvincesVO getById(Integer id) {
        return ProvincesConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(ProvincesDTO provinces) {
        ProvincesEntity provincesEntity = ProvincesConver.MAPPER.converDTO(provinces);
        return this.save(provincesEntity);
    }

    @Override
    public boolean updateById(ProvincesDTO provinces) {
        ProvincesEntity provincesEntity = ProvincesConver.MAPPER.converDTO(provinces);
        return this.updateById(provincesEntity);
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
