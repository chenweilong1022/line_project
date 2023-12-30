package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdStaticProxyDTO;
import io.renren.modules.ltt.entity.CdStaticProxyEntity;
import io.renren.modules.ltt.vo.CdStaticProxyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdStaticProxyConver {

    CdStaticProxyConver MAPPER =  Mappers.getMapper(CdStaticProxyConver.class);

    CdStaticProxyEntity converDTO(CdStaticProxyDTO cdStaticProxyDTO);

    List<CdStaticProxyEntity> converDTO(List<CdStaticProxyDTO> cdStaticProxyDTOs);

    CdStaticProxyVO conver(CdStaticProxyEntity cdStaticProxyEntities);

    List<CdStaticProxyVO> conver(List<CdStaticProxyEntity> cdStaticProxyEntities);

}
