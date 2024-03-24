package io.renren.modules.ltt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.ltt.dto.AutoAssignGroupsDTO;
import io.renren.modules.ltt.dto.CdMaterialDTO;
import io.renren.modules.ltt.dto.ImportZipDTO;
import io.renren.modules.ltt.vo.CdMaterialVO;
import io.renren.modules.ltt.entity.CdMaterialEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;


/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-12 12:30:34
 */
public interface CdMaterialService extends IService<CdMaterialEntity> {

    /**
     * 分页查询
     * @param cdMaterial
     * @return
     */
    PageUtils queryPage(CdMaterialDTO cdMaterial);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    CdMaterialVO getById(Integer id);

    @Transactional(rollbackFor = Exception.class)
    byte[] exportSy(CdMaterialDTO cdMaterial);

    /**
     * 保存
     * @param cdMaterial
     * @return
     */
    boolean save(CdMaterialDTO cdMaterial);
    /**
     * 根据id修改
     * @param cdMaterial
     * @return
     */
    boolean updateById(CdMaterialDTO cdMaterial);
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
     * 自动分组
     * @param cdMaterial
     */
    void autoAssignGroups(AutoAssignGroupsDTO cdMaterial);

    /**
     * 导出zip
     * @param importZipDTO
     */
    byte[] importZip(ImportZipDTO importZipDTO);
}

