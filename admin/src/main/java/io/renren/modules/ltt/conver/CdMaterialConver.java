package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdMaterialDTO;
import io.renren.modules.ltt.entity.CdMaterialEntity;
import io.renren.modules.ltt.vo.CdMaterialVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdMaterialConver {

    CdMaterialConver MAPPER =  Mappers.getMapper(CdMaterialConver.class);

    CdMaterialEntity converDTO(CdMaterialDTO cdMaterialDTO);

    List<CdMaterialEntity> converDTO(List<CdMaterialDTO> cdMaterialDTOs);

    CdMaterialVO conver(CdMaterialEntity cdMaterialEntities);

    List<CdMaterialVO> conver(List<CdMaterialEntity> cdMaterialEntities);

}
