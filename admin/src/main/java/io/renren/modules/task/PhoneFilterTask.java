package io.renren.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.ltt.dto.AddFriendsByMid;
import io.renren.modules.ltt.dto.CdLineIpProxyDTO;
import io.renren.modules.ltt.dto.SearchPhoneDTO;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import io.renren.modules.ltt.entity.CdPhoneFilterEntity;
import io.renren.modules.ltt.enums.GroupSubTasksStatus;
import io.renren.modules.ltt.enums.OpenStatus;
import io.renren.modules.ltt.enums.PhoneFilterStatus;
import io.renren.modules.ltt.enums.RegisterStatus;
import io.renren.modules.ltt.service.*;
import io.renren.modules.ltt.vo.SearchPhoneVO;
import io.renren.modules.ltt.vo.The818051863582;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/21 18:45
 */
@Component
@Slf4j
@EnableAsync
public class PhoneFilterTask {





    static ReentrantLock task1Lock = new ReentrantLock();
    static ReentrantLock task2Lock = new ReentrantLock();
    static ReentrantLock task3Lock = new ReentrantLock();
    static ReentrantLock task4Lock = new ReentrantLock();

    @Autowired
    private LineService lineService;
    @Autowired
    private ProxyService proxyService;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 更新头像结果返回
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task4() {
        boolean b = task4Lock.tryLock();
        if (!b) {
            return;
        }
        try {
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }finally {
            task4Lock.unlock();
        }
    }


    /**
     * 更新头像结果返回
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task3() {
        boolean b = task3Lock.tryLock();
        if (!b) {
            return;
        }
        try {
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }finally {
            task3Lock.unlock();
        }
    }


    /**
     * 获取type为1 加粉类型为手机号模式
     */
    @Scheduled(fixedDelay = 8000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task2() {
        boolean b = task2Lock.tryLock();
        if (!b) {
            return;
        }
        try{

        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }finally {
            task2Lock.unlock();
        }
    }

    @Autowired
    private CdPhoneFilterService cdPhoneFilterService;
    @Autowired
    private CdLineRegisterService cdLineRegisterService;
    @Autowired
    private CdLineIpProxyService cdLineIpProxyService;

    public static final Object phoneFilterObj = new Object();

    /**
     * 获取初始化的添加粉任务
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task1() {
        boolean b = task1Lock.tryLock();
        if (!b) {
            return;
        }
        try{
            List<CdPhoneFilterEntity> cdPhoneFilterEntities = cdPhoneFilterService.list(new QueryWrapper<CdPhoneFilterEntity>().lambda()
                    .eq(CdPhoneFilterEntity::getTaskStatus, PhoneFilterStatus.PhoneFilterStatus1.getKey())
                    .last("limit 10")
            );

            if (CollUtil.isEmpty(cdPhoneFilterEntities)) {
                log.info("PhoneFilterTask task1 cdPhoneFilterEntities isEmpty");
                return;
            }

            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(),RegisterStatus.RegisterStatus7.getKey())
                    .eq(CdLineRegisterEntity::getCountryCode,"th")
                    .last("limit 10")
                    .orderByDesc(CdLineRegisterEntity::getId)
            );

            if (CollUtil.isEmpty(cdLineRegisterEntities)) {
                log.info("PhoneFilterTask task1 cdLineRegisterEntities isEmpty");
                return;
            }
            Queue<CdLineRegisterEntity> cdLineRegisterEntityQueue = new LinkedList<>(cdLineRegisterEntities);
            CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntityQueue.poll();

            List<CdPhoneFilterEntity> cdPhoneFilterEntitiesUpdate = new ArrayList<>();
            for (int i = 0; i < cdPhoneFilterEntities.size(); i++) {
                CdPhoneFilterEntity cdPhoneFilterEntity = cdPhoneFilterEntities.get(i);
                CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
                cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
                cdLineIpProxyDTO.setLzPhone(cdPhoneFilterEntity.getContactKey());
                String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
                if (StrUtil.isEmpty(proxyIp)) {
                    continue;
                }
                SearchPhoneDTO searchPhoneDTO = new SearchPhoneDTO();
                searchPhoneDTO.setProxy(proxyIp);
                searchPhoneDTO.setPhone(cdPhoneFilterEntity.getContactKey());
                searchPhoneDTO.setToken(cdLineRegisterEntity.getToken());
                SearchPhoneVO andAddContactsByPhone = lineService.searchPhone(searchPhoneDTO);

                if (ObjectUtil.isNotNull(andAddContactsByPhone)) {
                    if (200 != andAddContactsByPhone.getCode()) {
                        cdPhoneFilterEntity.setMsg(andAddContactsByPhone.getMsg());
//                        cdPhoneFilterEntity.setTaskStatus(PhoneFilterStatus.PhoneFilterStatus4.getKey());
                        cdPhoneFilterEntitiesUpdate.add(cdPhoneFilterEntity);
                        cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
                        if (CollUtil.isNotEmpty(cdPhoneFilterEntitiesUpdate)) {
                            synchronized (phoneFilterObj) {
                                cdPhoneFilterService.updateBatchById(cdPhoneFilterEntitiesUpdate);
                            }
                        }
                        return;
                    } else {
                        Map<String, The818051863582> data = andAddContactsByPhone.getData();
                        if (CollUtil.isNotEmpty(data)) {
                            The818051863582 the8180518635821 = data.values().stream().findFirst().get();
                            if (ObjectUtil.isNotNull(the8180518635821)) {
                                cdPhoneFilterEntity.setTaskStatus(PhoneFilterStatus.PhoneFilterStatus3.getKey());
                                cdPhoneFilterEntity.setMid(the8180518635821.getMid());
                                cdPhoneFilterEntity.setDisplayName(the8180518635821.getDisplayName());
                                cdPhoneFilterEntitiesUpdate.add(cdPhoneFilterEntity);
                            }
                        }else {
                            cdPhoneFilterEntity.setTaskStatus(PhoneFilterStatus.PhoneFilterStatus4.getKey());
                            cdPhoneFilterEntitiesUpdate.add(cdPhoneFilterEntity);
                        }
                    }

                }
            }
            if (CollUtil.isNotEmpty(cdPhoneFilterEntitiesUpdate)) {
                synchronized (phoneFilterObj) {
                    cdPhoneFilterService.updateBatchById(cdPhoneFilterEntitiesUpdate);
                }
            }
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }finally {
            task1Lock.unlock();
        }

    }
}
