package io.renren.modules.ltt.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.ltt.dto.CdMaterialDTO;
import io.renren.modules.ltt.entity.CdMaterialEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.ltt.vo.CdMaterialVO;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-12 12:30:34
 */
@Mapper
public interface CdMaterialDao extends BaseMapper<CdMaterialEntity> {

    IPage<CdMaterialVO> listPage(Page<CdMaterialEntity> page, CdMaterialDTO cdMaterial);
}
