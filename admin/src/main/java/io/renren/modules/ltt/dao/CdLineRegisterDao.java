package io.renren.modules.ltt.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.ltt.dto.CdLineRegisterDTO;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.ltt.vo.CdLineRegisterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-11-30 22:33:44
 */
@Mapper
public interface CdLineRegisterDao extends BaseMapper<CdLineRegisterEntity> {

    IPage<CdLineRegisterVO> listPage(Page<CdLineRegisterEntity> page,@Param("dto") CdLineRegisterDTO cdLineRegister);
}
