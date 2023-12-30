package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.DeptDTO;
import io.renren.modules.ltt.vo.DeptVO;
import io.renren.modules.ltt.entity.DeptEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-06-08 11:34:05
 */
public interface DeptService extends IService<DeptEntity> {

    /**
     * 分页查询
     * @param dept
     * @return
     */
    PageUtils queryPage(DeptDTO dept);
    /**
     * 根据id查询
     * @param deptid
     * @return
     */
    DeptVO getById(Integer deptid);
    /**
     * 保存
     * @param dept
     * @return
     */
    boolean save(DeptDTO dept);
    /**
     * 根据id修改
     * @param dept
     * @return
     */
    boolean updateById(DeptDTO dept);
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

