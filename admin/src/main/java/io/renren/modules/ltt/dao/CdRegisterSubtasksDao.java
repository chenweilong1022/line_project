package io.renren.modules.ltt.dao;

import io.renren.modules.ltt.entity.CdRegisterSubtasksEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-12-02 00:31:50
 */
@Mapper
public interface CdRegisterSubtasksDao extends BaseMapper<CdRegisterSubtasksEntity> {

    List<CdRegisterSubtasksEntity> groupByTaskId();
}
