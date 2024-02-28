package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdMaterialPhoneSendRecordDTO;
import io.renren.modules.ltt.entity.CdMaterialPhoneSendRecordEntity;
import io.renren.modules.ltt.vo.CdMaterialPhoneSendRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdMaterialPhoneSendRecordConver {

    CdMaterialPhoneSendRecordConver MAPPER =  Mappers.getMapper(CdMaterialPhoneSendRecordConver.class);

    CdMaterialPhoneSendRecordEntity converDTO(CdMaterialPhoneSendRecordDTO cdMaterialPhoneSendRecordDTO);

    List<CdMaterialPhoneSendRecordEntity> converDTO(List<CdMaterialPhoneSendRecordDTO> cdMaterialPhoneSendRecordDTOs);

    CdMaterialPhoneSendRecordVO conver(CdMaterialPhoneSendRecordEntity cdMaterialPhoneSendRecordEntities);

    List<CdMaterialPhoneSendRecordVO> conver(List<CdMaterialPhoneSendRecordEntity> cdMaterialPhoneSendRecordEntities);

}
