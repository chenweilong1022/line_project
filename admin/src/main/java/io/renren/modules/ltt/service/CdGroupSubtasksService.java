package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdGroupSubtasksDTO;
import io.renren.modules.ltt.vo.CdGroupSubtasksVO;
import io.renren.modules.ltt.entity.CdGroupSubtasksEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-02 19:02:29
 */
public interface CdGroupSubtasksService extends IService<CdGroupSubtasksEntity> {

    /**
     * 分页查询
     * @param cdGroupSubtasks
     * @return
     */
    PageUtils queryPage(CdGroupSubtasksDTO cdGroupSubtasks);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdGroupSubtasksVO getById(Integer id);
    /**
     * 保存
     * @param cdGroupSubtasks
     * @return
     */
    boolean save(CdGroupSubtasksDTO cdGroupSubtasks);
    /**
     * 根据id修改
     * @param cdGroupSubtasks
     * @return
     */
    boolean updateById(CdGroupSubtasksDTO cdGroupSubtasks);
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

