package io.renren.modules.ltt.dao;

import io.renren.modules.ltt.dto.CanSendListByGroupTaskIdDTO;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.ltt.vo.CanSendListByGroupTaskIdVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-12 12:30:34
 */
@Mapper
public interface CdMaterialPhoneDao extends BaseMapper<CdMaterialPhoneEntity> {

    List<CanSendListByGroupTaskIdVO> canSendListByGroupTaskId(@Param("dto") CanSendListByGroupTaskIdDTO dto);

    List<CdMaterialPhoneEntity> groupByIds(@Param("ids") List<Integer> ids);
}
