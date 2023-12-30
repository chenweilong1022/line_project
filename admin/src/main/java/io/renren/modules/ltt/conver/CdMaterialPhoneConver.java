package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdMaterialPhoneDTO;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
import io.renren.modules.ltt.vo.CdMaterialPhoneVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdMaterialPhoneConver {

    CdMaterialPhoneConver MAPPER =  Mappers.getMapper(CdMaterialPhoneConver.class);

    CdMaterialPhoneEntity converDTO(CdMaterialPhoneDTO cdMaterialPhoneDTO);

    List<CdMaterialPhoneEntity> converDTO(List<CdMaterialPhoneDTO> cdMaterialPhoneDTOs);

    CdMaterialPhoneVO conver(CdMaterialPhoneEntity cdMaterialPhoneEntities);

    CdMaterialPhoneEntity conver1(CdMaterialPhoneEntity cdMaterialPhoneEntities);

    List<CdMaterialPhoneVO> conver(List<CdMaterialPhoneEntity> cdMaterialPhoneEntities);

}
