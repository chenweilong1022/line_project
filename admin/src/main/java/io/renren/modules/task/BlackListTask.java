package io.renren.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.renren.common.utils.ConfigConstant;
import io.renren.modules.ltt.dto.*;
import io.renren.modules.ltt.entity.CdGetPhoneEntity;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import io.renren.modules.ltt.enums.AccountExistStatus;
import io.renren.modules.ltt.enums.DeleteFlag;
import io.renren.modules.ltt.enums.PhoneStatus;
import io.renren.modules.ltt.enums.RegisterStatus;
import io.renren.modules.ltt.service.*;
import io.renren.modules.ltt.vo.*;
import io.renren.modules.sys.entity.ProjectWorkEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static io.renren.modules.ltt.enums.PhoneStatus.PhoneStatus5;
import static io.renren.modules.ltt.enums.PhoneStatus.PhoneStatus6;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/21 18:45
 */
@Component
@Slf4j
@EnableAsync
public class BlackListTask {


    @Autowired
    private CdGetPhoneService cdGetPhoneService;
    @Autowired
    private CdLineRegisterService cdLineRegisterService;
    @Autowired
    private LineService lineService;
    @Autowired
    private ProxyService proxyService;
    @Resource(name = "cardMeServiceImpl")
    private FirefoxService firefoxService;

    @Resource(name = "caffeineCacheProjectWorkEntity")
    private Cache<String, ProjectWorkEntity> caffeineCacheProjectWorkEntity;

    public static final Object lockObj = new Object();



    @Resource(name = "caffeineCacheCode")
    private Cache<String, String> caffeineCacheCode;

    static ReentrantLock taskb5Lock = new ReentrantLock();

    @Scheduled(fixedDelay = 10000)
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void task5() {
        boolean tflag = taskb5Lock.tryLock();
        if (!tflag) {
            return;
        }
        try {
            //获取所有已经发起注册的机器
            List<CdLineRegisterEntity> list = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .in(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus3.getKey(),RegisterStatus.RegisterStatus1.getKey())
            );
            if (CollUtil.isEmpty(list)) {
                log.info("task5 list isEmpty");
                return;
            }
            List<CdGetPhoneEntity> cdGetPhoneEntities = new ArrayList<>();
            List<CdLineRegisterEntity> cdLineRegisterEntities = new ArrayList<>();
            for (CdLineRegisterEntity cdLineRegisterEntity : list) {
                RegisterResultDTO registerResultDTO = new RegisterResultDTO();
                registerResultDTO.setTaskId(cdLineRegisterEntity.getTaskId());
                RegisterResultVO registerResultVO = lineService.registerResult(registerResultDTO);
                if (ObjectUtil.isNull(registerResultVO)) {
                    continue;
                }
                if (200 == registerResultVO.getCode()) {
                    Long status = registerResultVO.getData().getStatus();
                    log.info("registerResultVO = {}",JSONUtil.toJsonStr(registerResultVO));
                    if (2 == status || 1 == status || Long.valueOf(20001).equals(status)) {
                        continue;
                    }
                    cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus5.getKey());
                    cdLineRegisterEntity.setErrMsg(registerResultVO.getData().getRemark());
                    //id
                    CdGetPhoneEntity cdGetPhoneEntity = new CdGetPhoneEntity();
                    cdGetPhoneEntity.setId(cdLineRegisterEntity.getGetPhoneId());
                    cdGetPhoneEntity.setPhoneStatus(PhoneStatus5.getKey());
                    if (StrUtil.isNotEmpty(cdLineRegisterEntity.getPkey())) {
                        boolean b = firefoxService.withBlackMobile(cdLineRegisterEntity.getPkey());
                        if (b) {
                            cdGetPhoneEntity.setPhoneStatus(PhoneStatus6.getKey());
                        }
                    }
                    cdGetPhoneEntities.add(cdGetPhoneEntity);
                    cdLineRegisterEntities.add(cdLineRegisterEntity);
                }
            }
            //如果为空 退出
            if (CollUtil.isEmpty(cdGetPhoneEntities) && CollUtil.isEmpty(cdLineRegisterEntities)) {
                return;
            }
            synchronized(lockObj){
                //注册出现问题修改为失败
                cdLineRegisterService.updateBatchById(cdLineRegisterEntities);
                //释放验证码
                cdGetPhoneService.updateBatchById(cdGetPhoneEntities);
            }
        }finally {
            taskb5Lock.unlock();
        }
    }


    static ReentrantLock taskb4Lock = new ReentrantLock();

    @Scheduled(fixedDelay = 15000)
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void task4() {

        boolean tflag = taskb4Lock.tryLock();
        if (!tflag) {
            return;
        }

        try {

            //获取所有已经发起注册的机器
            List<CdLineRegisterEntity> list = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .eq(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus3.getKey())
            );
            if (CollUtil.isEmpty(list)) {
                log.info("task4 list isEmpty");
                return;
            }

            List<CdLineRegisterEntity> cdLineRegisterEntities = new ArrayList<>();

            for (CdLineRegisterEntity cdLineRegisterEntity : list) {
                SyncLineTokenDTO syncLineTokenDTO = new SyncLineTokenDTO();
                syncLineTokenDTO.setTaskId(cdLineRegisterEntity.getTaskId());
                SyncLineTokenVO syncLineTokenVO = lineService.SyncLineTokenDTO(syncLineTokenDTO);
                if (ObjectUtil.isNull(syncLineTokenVO)) {
                    continue;
                }
                log.info("syncLineTokenVO = {}", JSONUtil.toJsonStr(syncLineTokenVO));
                if (200 == syncLineTokenVO.getCode() && CollUtil.isNotEmpty(syncLineTokenVO.getData())) {
                    SyncLineTokenVOData syncLineTokenVOData = syncLineTokenVO.getData().get(0);
                    cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus4.getKey());
                    String token = syncLineTokenVOData.getToken();
                    LineTokenJson lineTokenJson = JSON.parseObject(token, LineTokenJson.class);
                    boolean accountExistStatus = lineTokenJson.isAccountExistStatus();
                    if (accountExistStatus) {
                        cdLineRegisterEntity.setAccountExistStatus(AccountExistStatus.AccountExistStatus2.getKey());
                    }else {
                        cdLineRegisterEntity.setAccountExistStatus(AccountExistStatus.AccountExistStatus1.getKey());
                    }
                    cdLineRegisterEntity.setToken(token);
                    cdLineRegisterEntities.add(cdLineRegisterEntity);
                }
            }
            //如果为空 退出
            if (CollUtil.isEmpty(cdLineRegisterEntities)) {
                return;
            }
            synchronized(lockObj){
                cdLineRegisterService.updateBatchById(cdLineRegisterEntities);
            }
        }finally {
            taskb4Lock.unlock();;
        }
    }

    static ReentrantLock taskb3Lock = new ReentrantLock();

    //获取所有注册的机器 查询是否已经发送验证码 如果发送了 发起验证码注册 并且修改状态
    @Scheduled(fixedDelay = 10000)
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void task3() {
        boolean tflag = taskb3Lock.tryLock();
        if (!tflag) {
            return;
        }
        try {

            //获取所有已经发起注册的机器
            List<CdLineRegisterEntity> list = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .eq(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus1.getKey())
            );
            if (CollUtil.isEmpty(list)) {
                log.info("task3 list isEmpty");
                return;
            }

            List<CdGetPhoneEntity> cdGetPhoneEntities = new ArrayList<>();
            List<CdLineRegisterEntity> cdLineRegisterEntities = new ArrayList<>();
            for (CdLineRegisterEntity cdLineRegisterEntity : list) {
                String code = caffeineCacheCode.getIfPresent(cdLineRegisterEntity.getPhone());
                if (StrUtil.isNotEmpty(code)) {
                    SMSCodeDTO smsCodeDTO = new SMSCodeDTO();
                    smsCodeDTO.setsmsCode(code);
                    smsCodeDTO.setTaskId(cdLineRegisterEntity.getTaskId());
                    SMSCodeVO smsCodeVO = lineService.smsCode(smsCodeDTO);
                    if (ObjectUtil.isNull(smsCodeVO)) {
                        continue;
                    }
                    log.info("smsCodeVO = {}", JSONUtil.toJsonStr(smsCodeVO));
                    if (200 == smsCodeVO.getCode()) {
                        cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus3.getKey());
                        cdLineRegisterEntity.setSmsCode(code);
                        cdLineRegisterEntities.add(cdLineRegisterEntity);
                        if (ObjectUtil.isNotNull(cdLineRegisterEntity.getGetPhoneId())) {
                            CdGetPhoneEntity cdGetPhoneEntity = new CdGetPhoneEntity();
                            cdGetPhoneEntity.setId(cdLineRegisterEntity.getGetPhoneId());
                            cdGetPhoneEntity.setPhoneStatus(PhoneStatus.PhoneStatus4.getKey());
                            cdGetPhoneEntities.add(cdGetPhoneEntity);
                        }
                    }
                }
            }

            //如果为空 退出
            if (CollUtil.isEmpty(cdGetPhoneEntities) && CollUtil.isEmpty(cdLineRegisterEntities)) {
                return;
            }
            synchronized(lockObj){
                // 取到验证码了修改状态
                cdLineRegisterService.updateBatchById(cdLineRegisterEntities);
                // 验证码被line使用了 修改状态
                cdGetPhoneService.updateBatchById(cdGetPhoneEntities);
            }
        }finally {
            taskb3Lock.unlock();
        }
    }


    static ReentrantLock taskb2Lock = new ReentrantLock();

    //每5s就去获取一次验证码 释放 修改状态
    @Scheduled(fixedDelay = 10000)
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void task2() throws InterruptedException {
        boolean tflag = taskb2Lock.tryLock();
        if (!tflag) {
            return;
        }
        try {
            //获取验证码
            List<CdGetPhoneEntity> list = cdGetPhoneService.list(new QueryWrapper<CdGetPhoneEntity>().lambda()
                    .in(CdGetPhoneEntity::getPhoneStatus, PhoneStatus.PhoneStatus2.getKey(), PhoneStatus5.getKey())
            );
            if (CollUtil.isEmpty(list)) {
                log.info("task2 list isEmpty");
                return;
            }

            List<CdGetPhoneEntity> cdGetPhoneEntities = new ArrayList<>();

            for (CdGetPhoneEntity cdGetPhoneEntity : list) {
                //如果需要释放
                if(PhoneStatus5.getKey().equals(cdGetPhoneEntity.getPhoneStatus())) {
                    boolean b = firefoxService.setRel(cdGetPhoneEntity.getPkey());
                    if (b) {
                        cdGetPhoneEntity.setPhoneStatus(PhoneStatus.PhoneStatus6.getKey());
                        cdGetPhoneEntities.add(cdGetPhoneEntity);
                    }
                    //如果需要修改验证码
                }else {
                    String phoneCode = cdGetPhoneEntity.getCode();
                    //超过20分
                    long between = DateUtil.between(cdGetPhoneEntity.getCreateTime(), DateUtil.date(), DateUnit.MINUTE);
                    if (between > 7) {
                        cdGetPhoneEntity.setCode("验证码超时");
                        cdGetPhoneEntity.setPhoneStatus(PhoneStatus.PhoneStatus3.getKey());
                        cdGetPhoneEntities.add(cdGetPhoneEntity);
                        continue;
                    }
                    if (StrUtil.isEmpty(phoneCode)) {
                        phoneCode = firefoxService.getPhoneCode(cdGetPhoneEntity.getPkey());
                    }
                    if (StrUtil.isEmpty(phoneCode)) {
                        continue;
                    }
                    log.info("phoneCode = {}", phoneCode);
                    if (StrUtil.isNotEmpty(phoneCode)) {
                        cdGetPhoneEntity.setCode(phoneCode);
                        cdGetPhoneEntity.setPhoneStatus(PhoneStatus.PhoneStatus3.getKey());
                        caffeineCacheCode.put(cdGetPhoneEntity.getPhone(),phoneCode);
                        cdGetPhoneEntities.add(cdGetPhoneEntity);
                    }
                }
            }

            //如果为空 退出
            if (CollUtil.isEmpty(cdGetPhoneEntities)) {
                return;
            }

            synchronized (lockObj) {
                cdGetPhoneService.updateBatchById(cdGetPhoneEntities);
            }
        }finally {
            taskb2Lock.unlock();
        }

    }


    static ReentrantLock taskb1Lock = new ReentrantLock();

    // 获取到手机号就去注册line 请求注册
    @Scheduled(fixedDelay = 5000)
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void task1() {
        boolean tflag = taskb1Lock.tryLock();
        if (!tflag) {
            return;
        }
        try{
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            if (ObjectUtil.isNull(projectWorkEntity)) {
                return;
            }
            List<CdGetPhoneEntity> list = cdGetPhoneService.list(new QueryWrapper<CdGetPhoneEntity>().lambda()
                    .eq(CdGetPhoneEntity::getPhoneStatus, PhoneStatus.PhoneStatus1.getKey())
                    .last("limit 10")
            );
            if (CollUtil.isEmpty(list)) {
                log.info("task1 list isEmpty");
                return;
            }
            log.info("list = {},size = {}", JSONUtil.toJsonStr(list),list.size());
            List<CdLineRegisterEntity> cdLineRegisterEntities = new ArrayList<>();
            for (CdGetPhoneEntity cdGetPhoneEntity : list) {
                //如果不为空去拉黑手机号
                CdLineRegisterEntity one = cdLineRegisterService.getOne(new QueryWrapper<CdLineRegisterEntity>().lambda()
                        .eq(CdLineRegisterEntity::getPhone,cdGetPhoneEntity.getPhone())
                        .in(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus4.getKey(),RegisterStatus.RegisterStatus7.getKey(),RegisterStatus.RegisterStatus8.getKey())
                );
                if (ObjectUtil.isNotNull(one)) {
                    if (StrUtil.isNotEmpty(cdGetPhoneEntity.getPkey())) {
                        boolean b = firefoxService.withBlackMobile(cdGetPhoneEntity.getPkey());
                        if (b) {
                            cdGetPhoneService.removeById(cdGetPhoneEntity.getId());
                            continue;
                        }
                    }
                }

                LineRegisterDTO lineRegisterDTO = new LineRegisterDTO();
                lineRegisterDTO.setAb(projectWorkEntity.getLineAb());
                lineRegisterDTO.setAppVersion(projectWorkEntity.getLineAppVersion());
                lineRegisterDTO.setCountryCode(projectWorkEntity.getFirefoxCountry1());
                lineRegisterDTO.setPhone(cdGetPhoneEntity.getPhone());
                String getflowip = proxyService.getflowip(new CdLineRegisterEntity().setCountryCode(lineRegisterDTO.getCountryCode()));
                if (StrUtil.isEmpty(getflowip)) {
                    return;
                }
                lineRegisterDTO.setProxy(getflowip);
                lineRegisterDTO.setTxtToken(projectWorkEntity.getLineTxtToken());
                LineRegisterVO lineRegisterVO = lineService.lineRegister(lineRegisterDTO);

                if (ObjectUtil.isNull(lineRegisterVO)) {
                    continue;
                }
                log.info("lineRegisterVO = {}", JSONUtil.toJsonStr(lineRegisterVO));
                // 提交成功
                if (200 == lineRegisterVO.getCode()) {

                    CdLineRegisterEntity cdLineRegisterDTO = new CdLineRegisterEntity();
                    cdLineRegisterDTO.setAb(lineRegisterDTO.getAb());
                    cdLineRegisterDTO.setAppVersion(lineRegisterDTO.getAppVersion());
                    cdLineRegisterDTO.setCountryCode(lineRegisterDTO.getCountryCode());
                    cdLineRegisterDTO.setPhone(lineRegisterDTO.getPhone());
                    cdLineRegisterDTO.setProxy(lineRegisterDTO.getProxy());
                    cdLineRegisterDTO.setTxtToken(lineRegisterDTO.getTxtToken());
                    DataLineRegisterVO data = lineRegisterVO.getData();
                    cdLineRegisterDTO.setTaskId(data.getTaskId());
                    cdLineRegisterDTO.setRegisterStatus(RegisterStatus.RegisterStatus1.getKey());
                    cdLineRegisterDTO.setDeleteFlag(DeleteFlag.NO.getKey());
                    cdLineRegisterDTO.setCreateTime(DateUtil.date());
                    cdLineRegisterDTO.setGetPhoneId(cdGetPhoneEntity.getId());
                    cdLineRegisterDTO.setPkey(cdGetPhoneEntity.getPkey());
                    cdLineRegisterDTO.setSubtasksId(cdGetPhoneEntity.getSubtasksId());
                    //设置代理类型
                    cdLineRegisterDTO.setProxyStatus(projectWorkEntity.getProxy());
                    cdLineRegisterEntities.add(cdLineRegisterDTO);
                    cdGetPhoneEntity.setPhoneStatus(PhoneStatus.PhoneStatus2.getKey());
                }
            }

            synchronized (lockObj) {
                cdGetPhoneService.updateBatchById(list);
                cdLineRegisterService.saveBatch(cdLineRegisterEntities);
            }
        }finally {
            taskb1Lock.unlock();
        }
    }

}
