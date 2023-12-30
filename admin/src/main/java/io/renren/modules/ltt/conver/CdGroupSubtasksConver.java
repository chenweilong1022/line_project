package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdGroupSubtasksDTO;
import io.renren.modules.ltt.entity.CdGroupSubtasksEntity;
import io.renren.modules.ltt.vo.CdGroupSubtasksVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdGroupSubtasksConver {

    CdGroupSubtasksConver MAPPER =  Mappers.getMapper(CdGroupSubtasksConver.class);

    CdGroupSubtasksEntity converDTO(CdGroupSubtasksDTO cdGroupSubtasksDTO);

    List<CdGroupSubtasksEntity> converDTO(List<CdGroupSubtasksDTO> cdGroupSubtasksDTOs);

    CdGroupSubtasksVO conver(CdGroupSubtasksEntity cdGroupSubtasksEntities);

    List<CdGroupSubtasksVO> conver(List<CdGroupSubtasksEntity> cdGroupSubtasksEntities);

}
