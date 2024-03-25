package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CanSendListByGroupTaskIdDTO;
import io.renren.modules.ltt.dto.CdMaterialPhoneDTO;
import io.renren.modules.ltt.vo.CanSendListByGroupTaskIdVO;
import io.renren.modules.ltt.vo.CdMaterialPhoneVO;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-12 12:30:34
 */
public interface CdMaterialPhoneService extends IService<CdMaterialPhoneEntity> {

    static final Object cdMaterialPhoneServiceLockObj = new Object();

    /**
     * 分页查询
     * @param cdMaterialPhone
     * @return
     */
    PageUtils queryPage(CdMaterialPhoneDTO cdMaterialPhone);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdMaterialPhoneVO getById(Integer id);
    /**
     * 保存
     * @param cdMaterialPhone
     * @return
     */
    boolean save(CdMaterialPhoneDTO cdMaterialPhone);
    /**
     * 根据id修改
     * @param cdMaterialPhone
     * @return
     */
    boolean updateById(CdMaterialPhoneDTO cdMaterialPhone);
    /**
     * 根据id删除
     * @param id
     * @return
     */
    @Override
    boolean removeById(Serializable id);

    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    @Override
    boolean removeByIds(Collection<? extends Serializable> ids);

    /**
     * 根据任务id分组可以发送消息的账号
     * @return
     */
    List<CanSendListByGroupTaskIdVO> canSendListByGroupTaskId(CanSendListByGroupTaskIdDTO dto);

    List<CdMaterialPhoneEntity> groupByIds(List<Integer> ids);
}

