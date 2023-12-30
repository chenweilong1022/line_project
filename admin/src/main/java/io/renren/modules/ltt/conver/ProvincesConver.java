package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.ProvincesDTO;
import io.renren.modules.ltt.entity.ProvincesEntity;
import io.renren.modules.ltt.vo.ProvincesVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface ProvincesConver {

    ProvincesConver MAPPER =  Mappers.getMapper(ProvincesConver.class);

    ProvincesEntity converDTO(ProvincesDTO provincesDTO);

    List<ProvincesEntity> converDTO(List<ProvincesDTO> provincesDTOs);

    ProvincesVO conver(ProvincesEntity provincesEntities);

    List<ProvincesVO> conver(List<ProvincesEntity> provincesEntities);

}
