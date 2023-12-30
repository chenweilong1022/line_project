package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdLineErrLogsDTO;
import io.renren.modules.ltt.entity.CdLineErrLogsEntity;
import io.renren.modules.ltt.vo.CdLineErrLogsVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdLineErrLogsConver {

    CdLineErrLogsConver MAPPER =  Mappers.getMapper(CdLineErrLogsConver.class);

    CdLineErrLogsEntity converDTO(CdLineErrLogsDTO cdLineErrLogsDTO);

    List<CdLineErrLogsEntity> converDTO(List<CdLineErrLogsDTO> cdLineErrLogsDTOs);

    CdLineErrLogsVO conver(CdLineErrLogsEntity cdLineErrLogsEntities);

    List<CdLineErrLogsVO> conver(List<CdLineErrLogsEntity> cdLineErrLogsEntities);

}
