package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdGroupTasksDTO;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;
import io.renren.modules.ltt.vo.CdGroupTasksVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdGroupTasksConver {

    CdGroupTasksConver MAPPER =  Mappers.getMapper(CdGroupTasksConver.class);

    CdGroupTasksEntity converDTO(CdGroupTasksDTO cdGroupTasksDTO);

    List<CdGroupTasksEntity> converDTO(List<CdGroupTasksDTO> cdGroupTasksDTOs);

    CdGroupTasksVO conver(CdGroupTasksEntity cdGroupTasksEntities);

    List<CdGroupTasksVO> conver(List<CdGroupTasksEntity> cdGroupTasksEntities);

}
