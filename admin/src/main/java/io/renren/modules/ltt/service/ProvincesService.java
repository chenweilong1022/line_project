package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.ProvincesDTO;
import io.renren.modules.ltt.vo.ProvincesVO;
import io.renren.modules.ltt.entity.ProvincesEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * 省份信息表
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-06-08 11:35:41
 */
public interface ProvincesService extends IService<ProvincesEntity> {

    /**
     * 分页查询
     * @param provinces
     * @return
     */
    PageUtils queryPage(ProvincesDTO provinces);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    ProvincesVO getById(Integer id);
    /**
     * 保存
     * @param provinces
     * @return
     */
    boolean save(ProvincesDTO provinces);
    /**
     * 根据id修改
     * @param provinces
     * @return
     */
    boolean updateById(ProvincesDTO provinces);
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

