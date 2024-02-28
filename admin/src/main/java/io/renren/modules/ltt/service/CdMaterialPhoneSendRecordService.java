package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdMaterialPhoneSendRecordDTO;
import io.renren.modules.ltt.vo.CdMaterialPhoneSendRecordVO;
import io.renren.modules.ltt.entity.CdMaterialPhoneSendRecordEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2024-01-29 22:59:57
 */
public interface CdMaterialPhoneSendRecordService extends IService<CdMaterialPhoneSendRecordEntity> {

    /**
     * 分页查询
     * @param cdMaterialPhoneSendRecord
     * @return
     */
    PageUtils queryPage(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdMaterialPhoneSendRecordVO getById(Integer id);
    /**
     * 保存
     * @param cdMaterialPhoneSendRecord
     * @return
     */
    boolean save(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord);
    /**
     * 根据id修改
     * @param cdMaterialPhoneSendRecord
     * @return
     */
    boolean updateById(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecord);
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
}

