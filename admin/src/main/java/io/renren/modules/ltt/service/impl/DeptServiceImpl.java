package io.renren.modules.ltt.service.impl;

import io.renren.datasources.annotation.Game;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.DeptDao;
import io.renren.modules.ltt.entity.DeptEntity;
import io.renren.modules.ltt.dto.DeptDTO;
import io.renren.modules.ltt.vo.DeptVO;
import io.renren.modules.ltt.service.DeptService;
import io.renren.modules.ltt.conver.DeptConver;

import java.io.Serializable;
import java.util.Collection;


@Service("deptService")
@Game
public class DeptServiceImpl extends ServiceImpl<DeptDao, DeptEntity> implements DeptService {

    @Override
    public PageUtils<DeptVO> queryPage(DeptDTO dept) {
        IPage<DeptEntity> page = baseMapper.selectPage(
                new Query<DeptEntity>(dept).getPage(),
                new QueryWrapper<DeptEntity>()
        );

        return PageUtils.<DeptVO>page(page).setList(DeptConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public DeptVO getById(Integer deptid) {
        return DeptConver.MAPPER.conver(baseMapper.selectById(deptid));
    }

    @Override
    public boolean save(DeptDTO dept) {
        DeptEntity deptEntity = DeptConver.MAPPER.converDTO(dept);
        return this.save(deptEntity);
    }

    @Override
    public boolean updateById(DeptDTO dept) {
        DeptEntity deptEntity = DeptConver.MAPPER.converDTO(dept);
        return this.updateById(deptEntity);
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
