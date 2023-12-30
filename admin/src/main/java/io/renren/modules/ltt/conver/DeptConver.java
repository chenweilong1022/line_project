package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.DeptDTO;
import io.renren.modules.ltt.entity.DeptEntity;
import io.renren.modules.ltt.vo.DeptVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface DeptConver {

    DeptConver MAPPER =  Mappers.getMapper(DeptConver.class);

    DeptEntity converDTO(DeptDTO deptDTO);

    List<DeptEntity> converDTO(List<DeptDTO> deptDTOs);

    DeptVO conver(DeptEntity deptEntities);

    List<DeptVO> conver(List<DeptEntity> deptEntities);

}
