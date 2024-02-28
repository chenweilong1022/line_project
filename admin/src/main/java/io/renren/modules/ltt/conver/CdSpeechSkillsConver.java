package io.renren.modules.ltt.conver;

import io.renren.modules.ltt.dto.CdSpeechSkillsDTO;
import io.renren.modules.ltt.entity.CdSpeechSkillsEntity;
import io.renren.modules.ltt.vo.CdSpeechSkillsVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2020-02-07 12:36
 */
@Mapper
public interface CdSpeechSkillsConver {

    CdSpeechSkillsConver MAPPER =  Mappers.getMapper(CdSpeechSkillsConver.class);

    CdSpeechSkillsEntity converDTO(CdSpeechSkillsDTO cdSpeechSkillsDTO);

    List<CdSpeechSkillsEntity> converDTO(List<CdSpeechSkillsDTO> cdSpeechSkillsDTOs);

    CdSpeechSkillsVO conver(CdSpeechSkillsEntity cdSpeechSkillsEntities);

    List<CdSpeechSkillsVO> conver(List<CdSpeechSkillsEntity> cdSpeechSkillsEntities);

}
