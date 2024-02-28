package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdLineRegisterDTO;
import io.renren.modules.ltt.vo.CdLineRegisterVO;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-11-30 22:33:44
 */
public interface CdLineRegisterService extends IService<CdLineRegisterEntity> {

    /**
     * 分页查询
     * @param cdLineRegister
     * @return
     */
    PageUtils queryPage(CdLineRegisterDTO cdLineRegister);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdLineRegisterVO getById(Integer id);
    /**
     * 保存
     * @param cdLineRegister
     * @return
     */
    boolean save(CdLineRegisterDTO cdLineRegister);
    /**
     * 根据id修改
     * @param cdLineRegister
     * @return
     */
    boolean updateById(CdLineRegisterDTO cdLineRegister);
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
     * 根据taskid查询
     * @param cdLineRegister
     * @return
     */
    PageUtils listByTaskId(CdLineRegisterDTO cdLineRegister);

    void issueLiffView(List<Integer> list);

    /**
     * 导出token
     * @param list
     * @return
     */
    byte[] importToken(List<Integer> list);

    /**
     * 代理清空
     */
    void clearProxy();

    /**
     * 账号锁了
     * @param cdLineRegister
     * @return
     */
    boolean unLock(Integer id);
}

