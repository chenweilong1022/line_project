package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdGroupTasksDTO;
import io.renren.modules.ltt.vo.CdGroupTasksVO;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-02 19:02:28
 */
public interface CdGroupTasksService extends IService<CdGroupTasksEntity> {

    /**
     * 分页查询
     * @param cdGroupTasks
     * @return
     */
    PageUtils queryPage(CdGroupTasksDTO cdGroupTasks);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdGroupTasksVO getById(Integer id);
    /**
     * 保存
     * @param cdGroupTasks
     * @return
     */
    boolean save(CdGroupTasksDTO cdGroupTasks);
    /**
     * 根据id修改
     * @param cdGroupTasks
     * @return
     */
    boolean updateById(CdGroupTasksDTO cdGroupTasks);
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

