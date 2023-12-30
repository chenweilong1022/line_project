package io.renren.modules.ltt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.enums.DeleteFlag;
import io.renren.modules.ltt.enums.PhoneStatus;
import io.renren.modules.ltt.service.FirefoxService;
import io.renren.modules.ltt.vo.GetPhoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdGetPhoneDao;
import io.renren.modules.ltt.entity.CdGetPhoneEntity;
import io.renren.modules.ltt.dto.CdGetPhoneDTO;
import io.renren.modules.ltt.vo.CdGetPhoneVO;
import io.renren.modules.ltt.service.CdGetPhoneService;
import io.renren.modules.ltt.conver.CdGetPhoneConver;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service("cdGetPhoneService")
@Game
public class CdGetPhoneServiceImpl extends ServiceImpl<CdGetPhoneDao, CdGetPhoneEntity> implements CdGetPhoneService {

    @Resource(name = "cardMeServiceImpl")
    private FirefoxService firefoxService;

    @Override
    public PageUtils<CdGetPhoneVO> queryPage(CdGetPhoneDTO cdGetPhone) {
        IPage<CdGetPhoneEntity> page = baseMapper.selectPage(
                new Query<CdGetPhoneEntity>(cdGetPhone).getPage(),
                new QueryWrapper<CdGetPhoneEntity>()
        );

        return PageUtils.<CdGetPhoneVO>page(page).setList(CdGetPhoneConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdGetPhoneVO getById(Integer id) {
        return CdGetPhoneConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdGetPhoneDTO cdGetPhone) {
        CdGetPhoneEntity cdGetPhoneEntity = CdGetPhoneConver.MAPPER.converDTO(cdGetPhone);
        return this.save(cdGetPhoneEntity);
    }

    @Override
    public boolean updateById(CdGetPhoneDTO cdGetPhone) {
        CdGetPhoneEntity cdGetPhoneEntity = CdGetPhoneConver.MAPPER.converDTO(cdGetPhone);
        return this.updateById(cdGetPhoneEntity);
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
    public List<CdGetPhoneEntity> addCount(CdGetPhoneDTO cdGetPhone) throws InterruptedException {
        Integer count = cdGetPhone.getCount();
        List<CdGetPhoneEntity> cdGetPhoneEntities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Thread.sleep(50);
            GetPhoneVo phone = firefoxService.getPhone();
            if (ObjectUtil.isNotNull(phone)) {
                CdGetPhoneEntity cdGetPhoneEntity = new CdGetPhoneEntity();
                cdGetPhoneEntity.setNumber(phone.getNumber());
                cdGetPhoneEntity.setPkey(phone.getPkey());
                cdGetPhoneEntity.setTime(phone.getTime());
                cdGetPhoneEntity.setCountry(phone.getCountry());
                cdGetPhoneEntity.setCountrycode(phone.getPhone());
                cdGetPhoneEntity.setOther(phone.getOther());
                cdGetPhoneEntity.setCom(phone.getCom());
                cdGetPhoneEntity.setPhone(phone.getPhone());
                cdGetPhoneEntity.setDeleteFlag(DeleteFlag.NO.getKey());
                cdGetPhoneEntity.setPhoneStatus(PhoneStatus.PhoneStatus1.getKey());
                cdGetPhoneEntity.setCreateTime(DateUtil.date());
                cdGetPhoneEntity.setSubtasksId(cdGetPhone.getSubtasksId());
                cdGetPhoneEntities.add(cdGetPhoneEntity);
            }
        }

        //如果获取了足够的数量 保存记录
        if (count == cdGetPhoneEntities.size()) {
            this.saveBatch(cdGetPhoneEntities);
        }
        return cdGetPhoneEntities;
    }


}
