package io.renren.modules.ltt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.renren.common.utils.ConfigConstant;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.dto.*;
import io.renren.modules.ltt.enums.DeleteFlag;
import io.renren.modules.ltt.enums.ProxyStatus;
import io.renren.modules.ltt.service.CdLineErrLogsService;
import io.renren.modules.ltt.service.LineService;
import io.renren.modules.ltt.vo.*;
import io.renren.modules.sys.entity.ProjectWorkEntity;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 21:57
 */
@Service("lineServiceImpl")
@Game
@Slf4j
public class LineServiceImpl implements LineService {


    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private CdLineErrLogsService cdLineErrLogsService;
    @Resource(name = "caffeineCacheProjectWorkEntity")
    private Cache<String, ProjectWorkEntity> caffeineCacheProjectWorkEntity;

    @Override
    public LineRegisterVO lineRegister(LineRegisterDTO lineRegisterDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/lineRegister",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(lineRegisterDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            LineRegisterVO lineRegisterVO = JSON.parseObject(resp, LineRegisterVO.class);

            extracted(jsonStr, resp);
            return lineRegisterVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public LineRegisterVO issueLiffView(IssueLiffViewDTO issueLiffViewDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/issueLiffView",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(issueLiffViewDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            RegisterResultVO registerResultVO = JSON.parseObject(resp, RegisterResultVO.class);
            extracted(jsonStr, resp);
//            return registerResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public RefreshAccessTokenVO refreshAccessToken(RefreshAccessTokenDTO issueLiffViewDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/refreshAccessToken",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(issueLiffViewDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr,20000);
            RefreshAccessTokenVO registerResultVO = JSON.parseObject(resp, RefreshAccessTokenVO.class);
            extracted(jsonStr, resp);
            return registerResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    private void extracted(String jsonStr, String resp) {
        CdLineErrLogsDTO cdLineErrLogsDTO = new CdLineErrLogsDTO();
        cdLineErrLogsDTO.setParams(jsonStr);
        cdLineErrLogsDTO.setResults(resp);
        cdLineErrLogsDTO.setCreateTime(DateUtil.date());
        cdLineErrLogsDTO.setDeleteFlag(DeleteFlag.NO.getKey());
        cdLineErrLogsService.save(cdLineErrLogsDTO);
        log.info("param = {},resp = {}", jsonStr, resp);
    }

    @Override
    public RegisterResultVO registerResult(RegisterResultDTO registerResultDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/registerResult",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(registerResultDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            RegisterResultVO registerResultVO = JSON.parseObject(resp, RegisterResultVO.class);

            extracted(jsonStr, resp);
            return registerResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }


    @Override
    public SMSCodeVO smsCode(SMSCodeDTO smsCodeDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/smsCode",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(smsCodeDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            SMSCodeVO smsCodeVO = JSON.parseObject(resp, SMSCodeVO.class);

            extracted(jsonStr, resp);
            return smsCodeVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public SyncLineTokenVO SyncLineTokenDTO(SyncLineTokenDTO syncLineTokenDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/syncLineToken",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(syncLineTokenDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            SyncLineTokenVO syncLineTokenVO = JSON.parseObject(resp, SyncLineTokenVO.class);

            extracted(jsonStr, resp);
            return syncLineTokenVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public SearchPhoneVO searchPhone(SearchPhoneDTO searchPhoneDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/searchPhone",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(searchPhoneDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            SearchPhoneVO searchPhoneVO = JSON.parseObject(resp, SearchPhoneVO.class);

            extracted(jsonStr, resp);
            return searchPhoneVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public LineRegisterVO createGroupMax(CreateGroupMax createGroupMax) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/createGroupMax",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(createGroupMax);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr,20000);
            LineRegisterVO lineRegisterVO = JSON.parseObject(resp, LineRegisterVO.class);

            extracted(jsonStr, resp);
            return lineRegisterVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public SearchPhoneVO findAndAddContactsByPhone(SearchPhoneDTO searchPhoneDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/findAndAddContactsByPhone",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(searchPhoneDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            SearchPhoneVO searchPhoneVO = JSON.parseObject(resp, SearchPhoneVO.class);

            extracted(jsonStr, resp);
            return searchPhoneVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public SearchPhoneVO addFriendsByMid(AddFriendsByMid addFriendsByMid) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/addFriendsByMid",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(addFriendsByMid);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            SearchPhoneVO searchPhoneVO = JSON.parseObject(resp, SearchPhoneVO.class);

            extracted(jsonStr, resp);
            return searchPhoneVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public CreateGroupResultVO createGroupResult(RegisterResultDTO registerResultDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/createGroupResult",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(registerResultDTO);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            CreateGroupResultVO createGroupResultVO = JSON.parseObject(resp, CreateGroupResultVO.class);

            extracted(jsonStr, resp);
            return createGroupResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public LineRegisterVO openApp(OpenApp openApp) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/openApp",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(openApp);
            String resp = HttpUtil.post(getPhoneHttp, jsonStr);
            LineRegisterVO lineRegisterVO = JSON.parseObject(resp, LineRegisterVO.class);

            extracted(jsonStr, resp);
            return lineRegisterVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public OpenAppResult openAppResult(RegisterResultDTO registerResultDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/account/openAppResult",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(registerResultDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);
            OpenAppResult openAppResult = JSON.parseObject(resp, OpenAppResult.class);
            if (!resp.contains("正在运行")) {
                extracted(jsonStr, resp);
            }
            return openAppResult;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public LineRegisterVO syncContents(SyncContentsDTO syncContentsDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/syncContents",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(syncContentsDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);

            LineRegisterVO lineRegisterVO = JSON.parseObject(resp, LineRegisterVO.class);
            extracted(jsonStr, resp);
            return lineRegisterVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public SyncContentsResultVO syncContentsResult(SyncContentsResultDTO syncContentsResultDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/syncContentsResult",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(syncContentsResultDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);
            log.info("syncContentsResult resp = {}",resp);
            SyncContentsResultVO syncContentsResultVO = JSON.parseObject(resp, SyncContentsResultVO.class);
//            SyncContentsResultVO syncContentsResultVO1 = new SyncContentsResultVO();
//            syncContentsResultVO1.setCode(syncContentsResultVO.getCode());
//            syncContentsResultVO1.setMsg(syncContentsResultVO.getMsg());
//            extracted(jsonStr, JSONUtil.toJsonStr(syncContentsResultVO1));
            return syncContentsResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public GetChatsVO getChats(GetChatsDTO getChatsDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/work/getChats",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(getChatsDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr);

            GetChatsVO getChatsVO = JSON.parseObject(resp, GetChatsVO.class);
            extracted(jsonStr, resp);
            return getChatsVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public RegisterResultVO createThread(CreateThreadDTO createThreadDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/msg/createThread",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(createThreadDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);

            RegisterResultVO syncContentsResultVO = JSON.parseObject(resp, RegisterResultVO.class);
            extracted(jsonStr, resp);
            return syncContentsResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public CreateThreadResultVO createThreadResult(RegisterResultDTO registerResultDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/msg/createThreadResult",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(registerResultDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);

            CreateThreadResultVO syncContentsResultVO = JSON.parseObject(resp, CreateThreadResultVO.class);
            extracted(jsonStr, resp);
            return syncContentsResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public ShareTextMsgVO shareTextMsg(ShareTextMsgDTO shareTextMsgDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/msg/shareTextMsg",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(shareTextMsgDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);

            ShareTextMsgVO syncContentsResultVO = JSON.parseObject(resp, ShareTextMsgVO.class);
            extracted(jsonStr, resp);
            return syncContentsResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
            return new ShareTextMsgVO().setCode(201);
        }
    }

    @Override
    public EncryptedAccessTokenVO encryptedAccessToken(EncryptedAccessTokenDTO dto) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/msg/encryptedAccessToken",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(dto);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);

            EncryptedAccessTokenVO syncContentsResultVO = JSON.parseObject(resp, EncryptedAccessTokenVO.class);
            extracted(jsonStr, resp);
            return syncContentsResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }
        return null;
    }

    @Override
    public ShareImgMsgVO shareImgMsg(ShareImgMsgDTO shareImgMsgDTO) {
        try {
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            String getPhoneHttp = String.format("%s/api/v1/msg/shareImgMsg",projectWorkEntity.getLineBaseHttp());
            String jsonStr = JSONUtil.toJsonStr(shareImgMsgDTO);
            String resp = HttpUtil.post(getPhoneHttp,jsonStr,20000);

            shareImgMsgDTO.setImgData("");
            String jsonStr1 = JSONUtil.toJsonStr(shareImgMsgDTO);
            ShareImgMsgVO syncContentsResultVO = JSON.parseObject(resp, ShareImgMsgVO.class);
            extracted(jsonStr1, resp);
            return syncContentsResultVO;
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
            return new ShareImgMsgVO().setCode(201);
        }
    }

    @EventListener
    @Order(value = 9999)//tha-1027-61697		tha-1027-44108
    public void handlerApplicationReadyEvent(ApplicationReadyEvent event) {
        SysConfigEntity one = sysConfigService.getOne(new QueryWrapper<SysConfigEntity>().lambda()
                .eq(SysConfigEntity::getParamKey, ConfigConstant.PROJECT_WORK_KEY)
        );
        if (ObjectUtil.isNull(one)) {
            SysConfigEntity config = new SysConfigEntity();
            config.setLineBaseHttp("http://137.184.112.207:22117");
            config.setFirefoxBaseUrl("http://www.firefox.fun");
            config.setFirefoxToken("721713f744ee967b493bfb0362afcee2_46759");
            config.setFirefoxIid("1027");
            config.setFirefoxCountry("tha");
            config.setProxy(ProxyStatus.ProxyStatus1.getKey());
            config.setProxyUseCount(3);
            config.setFirefoxCountry1("th");

            config.setLineAb("2023.1127.1821");
            config.setLineAppVersion("13.19.1");
            config.setLineTxtToken("81f9933e3a434a1aaf7af09893937fd0");
            sysConfigService.save(config);
        }else {
            ProjectWorkEntity bean = JSONUtil.toBean(one.getParamValue(), ProjectWorkEntity.class);
            if (ObjectUtil.isNotNull(bean)) {
                SysConfigEntity config = new SysConfigEntity();
                config.setId(one.getId());
                config.setLineBaseHttp(bean.getLineBaseHttp());
                config.setFirefoxBaseUrl(bean.getFirefoxBaseUrl());
                config.setFirefoxToken(bean.getFirefoxToken());
                config.setFirefoxIid(bean.getFirefoxIid());
                config.setFirefoxCountry(bean.getFirefoxCountry());
                config.setProxy(bean.getProxy());
                config.setProxyUseCount(bean.getProxyUseCount());
                config.setFirefoxCountry1(bean.getFirefoxCountry1());

                config.setLineAb(bean.getLineAb());
                config.setLineAppVersion(bean.getLineAppVersion());
                config.setLineTxtToken(bean.getLineTxtToken());

                sysConfigService.update(config);
            }
        }
    }
}
