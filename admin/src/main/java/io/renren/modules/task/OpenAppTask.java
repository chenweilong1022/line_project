//package io.renren.modules.task;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import io.renren.modules.ltt.dto.*;
//import io.renren.modules.ltt.entity.*;
//import io.renren.modules.ltt.enums.OpenStatus;
//import io.renren.modules.ltt.enums.RegisterStatus;
//import io.renren.modules.ltt.enums.RegistrationStatus;
//import io.renren.modules.ltt.service.*;
//import io.renren.modules.ltt.vo.LineRegisterVO;
//import io.renren.modules.ltt.vo.OpenAppResult;
//import io.renren.modules.ltt.vo.RefreshAccessTokenVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import springfox.documentation.annotations.ApiIgnore;
//
//import javax.annotation.Resource;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * @author liuyuchan
// * @email liuyuchan286@gmail.com
// * @date 2023/12/2 00:45
// */
//@Component
//@Slf4j
//@EnableAsync
//public class OpenAppTask {
//
//
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
//    private LineService lineService;
//    @Autowired
//    private ProxyService proxyService;
//    @Autowired
//    private CdLineErrLogsService cdLineErrLogsService;
//
//    private static final Object lockObj4 = new Object();
//
//    @Resource(name = "poolExecutor")
//    private ThreadPoolTaskExecutor poolExecutor;
//    static ReentrantLock task4Lock = new ReentrantLock();
//
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task4() {
//        boolean b = task4Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            List<CdLineRegisterEntity> list = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                    .eq(CdLineRegisterEntity::getOpenStatus, OpenStatus.OpenStatus5.getKey())
//                    .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(), RegisterStatus.RegisterStatus7.getKey())
//                    .last("limit 20")
//            );
//            if (CollUtil.isEmpty(list)) {
//                return;
//            }
//
//            final CountDownLatch latch = new CountDownLatch(list.size());
//
//            Vector<CdLineRegisterEntity> cdLineRegisterEntitiesNew = new Vector<>();
//
//            for (CdLineRegisterEntity one : list) {
//
//                poolExecutor.submit(new Thread(()->{
//                    String getflowip = proxyService.getflowip(one);
//                    if (StrUtil.isEmpty(getflowip)) {
//                        latch.countDown();
//                        return;
//                    }
//                    RefreshAccessTokenDTO refreshAccessTokenDTO = new RefreshAccessTokenDTO();
//                    refreshAccessTokenDTO.setProxy(getflowip);
//                    refreshAccessTokenDTO.setToken(one.getToken());
//                    RefreshAccessTokenVO refreshAccessTokenVO = lineService.refreshAccessToken(refreshAccessTokenDTO);
//                    if (ObjectUtil.isNotNull(refreshAccessTokenVO) && 200 == refreshAccessTokenVO.getCode()) {
//                        LineTokenJson lineTokenJson = JSON.parseObject(one.getToken(), LineTokenJson.class);
//                        lineTokenJson.setAccessToken(refreshAccessTokenVO.getData().getAccessToken());
//                        lineTokenJson.setRefreshToken(refreshAccessTokenVO.getData().getRefreshToken());
//                        String token = JSONUtil.toJsonStr(lineTokenJson);
//                        one.setToken(token);
//                        one.setOpenStatus(OpenStatus.OpenStatus1.getKey());
//                        cdLineRegisterEntitiesNew.add(one);
//                    }else if (ObjectUtil.isNotNull(refreshAccessTokenVO) && 201 == refreshAccessTokenVO.getCode()) {
//                        one.setOpenStatus(OpenStatus.OpenStatus4.getKey());
//                        cdLineRegisterEntitiesNew.add(one);
//                    }
//                    latch.countDown();
//                }));
//            }
//            latch.await();
//            synchronized (BlackListTask.lockObj) {
//                cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesNew);
//            }
//        } catch (InterruptedException e) {
//
//        } finally {
//            task4Lock.unlock();;
//        }
//    }
//
//
//
////    @Scheduled(cron = "0 10 9 * * ?")
////    @Transactional(rollbackFor = Exception.class)
////    @Async
////    public void task3() {
////        CdLineRegisterEntity cdLineRegisterEntity = new CdLineRegisterEntity();
////        cdLineRegisterEntity.setOpenStatus(OpenStatus.OpenStatus1.getKey());
////        cdLineRegisterService.update(cdLineRegisterEntity,new QueryWrapper<CdLineRegisterEntity>());
////    }
//
//
//    static ReentrantLock task2Lock = new ReentrantLock();
//
//
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task2() {
//        boolean b = task2Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            List<CdLineRegisterEntity> list = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                    .eq(CdLineRegisterEntity::getOpenStatus, OpenStatus.OpenStatus2.getKey())
//                    .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(), RegisterStatus.RegisterStatus7.getKey())
//                    .last("limit 20")
//            );
//
//            if (CollUtil.isEmpty(list)) {
//                return;
//            }
//
//            final CountDownLatch latch = new CountDownLatch(list.size());
//
//            List<CdLineRegisterEntity> cdLineRegisterEntitiesNewUpdate = new ArrayList<>();
//
//            for (CdLineRegisterEntity one : list) {
//                poolExecutor.submit(new Thread(()->{
//                    RegisterResultDTO registerResultDTO = new RegisterResultDTO();
//                    registerResultDTO.setTaskId(one.getTaskId());
//                    OpenAppResult openAppResult = lineService.openAppResult(registerResultDTO);
//                    if (ObjectUtil.isNotNull(openAppResult) && 200 == openAppResult.getCode()) {
//                        OpenAppResult.Data data = openAppResult.getData();
//                        if (ObjectUtil.isNull(data)) {
//                            latch.countDown();
//                            return;
//                        }
//                        CdLineRegisterEntity update = new CdLineRegisterEntity();
//                        update.setId(one.getId());
//                        if (2 == data.getStatus()) {
//                            update.setOpenStatus(OpenStatus.OpenStatus3.getKey());
//                            cdLineRegisterEntitiesNewUpdate.add(update);
//                        }else if (Long.valueOf(10001).equals(data.getStatus())) {
//                            update.setOpenStatus(OpenStatus.OpenStatus5.getKey());
//                            cdLineRegisterEntitiesNewUpdate.add(update);
//                        }else if (-1 == data.getStatus()) {
//                            update.setOpenStatus(OpenStatus.OpenStatus4.getKey());
//                            if (data.getRemark().contains("网络异常")) {
//                                update.setOpenStatus(OpenStatus.OpenStatus1.getKey());
//                            }else if (data.getRemark().contains("TOKEN_CLIENT_LOGGED_OUT")) {
//                                update.setOpenStatus(OpenStatus.OpenStatus1.getKey());
//                                update.setRegisterStatus(RegisterStatus.RegisterStatus8.getKey());
//                            }
//                            cdLineRegisterEntitiesNewUpdate.add(update);
//                        }
//                    }
//                    latch.countDown();
//                }));
//            }
//            latch.await();
//            synchronized (BlackListTask.lockObj) {
//                cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesNewUpdate);
//            }
//        } catch (InterruptedException e) {
//
//        } finally {
//            task2Lock.unlock();
//        }
//    }
//
//
//
//    static ReentrantLock task1Lock = new ReentrantLock();
//
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task1() {
//        boolean b = task1Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            List<CdLineRegisterEntity> list = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                    .eq(CdLineRegisterEntity::getOpenStatus, OpenStatus.OpenStatus1.getKey())
//                    .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(), RegisterStatus.RegisterStatus7.getKey())
//                    .last("limit 20")
//            );
//            if (CollUtil.isEmpty(list)) {
//                return;
//            }
//
//            List<CdLineRegisterEntity> cdLineRegisterEntitiesNew = new ArrayList<>();
//            final CountDownLatch latch = new CountDownLatch(list.size());
//            for (CdLineRegisterEntity one : list) {
//
//                poolExecutor.submit(new Thread(()->{
//                    String getflowip = proxyService.getflowip(one);
//                    if (StrUtil.isEmpty(getflowip)) {
//                        latch.countDown();
//                        return;
//                    }
//                    OpenApp openApp = new OpenApp();
//                    openApp.setProxy(getflowip);
//                    openApp.setToken(one.getToken());
//                    LineRegisterVO lineRegisterVO = lineService.openApp(openApp);
//                    if (ObjectUtil.isNotNull(lineRegisterVO) && 200 == lineRegisterVO.getCode()) {
//                        CdLineRegisterEntity update = new CdLineRegisterEntity();
//                        update.setId(one.getId());
//                        update.setOpenStatus(OpenStatus.OpenStatus2.getKey());
//                        update.setTaskId(lineRegisterVO.getData().getTaskId());
//                        cdLineRegisterEntitiesNew.add(update);
//                    }
//                    latch.countDown();
//                }));
//            }
//
//            latch.await();
//            synchronized (BlackListTask.lockObj) {
//                cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesNew);
//            }
//        } catch (InterruptedException e) {
//
//        } finally {
//            task1Lock.unlock();
//        }
//    }
//
//
//}
