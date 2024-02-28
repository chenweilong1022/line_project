//package io.renren.modules.task;
//
//import cn.hutool.core.codec.Base64;
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.io.FastByteArrayOutputStream;
//import cn.hutool.core.io.IoUtil;
//import cn.hutool.core.util.ImageUtil;
//import cn.hutool.core.util.NetUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.http.HttpUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.github.benmanes.caffeine.cache.Cache;
//import io.renren.modules.ltt.dto.*;
//import io.renren.modules.ltt.entity.CdGroupTasksEntity;
//import io.renren.modules.ltt.entity.CdLineRegisterEntity;
//import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
//import io.renren.modules.ltt.entity.CdMaterialPhoneSendRecordEntity;
//import io.renren.modules.ltt.enums.*;
//import io.renren.modules.ltt.service.*;
//import io.renren.modules.ltt.vo.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.stream.Collectors;
//
///**
// * @author liuyuchan
// * @email liuyuchan286@gmail.com
// * @date 2023/12/2 00:45
// */
//@Component
//@Slf4j
//@EnableAsync
//public class SendMsg {
//
//    @Autowired
//    private CdRegisterTaskService cdRegisterTaskService;
//    @Autowired
//    private CdRegisterSubtasksService cdRegisterSubtasksService;
//    @Autowired
//    private CdGetPhoneService cdGetPhoneService;
//    @Autowired
//    private CdLineRegisterService cdLineRegisterService;
//    @Autowired
//    private CdGroupTasksService cdGroupTasksService;
//    @Autowired
//    private CdGroupSubtasksService cdGroupSubtasksService;
//    @Autowired
//    private LineService lineService;
//    @Resource(name = "poolExecutor")
//    private ThreadPoolTaskExecutor poolExecutor;
//    @Autowired
//    private ProxyService proxyService;
//    @Autowired
//    private CdMaterialPhoneService cdMaterialPhoneService;
//    @Autowired
//    private CdSpeechSkillsService cdSpeechSkillsService;
//    @Autowired
//    private CdMaterialPhoneSendRecordService cdMaterialPhoneSendRecordService;
//
//    @Resource(name = "caffeineCacheCode")
//    private Cache<String, String> caffeineCacheCode;
//    @Autowired
//    ThreadPoolTaskExecutor threadPoolTaskExecutor;
//
//    static ReentrantLock taskSendMsg1Lock = new ReentrantLock();
//    static ReentrantLock taskSendMsg2Lock = new ReentrantLock();
//
//    static ReentrantLock taskSendMsg3Lock = new ReentrantLock();
//
//
//    //查询同步结果
//    @Scheduled(fixedDelay = 10000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task3() {
//        boolean b = taskSendMsg3Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            CanSendListByGroupTaskIdDTO canSendListByGroupTaskIdDTO = new CanSendListByGroupTaskIdDTO();
//            canSendListByGroupTaskIdDTO.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus7.getKey());
//            //查询可以发送消息的账号
//            List<CanSendListByGroupTaskIdVO> canSendListByGroupTaskIdVOS = cdMaterialPhoneService.canSendListByGroupTaskId(canSendListByGroupTaskIdDTO);
//            if (CollUtil.isEmpty(canSendListByGroupTaskIdVOS)) {
//                log.info("SendMsg task3 list isEmpty");
//                return;
//            }
//            //需要发送的消息为空
//            List<CdSpeechSkillsVO> cdSpeechSkillsVOS = cdSpeechSkillsService.orderByList(null);
//            if (CollUtil.isEmpty(cdSpeechSkillsVOS)) {
//                log.info("cdSpeechSkillsVOS task3 list isEmpty");
//                return;
//            }
//
//            //获取可以发送消息的账号id列表
//            List<Integer> ids = canSendListByGroupTaskIdVOS.stream().map(CanSendListByGroupTaskIdVO::getMaterialPhoneId).collect(Collectors.toList());
//            List<CdMaterialPhoneEntity> materialPhoneEntities = cdMaterialPhoneService.listByIds(ids);
//            //获取line账号
//            List<Integer> lineRegisterIds = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getLineRegisterId).collect(Collectors.toList());
//            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineRegisterIds);
//            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));
//
//            Vector<CdMaterialPhoneEntity> updates = new Vector<>();
//            Vector<CdMaterialPhoneSendRecordEntity> cdMaterialPhoneSendRecordEntities = new Vector<>();
//
//            final CountDownLatch latch = new CountDownLatch(materialPhoneEntities.size());
//            //准备发消息
//            for (CdMaterialPhoneEntity materialPhoneEntity : materialPhoneEntities) {
//                threadPoolTaskExecutor.submit(new Thread(()->{
//
//
//                    CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(materialPhoneEntity.getLineRegisterId());
//                    if (ObjectUtil.isNull(cdLineRegisterEntity)) {
//                        latch.countDown();
//                        return;
//                    }
//                    String getflowip = proxyService.getflowip(cdLineRegisterEntity);
//                    if (StrUtil.isEmpty(getflowip)) {
//                        latch.countDown();
//                        return;
//                    }
//                    //发送消息
//                    for (CdSpeechSkillsVO cdSpeechSkillsVO : cdSpeechSkillsVOS) {
//                        if (SpeechSkillsType.SpeechSkillsType2.getKey().equals(cdSpeechSkillsVO.getType())) {
//                            String encryptedAccessToken = caffeineCacheCode.getIfPresent(cdLineRegisterEntity.getPhone());
//                            if (StrUtil.isEmpty(encryptedAccessToken)) {
//                                EncryptedAccessTokenDTO dto = new EncryptedAccessTokenDTO();
//                                dto.setProxy(getflowip);
//                                dto.setToken(cdLineRegisterEntity.getToken());
//                                EncryptedAccessTokenVO vo = lineService.encryptedAccessToken(dto);
//                                if (ObjectUtil.isNotNull(vo) && 200 == vo.getCode()) {
//                                    encryptedAccessToken = vo.getData().getEncryptedAccessToken();
//                                    caffeineCacheCode.put(cdLineRegisterEntity.getPhone(),encryptedAccessToken);
//                                }
//                            }
//
//                            String linkBase64 = caffeineCacheCode.getIfPresent(cdSpeechSkillsVO.getPicture());
//                            if (StrUtil.isEmpty(linkBase64)) {
//                                FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream();
//                                long download = HttpUtil.download(cdSpeechSkillsVO.getPicture(), fastByteArrayOutputStream, true);
//                                linkBase64 = Base64.encode(fastByteArrayOutputStream.toByteArray());
//                                caffeineCacheCode.put(cdSpeechSkillsVO.getPicture(),linkBase64);
//                            }
//
//                            if (StrUtil.isEmpty(encryptedAccessToken) || StrUtil.isEmpty(linkBase64)) {
//
//                                continue;
//                            }
//                            ShareImgMsgDTO shareImgMsgDTO = new ShareImgMsgDTO();
//                            shareImgMsgDTO.setProxy(getflowip);
//                            shareImgMsgDTO.setImgData(linkBase64);
//                            shareImgMsgDTO.setEncryptedAccessToken(encryptedAccessToken);
//                            shareImgMsgDTO.setToMid(materialPhoneEntity.getMid());
//                            shareImgMsgDTO.setToken(cdLineRegisterEntity.getToken());
//                            ShareImgMsgVO vo =  lineService.shareImgMsg(shareImgMsgDTO);
//                            if (200 == vo.getCode()) {
//                                CdMaterialPhoneSendRecordEntity cdMaterialPhoneSendRecordEntity = new CdMaterialPhoneSendRecordEntity();
//                                cdMaterialPhoneSendRecordEntity.setGroupTaskId(materialPhoneEntity.getGroupTaskId());
//                                cdMaterialPhoneSendRecordEntity.setLineRegisterId(materialPhoneEntity.getLineRegisterId());
//                                cdMaterialPhoneSendRecordEntity.setMaterialId(materialPhoneEntity.getMaterialId());
//                                cdMaterialPhoneSendRecordEntity.setContactKey(materialPhoneEntity.getContactKey());
//                                cdMaterialPhoneSendRecordEntity.setMid(materialPhoneEntity.getMid());
//                                cdMaterialPhoneSendRecordEntity.setSendText(cdSpeechSkillsVO.getPicture());
//                                cdMaterialPhoneSendRecordEntity.setSendType(cdSpeechSkillsVO.getType());
//                                cdMaterialPhoneSendRecordEntity.setSendStatus(SendStatus.SendStatus2.getKey());
//                                cdMaterialPhoneSendRecordEntities.add(cdMaterialPhoneSendRecordEntity);
//                            } else if (201 == vo.getCode()) {
//                                //网络异常，去清空代理
//                                UpdateWrapper<CdLineRegisterEntity> registerEntityUpdateWrapper = new UpdateWrapper<>();
//                                registerEntityUpdateWrapper.setSql("`proxy` = null").lambda().eq(CdLineRegisterEntity::getId,materialPhoneEntity.getLineRegisterId());
//                                cdLineRegisterService.update(registerEntityUpdateWrapper);
//                                cdMaterialPhoneService.removeById(materialPhoneEntity.getId());
//                                latch.countDown();
//                                return;
//                            }
//                        }else {
//                            String text = cdSpeechSkillsVO.getWord();
//                            if (StrUtil.isEmpty(text)) {
//                                text = cdSpeechSkillsVO.getLink();
//                            }
//                            if (StrUtil.isEmpty(text)) {
//                                continue;
//                            }
//
//                            ShareTextMsgDTO shareTextMsgDTO = new ShareTextMsgDTO();
//                            shareTextMsgDTO.setProxy(getflowip);
//                            shareTextMsgDTO.setMsgText(text);
//                            shareTextMsgDTO.setNegotiatePublicKeyBase64(materialPhoneEntity.getSettings());
//                            shareTextMsgDTO.setNegotiatePublicKeyId(Long.valueOf(materialPhoneEntity.getAttributes()));
//                            shareTextMsgDTO.setToMid(materialPhoneEntity.getMid());
//                            shareTextMsgDTO.setToken(cdLineRegisterEntity.getToken());
//                            ShareTextMsgVO shareTextMsgVO = lineService.shareTextMsg(shareTextMsgDTO);
//                            //如果消息发送成功
//                            if (200 == shareTextMsgVO.getCode()) {
//                                CdMaterialPhoneSendRecordEntity cdMaterialPhoneSendRecordEntity = new CdMaterialPhoneSendRecordEntity();
//                                cdMaterialPhoneSendRecordEntity.setGroupTaskId(materialPhoneEntity.getGroupTaskId());
//                                cdMaterialPhoneSendRecordEntity.setLineRegisterId(materialPhoneEntity.getLineRegisterId());
//                                cdMaterialPhoneSendRecordEntity.setMaterialId(materialPhoneEntity.getMaterialId());
//                                cdMaterialPhoneSendRecordEntity.setContactKey(materialPhoneEntity.getContactKey());
//                                cdMaterialPhoneSendRecordEntity.setMid(materialPhoneEntity.getMid());
//                                cdMaterialPhoneSendRecordEntity.setSendText(text);
//                                cdMaterialPhoneSendRecordEntity.setSendType(cdSpeechSkillsVO.getType());
//                                cdMaterialPhoneSendRecordEntity.setSendStatus(SendStatus.SendStatus2.getKey());
//                                cdMaterialPhoneSendRecordEntities.add(cdMaterialPhoneSendRecordEntity);
//                            } else if (201 == shareTextMsgVO.getCode()) {
//                                //网络异常，去清空代理
//                                UpdateWrapper<CdLineRegisterEntity> registerEntityUpdateWrapper = new UpdateWrapper<>();
//                                registerEntityUpdateWrapper.setSql("`proxy` = null").lambda().eq(CdLineRegisterEntity::getId,materialPhoneEntity.getLineRegisterId());
//                                cdLineRegisterService.update(registerEntityUpdateWrapper);
//                                cdMaterialPhoneService.removeById(materialPhoneEntity.getId());
//                                latch.countDown();
//                                return;
//                            }
//                        }
//                    }
//                    CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
//                    cdMaterialPhoneEntity.setId(materialPhoneEntity.getId());
//                    cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus8.getKey());
//                    updates.add(cdMaterialPhoneEntity);
//                    latch.countDown();
//                }));
//            }
//            latch.await();
//            //如果会话列表不为空
//            if (CollUtil.isNotEmpty(updates)) {
//                synchronized (CdMaterialPhoneService.cdMaterialPhoneServiceLockObj) {
//                    cdMaterialPhoneService.updateBatchById(updates);
//                }
//            }
//            if (CollUtil.isNotEmpty(cdMaterialPhoneSendRecordEntities)) {
//                //保存记录
//                cdMaterialPhoneSendRecordService.saveBatch(cdMaterialPhoneSendRecordEntities);
//            }
//        } catch (InterruptedException e) {
//            log.error("InterruptedException e = {}",e.getMessage());
//        } finally {
//            taskSendMsg3Lock.unlock();
//        }
//    }
//
//
//
//    //查询同步结果
//    @Scheduled(fixedDelay = 10000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task2() {
//        boolean b = taskSendMsg2Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            CanSendListByGroupTaskIdDTO canSendListByGroupTaskIdDTO = new CanSendListByGroupTaskIdDTO();
//            canSendListByGroupTaskIdDTO.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus6.getKey());
//            //查询可以发送消息的账号
//            List<CanSendListByGroupTaskIdVO> canSendListByGroupTaskIdVOS = cdMaterialPhoneService.canSendListByGroupTaskId(canSendListByGroupTaskIdDTO);
//            if (CollUtil.isEmpty(canSendListByGroupTaskIdVOS)) {
//                log.info("SendMsg task2 list isEmpty");
//                return;
//            }
//            //获取可以发送消息的账号id列表
//            List<Integer> ids = canSendListByGroupTaskIdVOS.stream().map(CanSendListByGroupTaskIdVO::getMaterialPhoneId).collect(Collectors.toList());
//            List<CdMaterialPhoneEntity> materialPhoneEntities = cdMaterialPhoneService.listByIds(ids);
//            final CountDownLatch latch = new CountDownLatch(materialPhoneEntities.size());
//            Vector<CdMaterialPhoneEntity> updates = new Vector<>();
//            //循环账号
//            for (CdMaterialPhoneEntity materialPhoneEntity : materialPhoneEntities) {
//                threadPoolTaskExecutor.submit(new Thread(()->{
//                    RegisterResultDTO registerResultDTO = new RegisterResultDTO();
//                    registerResultDTO.setTaskId(materialPhoneEntity.getContactType());
//                    CreateThreadResultVO createThreadResultVO = lineService.createThreadResult(registerResultDTO);
//                    if (200 == createThreadResultVO.getCode()) {
//                        CreateThreadResultVO.Data data = createThreadResultVO.getData();
//                        //成功获取到数据
//                        if (2 == data.getStatus()) {
//                            CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
//                            cdMaterialPhoneEntity.setId(materialPhoneEntity.getId());
//                            cdMaterialPhoneEntity.setAttributes(String.valueOf(data.getNegotiatePublicKeyId()));
//                            cdMaterialPhoneEntity.setSettings(data.getNegotiatePublicKeyBase64());
//                            cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus7.getKey());
//                            updates.add(cdMaterialPhoneEntity);
//                            //账号已经被封
//                        } else if (-1 == data.getStatus()) {
//                            cdLineRegisterService.unLock(materialPhoneEntity.getLineRegisterId());
//                            cdMaterialPhoneService.removeById(materialPhoneEntity.getId());
//                        } else if (-2 == data.getStatus()) {
//                            //网络异常，去清空代理
//                            UpdateWrapper<CdLineRegisterEntity> registerEntityUpdateWrapper = new UpdateWrapper<>();
//                            registerEntityUpdateWrapper.setSql("`proxy` = null").lambda().eq(CdLineRegisterEntity::getId,materialPhoneEntity.getLineRegisterId());
//                            cdLineRegisterService.update(registerEntityUpdateWrapper);
//                            cdMaterialPhoneService.removeById(materialPhoneEntity.getId());
//                        }
//                    }
//                    latch.countDown();
//                }));
//            }
//
//            latch.await();
//            //如果会话列表不为空
//            if (CollUtil.isNotEmpty(updates)) {
//                synchronized (CdMaterialPhoneService.cdMaterialPhoneServiceLockObj) {
//                    cdMaterialPhoneService.updateBatchById(updates);
//                }
//            }
//        } catch (InterruptedException e) {
//            log.error("InterruptedException e = {}",e.getMessage());
//        } finally {
//            taskSendMsg2Lock.unlock();
//        }
//    }
//
//
//
//
//    @Scheduled(fixedDelay = 10000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task1() {
//        boolean b = taskSendMsg1Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            CanSendListByGroupTaskIdDTO canSendListByGroupTaskIdDTO = new CanSendListByGroupTaskIdDTO();
//            canSendListByGroupTaskIdDTO.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus3.getKey());
//            //查询可以发送消息的账号
//            List<CanSendListByGroupTaskIdVO> canSendListByGroupTaskIdVOS = cdMaterialPhoneService.canSendListByGroupTaskId(canSendListByGroupTaskIdDTO);
//            if (CollUtil.isEmpty(canSendListByGroupTaskIdVOS)) {
//                log.info("SendMsg task1 list isEmpty");
//                return;
//            }
//            //获取可以发送消息的账号id列表
//            List<Integer> ids = canSendListByGroupTaskIdVOS.stream().map(CanSendListByGroupTaskIdVO::getMaterialPhoneId).collect(Collectors.toList());
//            List<CdMaterialPhoneEntity> materialPhoneEntities = cdMaterialPhoneService.listByIds(ids);
//            //获取line账号
//            List<Integer> lineRegisterIds = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getLineRegisterId).collect(Collectors.toList());
//            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineRegisterIds);
//            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));
//
//            List<CdMaterialPhoneEntity> updates = new ArrayList<>();
//            //循环账号
//            for (CdMaterialPhoneEntity materialPhoneEntity : materialPhoneEntities) {
//                CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(materialPhoneEntity.getLineRegisterId());
//                if (ObjectUtil.isNull(cdLineRegisterEntity)) {
//                    continue;
//                }
//                if (!CollUtil.newArrayList(RegisterStatus.RegisterStatus7.getKey(),RegisterStatus.RegisterStatus4.getKey()).contains(cdLineRegisterEntity.getRegisterStatus())) {
//                    cdMaterialPhoneService.removeById(materialPhoneEntity.getId());
//                    continue;
//                }
//                CreateThreadDTO createThreadDTO = new CreateThreadDTO();
//                createThreadDTO.setToMid(materialPhoneEntity.getMid());
//                createThreadDTO.setToken(cdLineRegisterEntity.getToken());
//                String getflowip = proxyService.getflowip(cdLineRegisterEntity);
//                if (StrUtil.isEmpty(getflowip)) {
//                    continue;
//                }
//                createThreadDTO.setProxy(getflowip);
//                //创建会话
//                RegisterResultVO thread = lineService.createThread(createThreadDTO);
//                //成功返回
//                if(200 == thread.getCode()) {
//                    //获取会话任务id
//                    String taskId = thread.getData().getTaskId();
//                    CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
//                    cdMaterialPhoneEntity.setId(materialPhoneEntity.getId());
//                    cdMaterialPhoneEntity.setContactType(taskId);
//                    cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus6.getKey());
//                    updates.add(cdMaterialPhoneEntity);
//                }
//            }
//            //如果会话列表不为空
//            if (CollUtil.isNotEmpty(updates)) {
//                synchronized (CdMaterialPhoneService.cdMaterialPhoneServiceLockObj) {
//                    cdMaterialPhoneService.updateBatchById(updates);
//                }
//            }
//        }finally {
//            taskSendMsg1Lock.unlock();
//        }
//    }
//
//
//}
