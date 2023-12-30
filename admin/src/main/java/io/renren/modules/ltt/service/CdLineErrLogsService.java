package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdLineErrLogsDTO;
import io.renren.modules.ltt.vo.CdLineErrLogsVO;
import io.renren.modules.ltt.entity.CdLineErrLogsEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * 
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-05 19:11:08
 */
public interface CdLineErrLogsService extends IService<CdLineErrLogsEntity> {

    /**
     * 分页查询
     * @param cdLineErrLogs
     * @return
     */
    PageUtils queryPage(CdLineErrLogsDTO cdLineErrLogs);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdLineErrLogsVO getById(Integer id);
    /**
     * 保存
     * @param cdLineErrLogs
     * @return
     */
    boolean save(CdLineErrLogsDTO cdLineErrLogs);
    /**
     * 根据id修改
     * @param cdLineErrLogs
     * @return
     */
    boolean updateById(CdLineErrLogsDTO cdLineErrLogs);
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

