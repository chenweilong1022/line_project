package io.renren.modules.ltt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpUtil;
import io.renren.common.validator.Assert;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.enums.DeleteFlag;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdStaticProxyDao;
import io.renren.modules.ltt.entity.CdStaticProxyEntity;
import io.renren.modules.ltt.dto.CdStaticProxyDTO;
import io.renren.modules.ltt.vo.CdStaticProxyVO;
import io.renren.modules.ltt.service.CdStaticProxyService;
import io.renren.modules.ltt.conver.CdStaticProxyConver;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service("cdStaticProxyService")
@Game
public class CdStaticProxyServiceImpl extends ServiceImpl<CdStaticProxyDao, CdStaticProxyEntity> implements CdStaticProxyService {

    @Override
    public PageUtils<CdStaticProxyVO> queryPage(CdStaticProxyDTO cdStaticProxy) {
        IPage<CdStaticProxyEntity> page = baseMapper.selectPage(
                new Query<CdStaticProxyEntity>(cdStaticProxy).getPage(),
                new QueryWrapper<CdStaticProxyEntity>()
        );

        return PageUtils.<CdStaticProxyVO>page(page).setList(CdStaticProxyConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdStaticProxyVO getById(Integer id) {
        return CdStaticProxyConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdStaticProxyDTO cdStaticProxy) {
        cdStaticProxy.setCreateTime(DateUtil.date());
        cdStaticProxy.setDeleteFlag(DeleteFlag.NO.getKey());
        CdStaticProxyEntity cdStaticProxyEntity = CdStaticProxyConver.MAPPER.converDTO(cdStaticProxy);
        return this.save(cdStaticProxyEntity);
    }

    @Override
    public boolean updateById(CdStaticProxyDTO cdStaticProxy) {
        CdStaticProxyEntity cdStaticProxyEntity = CdStaticProxyConver.MAPPER.converDTO(cdStaticProxy);
        return this.updateById(cdStaticProxyEntity);
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
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchTxt(CdStaticProxyDTO cdStaticProxy) {
        String s = HttpUtil.downloadString(cdStaticProxy.getTxtUrl(), "UTF-8");
        String[] split = s.split("\n");
        Assert.isTrue(ArrayUtil.isEmpty(split),"txt不能为空");
        List<CdStaticProxyEntity> cdStaticProxyEntities = new ArrayList<>();
        for (String string : split) {
            String[] split1 = string.split("#");
            String u = split1[0];
            String p = split1[1];
            String ip = split1[2];
            CdStaticProxyEntity cdStaticProxyEntity = new CdStaticProxyEntity();
            cdStaticProxyEntity.setUsername(u);
            cdStaticProxyEntity.setPassword(p);
            cdStaticProxyEntity.setIpProxy(ip);
            cdStaticProxyEntity.setUseCount(0);
            cdStaticProxyEntity.setDeleteFlag(DeleteFlag.NO.getKey());
            cdStaticProxyEntity.setCreateTime(DateUtil.date());
            cdStaticProxyEntities.add(cdStaticProxyEntity);
        }
        this.saveBatch(cdStaticProxyEntities);
    }

}
