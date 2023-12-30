package io.renren.modules.ltt.dao;

import io.renren.modules.ltt.entity.ProvincesEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 省份信息表
 * 
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-06-08 11:35:41
 */
@Mapper
public interface ProvincesDao extends BaseMapper<ProvincesEntity> {
	
}
