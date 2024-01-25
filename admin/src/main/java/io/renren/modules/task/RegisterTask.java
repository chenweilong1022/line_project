//package io.renren.modules.task;
//
//import cn.hutool.core.collection.CollUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import io.renren.modules.ltt.dto.CdGetPhoneDTO;
//import io.renren.modules.ltt.entity.CdGetPhoneEntity;
//import io.renren.modules.ltt.entity.CdLineRegisterEntity;
//import io.renren.modules.ltt.entity.CdRegisterSubtasksEntity;
//import io.renren.modules.ltt.entity.CdRegisterTaskEntity;
//import io.renren.modules.ltt.enums.RegisterStatus;
//import io.renren.modules.ltt.enums.RegistrationStatus;
//import io.renren.modules.ltt.service.CdGetPhoneService;
//import io.renren.modules.ltt.service.CdLineRegisterService;
//import io.renren.modules.ltt.service.CdRegisterSubtasksService;
//import io.renren.modules.ltt.service.CdRegisterTaskService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
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
//public class RegisterTask {
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
//
//
//    private static final Object lockObj2 = new Object();
//
//    static ReentrantLock taskr4Lock = new ReentrantLock();
//
//    @Scheduled(fixedDelay = 10000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task4() throws InterruptedException {
//        boolean tflag = taskr4Lock.tryLock();
//        if (!tflag) {
//            return;
//        }
//        try {
//            //获取子任务
//            List<CdRegisterSubtasksEntity> cdRegisterSubtasksEntities = cdRegisterSubtasksService.list(new QueryWrapper<CdRegisterSubtasksEntity>().lambda()
//                    .eq(CdRegisterSubtasksEntity::getRegistrationStatus,RegistrationStatus.RegistrationStatus5.getKey())
//                    .last("limit 1")
//            );
//
//            if (CollUtil.isEmpty(cdRegisterSubtasksEntities)) {
//                log.info("RegisterTask task3 list isEmpty");
//                return;
//            }
//
//            for (CdRegisterSubtasksEntity cdRegisterSubtasksEntity : cdRegisterSubtasksEntities) {
//                //注册数量
//                int count = cdLineRegisterService.count(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                        .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(),RegisterStatus.RegisterStatus5.getKey())
//                        .eq(CdLineRegisterEntity::getSubtasksId,cdRegisterSubtasksEntity.getId())
//                );
//                //如果完成了注册数量
//                if (cdRegisterSubtasksEntity.getNumberRegistrations().equals(count) || cdRegisterSubtasksEntity.getNumberRegistrations() < count) {
//                    int successCount = cdLineRegisterService.count(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                            .eq(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey())
//                            .eq(CdLineRegisterEntity::getSubtasksId,cdRegisterSubtasksEntity.getId())
//                    );
//                    int errorCount = count - successCount;
//                    cdRegisterSubtasksEntity.setNumberSuccesses(successCount);
//                    cdRegisterSubtasksEntity.setNumberFailures(errorCount);
//                    cdRegisterSubtasksEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus6.getKey());
//                    //获取主任务 修改主任务数量
//                    CdRegisterTaskEntity cdRegisterTaskEntity = cdRegisterTaskService.getById((Serializable) cdRegisterSubtasksEntity.getTaskId());
//                    cdRegisterTaskEntity.setNumberSuccesses(cdRegisterTaskEntity.getNumberSuccesses() + successCount);
//                    cdRegisterTaskEntity.setNumberFailures(cdRegisterTaskEntity.getNumberFailures() + errorCount);
//                    Integer numberRegistered = cdRegisterTaskEntity.getNumberSuccesses() + cdRegisterTaskEntity.getNumberFailures();
//                    if (numberRegistered.equals(cdRegisterTaskEntity.getTotalAmount())) {
//                        cdRegisterTaskEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus6.getKey());
//                    }
//                    synchronized (lockObj2) {
//                        cdRegisterSubtasksService.updateById(cdRegisterSubtasksEntity);
//                        cdRegisterTaskService.updateById(cdRegisterTaskEntity);
//                    }
//                }
//            }
//        }finally {
//            taskr4Lock.unlock();
//        }
//    }
//
//
//    static ReentrantLock taskr3Lock = new ReentrantLock();
//
//    @Scheduled(fixedDelay = 10000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task3() throws InterruptedException {
//        boolean tflag = taskr3Lock.tryLock();
//        if (!tflag) {
//            return;
//        }
//        try {
//            //获取子任务
//            List<CdRegisterSubtasksEntity> cdRegisterSubtasksEntities = cdRegisterSubtasksService.list(new QueryWrapper<CdRegisterSubtasksEntity>().lambda()
//                    .eq(CdRegisterSubtasksEntity::getRegistrationStatus,RegistrationStatus.RegistrationStatus2.getKey())
//                    .last("limit 1")
//            );
//
//            if (CollUtil.isEmpty(cdRegisterSubtasksEntities)) {
//                log.info("RegisterTask task3 list isEmpty");
//                return;
//            }
//
//            for (CdRegisterSubtasksEntity cdRegisterSubtasksEntity : cdRegisterSubtasksEntities) {
//                //注册数量
//                int count = cdLineRegisterService.count(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                        .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(),RegisterStatus.RegisterStatus5.getKey(),RegisterStatus.RegisterStatus3.getKey())
//                        .eq(CdLineRegisterEntity::getSubtasksId,cdRegisterSubtasksEntity.getId())
//                );
//                //如果完成了注册数量
//                if (cdRegisterSubtasksEntity.getNumberRegistrations().equals(count) || cdRegisterSubtasksEntity.getNumberRegistrations() < count) {
//                    cdRegisterSubtasksEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus5.getKey());
//                    //获取主任务 修改主任务数量
//                    synchronized (lockObj2) {
//                        cdRegisterSubtasksService.updateById(cdRegisterSubtasksEntity);
//                    }
//                }
//            }
//        }finally {
//            taskr3Lock.unlock();
//        }
//    }
//
//
//    static ReentrantLock taskr2Lock = new ReentrantLock();
//
//    @Scheduled(fixedDelay = 10000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task2() throws InterruptedException {
//        boolean tflag = taskr2Lock.tryLock();
//        if (!tflag) {
//            return;
//        }
//
//        try {
//            //获取子任务
//            List<CdRegisterSubtasksEntity> cdRegisterSubtasksEntities = cdRegisterSubtasksService.groupByTaskId();
//            if (CollUtil.isEmpty(cdRegisterSubtasksEntities)) {
//                log.info("RegisterTask task2 list isEmpty");
//                return;
//            }
//            for (CdRegisterSubtasksEntity cdRegisterSubtasksEntity : cdRegisterSubtasksEntities) {
//                //如果获取的状态为2跳出循环
//                if (RegistrationStatus.RegistrationStatus2.getKey().equals(cdRegisterSubtasksEntity.getRegistrationStatus())) {
//                    //获取子任务数量
//                    int count = cdGetPhoneService.count(new QueryWrapper<CdGetPhoneEntity>().lambda()
//                            .eq(CdGetPhoneEntity::getSubtasksId,cdRegisterSubtasksEntity.getId())
//                    );
//                    if (!cdRegisterSubtasksEntity.getNumberRegistrations().equals(count)) {
//                        CdGetPhoneDTO cdGetPhoneDTO = new CdGetPhoneDTO();
//                        cdGetPhoneDTO.setCount(cdRegisterSubtasksEntity.getNumberRegistrations() - count);
//                        cdGetPhoneDTO.setSubtasksId(cdRegisterSubtasksEntity.getId());
//                        List<CdGetPhoneEntity> cdGetPhoneEntities = cdGetPhoneService.addCount(cdGetPhoneDTO);
//                        //如果数量相等
//                        if (cdGetPhoneDTO.getCount().equals(cdGetPhoneEntities.size() + count)) {
//                            cdRegisterSubtasksEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus2.getKey());
//                            synchronized (lockObj2) {
//                                cdRegisterSubtasksService.updateById(cdRegisterSubtasksEntity);
//                            }
//                        }
//                    }
//                    continue;
//                }
//
//                CdGetPhoneDTO cdGetPhoneDTO = new CdGetPhoneDTO();
//                cdGetPhoneDTO.setCount(cdRegisterSubtasksEntity.getNumberRegistrations());
//                cdGetPhoneDTO.setSubtasksId(cdRegisterSubtasksEntity.getId());
//                List<CdGetPhoneEntity> cdGetPhoneEntities = cdGetPhoneService.addCount(cdGetPhoneDTO);
//                //如果数量相等
//                if (CollUtil.isNotEmpty(cdGetPhoneEntities)) {
//                    cdRegisterSubtasksEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus2.getKey());
//                    synchronized (lockObj2) {
//                        cdRegisterSubtasksService.updateById(cdRegisterSubtasksEntity);
//                    }
//                }
//            }
//        }finally {
//            taskr2Lock.unlock();
//        }
//
//    }
//
//
//    static ReentrantLock taskr1Lock = new ReentrantLock();
//
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task1() {
//        boolean tflag = taskr1Lock.tryLock();
//        if (!tflag) {
//            return;
//        }
//        try {
//            //查询是否有注册任务
//            List<CdRegisterTaskEntity> list = cdRegisterTaskService.list(new QueryWrapper<CdRegisterTaskEntity>().lambda()
//                    .eq(CdRegisterTaskEntity::getRegistrationStatus, RegistrationStatus.RegistrationStatus1.getKey())
//            );
//
//            if (CollUtil.isEmpty(list)) {
//                log.info("RegisterTask task1 list isEmpty");
//                return;
//            }
//
//            List<CdRegisterSubtasksEntity> cdRegisterSubtasksEntities = new ArrayList<>();
//            //
//            for (CdRegisterTaskEntity cdRegisterTaskEntity : list) {
//                //获取子任务
//                int count = cdRegisterSubtasksService.count(new QueryWrapper<CdRegisterSubtasksEntity>().lambda()
//                        .eq(CdRegisterSubtasksEntity::getTaskId, cdRegisterTaskEntity.getId())
//                );
//                //查询子任务是否在注册
//                if (count == 0) {
//                    boolean flag = true;
//                    while (flag) {
//                        //总数量
//                        Integer totalAmount = cdRegisterTaskEntity.getTotalAmount();
//                        //线程数
//                        Integer numberThreads = cdRegisterTaskEntity.getNumberThreads();
//                        //注册数量 0
//                        Integer numberRegistered = cdRegisterTaskEntity.getNumberRegistered();
//                        //剩余注册数量
//                        Integer newNumberRegistrations = totalAmount - numberRegistered;
//                        if (newNumberRegistrations > numberThreads) {
//                            newNumberRegistrations = numberThreads;
//                        }else{
//                            flag = false;
//                            cdRegisterTaskEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus2.getKey());
//                        }
//                        CdRegisterSubtasksEntity cdRegisterSubtasksEntity = new CdRegisterSubtasksEntity();
//                        cdRegisterSubtasksEntity.setTaskId(cdRegisterTaskEntity.getId());
//                        cdRegisterSubtasksEntity.setNumberRegistrations(newNumberRegistrations);
//                        cdRegisterSubtasksEntity.setNumberSuccesses(0);
//                        cdRegisterSubtasksEntity.setNumberFailures(0);
//                        cdRegisterSubtasksEntity.setRegistrationStatus(RegistrationStatus.RegistrationStatus1.getKey());
//                        cdRegisterSubtasksEntities.add(cdRegisterSubtasksEntity);
//                        //设置主表注册数量
//                        cdRegisterTaskEntity.setNumberRegistered(cdRegisterTaskEntity.getNumberRegistered() + newNumberRegistrations);
//                    }
//                }
//            }
//
//            synchronized (lockObj2) {
//                cdRegisterSubtasksService.saveBatch(cdRegisterSubtasksEntities);
//                cdRegisterTaskService.updateBatchById(list);
//            }
//        }finally {
//            taskr1Lock.unlock();;
//        }
//    }
//
//
//
//}
