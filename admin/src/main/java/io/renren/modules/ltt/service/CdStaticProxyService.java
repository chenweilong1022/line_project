package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.CdStaticProxyDTO;
import io.renren.modules.ltt.vo.CdStaticProxyVO;
import io.renren.modules.ltt.entity.CdStaticProxyEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-13 22:33:36
 */
public interface CdStaticProxyService extends IService<CdStaticProxyEntity> {

    /**
     * 分页查询
     * @param cdStaticProxy
     * @return
     */
    PageUtils queryPage(CdStaticProxyDTO cdStaticProxy);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdStaticProxyVO getById(Integer id);
    /**
     * 保存
     * @param cdStaticProxy
     * @return
     */
    boolean save(CdStaticProxyDTO cdStaticProxy);
    /**
     * 根据id修改
     * @param cdStaticProxy
     * @return
     */
    boolean updateById(CdStaticProxyDTO cdStaticProxy);
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
     * 根据txt上传代理
     * @param cdStaticProxy
     */
    void saveBatchTxt(CdStaticProxyDTO cdStaticProxy);
}

