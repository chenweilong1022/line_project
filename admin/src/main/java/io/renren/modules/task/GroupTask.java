package io.renren.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.ltt.dto.*;
import io.renren.modules.ltt.entity.*;
import io.renren.modules.ltt.enums.*;
import io.renren.modules.ltt.service.*;
import io.renren.modules.ltt.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/12/2 00:45
 */
@Component
@Slf4j
@EnableAsync
public class GroupTask {



    @Autowired
    private CdRegisterTaskService cdRegisterTaskService;
    @Autowired
    private CdRegisterSubtasksService cdRegisterSubtasksService;
    @Autowired
    private CdGetPhoneService cdGetPhoneService;
    @Autowired
    private CdLineRegisterService cdLineRegisterService;
    @Autowired
    private CdGroupTasksService cdGroupTasksService;
    @Autowired
    private CdGroupSubtasksService cdGroupSubtasksService;
    @Autowired
    private LineService lineService;
    @Resource(name = "poolExecutor")
    private ThreadPoolTaskExecutor poolExecutor;
    @Autowired
    private ProxyService proxyService;
    @Autowired
    private CdMaterialPhoneService cdMaterialPhoneService;

    private static final Object lockObj3 = new Object();
    private static final Object lockRegisterObj3 = new Object();
    private static final Object lockPhoneObj = new Object();

    static ReentrantLock task10Lock = new ReentrantLock();

    //查询同步结果
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task10() {
        boolean b = task10Lock.tryLock();
        if (!b) {
            return;
        }
        try{

            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType, AddType.AddType2.getKey())
                    .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus5.getKey())
                    .last("limit 5")
                    .orderByDesc(CdGroupTasksEntity::getId)
            );

            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
                log.info("GroupTask task10 list isEmpty");
                return;
            }

            //获取所有的任务id
            List<Integer> ids = new ArrayList<>();
            //所有的账号id
            List<Integer> lineId = new ArrayList<>();
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                ids.add(cdGroupTasksEntity.getId());
                lineId.add(cdGroupTasksEntity.getLineRegisterId());
            }
            //获取所有的通讯录数据
            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
                    .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus9.getKey())
            );
            if (CollUtil.isEmpty(cdMaterialPhoneEntities)) {
                for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                    cdGroupTasksEntity.setSuccessfullyAttractGroupsNumber(0);
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus10.getKey());
                }
                synchronized(lockObj3) {
                    cdGroupTasksService.updateBatchById(cdGroupTasksEntities);
                }
                return;
            }
            //根据task分组通讯录
            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = cdMaterialPhoneEntities.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
            //获取账号
            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));

            //同步成功的数据
            List<CdGroupTasksEntity> tasksEntities = new ArrayList<>();
            List<CdMaterialPhoneEntity> materialPhoneEntitiesUpdate = new ArrayList<>();

            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
                if (CollUtil.isEmpty(materialPhoneEntities)){
                    continue;
                }
                //获取当前任务的运行账号
                CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(cdGroupTasksEntity.getLineRegisterId());
                if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                    continue;
                }

                CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
                cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
                cdLineIpProxyDTO.setLzPhone(materialPhoneEntities.get(0).getContactKey());
                String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
                if (StrUtil.isEmpty(proxyIp)) {
                    return;
                }
                GetChatsDTO getChatsDTO = new GetChatsDTO();
                getChatsDTO.setProxy(proxyIp);
                getChatsDTO.setChatRoomId(cdGroupTasksEntity.getRoomId());
                getChatsDTO.setToken(cdLineRegisterEntity.getToken());
                GetChatsVO chats = lineService.getChats(getChatsDTO);
                if (ObjectUtil.isNotNull(chats) && 200 == chats.getCode()) {
                    GetChatsVO.Data data = chats.getData();
                    if (ObjectUtil.isNull(data)) continue;
                    List<GetChatsVO.Data.Chat> dataChats = data.getChats();
                    if (CollUtil.isEmpty(dataChats)) continue;
                    GetChatsVO.Data.Chat chat = dataChats.get(0);
                    if (ObjectUtil.isNull(chat)) continue;
                    GetChatsVO.Data.Chat.Extra extra = chat.getExtra();
                    if (ObjectUtil.isNull(extra)) continue;
                    GetChatsVO.Data.Chat.Extra.GroupExtra groupExtra = extra.getGroupExtra();
                    if (ObjectUtil.isNull(groupExtra)) continue;
                    Map<String, Long> memberMids = groupExtra.getMemberMids();
                    Map<String, CdMaterialPhoneEntity> midCdMaterialPhoneEntityMap = materialPhoneEntities.stream().filter(item -> StrUtil.isNotEmpty(item.getMid())).collect(Collectors.toMap(CdMaterialPhoneEntity::getMid, s -> s,(v1,v2) -> v2));
                    for (String key : memberMids.keySet()) {
                        CdMaterialPhoneEntity cdMaterialPhoneEntity = midCdMaterialPhoneEntityMap.get(key);
                        if (ObjectUtil.isNotNull(cdMaterialPhoneEntity)) {
                            cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus5.getKey());
                            materialPhoneEntitiesUpdate.add(cdMaterialPhoneEntity);
                        }
                    }
                    cdGroupTasksEntity.setSuccessfullyAttractGroupsNumber(memberMids.keySet().size());
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus9.getKey());
                    tasksEntities.add(cdGroupTasksEntity);
                }
            }

            synchronized(lockObj3) {
                cdMaterialPhoneService.updateBatchById(materialPhoneEntitiesUpdate);
                cdGroupTasksService.updateBatchById(tasksEntities);
            }
        }finally {
            task10Lock.unlock();
        }

    }

    static ReentrantLock task9Lock = new ReentrantLock();

    //查询同步结果
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task9() {
        boolean b = task9Lock.tryLock();
        if (!b) {
            log.info("task9Lock 在执行任务");
            return;
        }
        try {
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
                    .last("limit 10")
                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus3.getKey())
            );

            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
                log.info("GroupTask task9 list isEmpty");
                return;
            }

            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();

            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                RegisterResultDTO registerResultDTO = new RegisterResultDTO();
                registerResultDTO.setTaskId(cdGroupTasksEntity.getTaskId());
                CreateGroupResultVO groupResult = lineService.createGroupResult(registerResultDTO);
                if (ObjectUtil.isNotNull(groupResult) && 200 == groupResult.getCode()) {
                    if (2 == groupResult.getData().getStatus()) {
                        cdGroupTasksEntity.setRoomId(groupResult.getData().getGroupInfo().getRoomId());
                        cdGroupTasksEntity.setChatRoomUrl(groupResult.getData().getGroupInfo().getChatRoomUrl());
                        cdGroupTasksEntity.setRoomTicketId(groupResult.getData().getGroupInfo().getRoomTicketId());
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus5.getKey());
                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                    }else if (-1 == groupResult.getData().getStatus()) {
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                        cdLineRegisterService.unLock(cdGroupTasksEntity.getLineRegisterId());
                    }else if (-2 == groupResult.getData().getStatus()) {
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                    }
                }else {
                    cdGroupTasksEntity.setRoomId(groupResult.getMsg());
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
                    cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                }
            }

            synchronized (lockObj3) {
                cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
            }
        }finally {
            task9Lock.unlock();
        }
    }


    static ReentrantLock task8Lock = new ReentrantLock();

    //查询同步结果
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task8() {
        boolean b = task8Lock.tryLock();
        if (!b) {
            log.info("task8Lock 在执行任务");
            return;
        }

        try{
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
                    .last("limit 10")
                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus7.getKey())
            );
            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
                log.info("GroupTask task8 list isEmpty");
                return;
            }
            //获取所有的任务id
            List<Integer> ids = new ArrayList<>();
            //所有的账号id
            List<Integer> lineId = new ArrayList<>();
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                ids.add(cdGroupTasksEntity.getId());
                lineId.add(cdGroupTasksEntity.getLineRegisterId());
            }
            //获取所有的通讯录数据
            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                    .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus9.getKey())
                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
            );
            //根据task分组通讯录
            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = cdMaterialPhoneEntities.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
            //获取账号
            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));

            //同步成功的数据
            List<CdGroupTasksEntity> tasksEntities = new ArrayList<>();
            final CountDownLatch latch = new CountDownLatch(cdGroupTasksEntities.size());
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                threadPoolTaskExecutor.submit(new Thread(()->{
                    List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
                    if (CollUtil.isEmpty(materialPhoneEntities)) {
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus8.getKey());
                        tasksEntities.add(cdGroupTasksEntity);
                        latch.countDown();
                        return;
                    }
                    List<String> mids = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getMid).collect(Collectors.toList());
                    if (CollUtil.isEmpty(mids)){
                        latch.countDown();
                        return;
                    }
                    //获取当前任务的运行账号
                    CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(cdGroupTasksEntity.getLineRegisterId());
                    if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                        latch.countDown();
                        return;
                    }
                    CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
                    cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
                    cdLineIpProxyDTO.setLzPhone(materialPhoneEntities.get(0).getContactKey());
                    String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
                    if (StrUtil.isEmpty(proxyIp)) {
                        latch.countDown();
                        return;
                    }
                    CreateGroupMax createGroupMax = new CreateGroupMax();
                    createGroupMax.setUserMidList(mids);
                    createGroupMax.setProxy(proxyIp);
                    createGroupMax.setGroupName(cdGroupTasksEntity.getGroupName());
                    createGroupMax.setToken(cdLineRegisterEntity.getToken());
                    LineRegisterVO lineRegisterVO = lineService.createGroupMax(createGroupMax);
                    if (ObjectUtil.isNotNull(lineRegisterVO) && 200 == lineRegisterVO.getCode()) {
                        cdGroupTasksEntity.setTaskId(lineRegisterVO.getData().getTaskId());
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus3.getKey());
                        tasksEntities.add(cdGroupTasksEntity);
                    }
                    latch.countDown();
                }));
            }

            latch.await();
            if (CollUtil.isNotEmpty(tasksEntities)) {
                synchronized (lockObj3) {
                    cdGroupTasksService.updateBatchById(tasksEntities);
                }
            }
        }catch (Exception e){

        }finally {
            task8Lock.unlock();
        }

    }

    static ReentrantLock task7Lock = new ReentrantLock();
    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private CdLineIpProxyService cdLineIpProxyService;

    static ReentrantLock task6Lock = new ReentrantLock();


    static ReentrantLock task4Lock = new ReentrantLock();

    /**
     * 加失败的自动重新分配
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
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus12.getKey())
                    .last("limit 1")
            );

            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
                log.info("GroupTask task6 list isEmpty");
                return;
            }
            //任务ids
            List<Integer> ids = cdGroupTasksEntities.stream().map(CdGroupTasksEntity::getId).collect(Collectors.toList());
            List<CdMaterialPhoneEntity> list = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
            );
            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = list.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));

            //获取200个账号
            LambdaQueryWrapper<CdLineRegisterEntity> last = new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey())
                    .orderByDesc(CdLineRegisterEntity::getId)
                    .last("limit " + 200);

            List<CdLineRegisterEntity> cdLineRegisterEntities1 = cdLineRegisterService.list(last);
            if (CollUtil.isEmpty(cdLineRegisterEntities1)) {
                log.info("line token is empty donot fenpei");
                return;
            }
            Queue<CdLineRegisterEntity> cdLineRegisterEntities2 = new LinkedList<>(cdLineRegisterEntities1);

            List<CdLineRegisterEntity> cdLineRegisterEntitiesUpdate = new ArrayList<>();
            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();
            List<CdMaterialPhoneEntity> cdMaterialPhoneEntitiesUpdate = new ArrayList<>();
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                //获取所有失败的手机号
                List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
                CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities2.poll();
                cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus7.getKey());
                cdLineRegisterEntity.setGroupTaskId(cdGroupTasksEntity.getId());
                cdLineRegisterEntitiesUpdate.add(cdLineRegisterEntity);

                CdGroupTasksEntity update = new CdGroupTasksEntity();
                update.setId(cdGroupTasksEntity.getId());
                update.setGroupStatus(GroupStatus.GroupStatus1.getKey());
                update.setLineRegisterId(cdLineRegisterEntity.getId());
                cdGroupTasksEntitiesUpdate.add(update);

                for (CdMaterialPhoneEntity cdMaterialPhoneEntity : materialPhoneEntities) {
                    cdMaterialPhoneEntity.setLineRegisterId(cdLineRegisterEntity.getId());
                    cdMaterialPhoneEntity.setGroupTaskId(cdGroupTasksEntity.getId());
                    cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus1.getKey());
                    cdMaterialPhoneEntitiesUpdate.add(cdMaterialPhoneEntity);
                }
            }

            if (CollUtil.isNotEmpty(cdMaterialPhoneEntitiesUpdate)) {
                synchronized (lockPhoneObj) {
                    cdMaterialPhoneService.updateBatchById(cdMaterialPhoneEntitiesUpdate);
                }
            }
            if (CollUtil.isNotEmpty(cdGroupTasksEntitiesUpdate)) {
                synchronized (lockObj3) {
                    cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
                }
            }

            if (CollUtil.isNotEmpty(cdLineRegisterEntitiesUpdate)) {
                synchronized (lockRegisterObj3) {
                    cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesUpdate);
                }
            }
        }catch (Exception e) {
            log.error("err = {}",e.getMessage());
        }finally {
            task4Lock.unlock();
        }
    }


    private final ConcurrentHashMap<Integer, Lock> lockMap = new ConcurrentHashMap<>();



    //开始 同步手机通讯录
    @Scheduled(fixedDelay = 1000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task6() {
        boolean b = task6Lock.tryLock();
        if (!b) {
            log.info("task6Lock 在执行任务");
            return;
        }
        try {
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus2.getKey())
                    .last("limit 80")
            );

            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
                log.info("GroupTask task6 list isEmpty");
                return;
            }
            //获取所有的任务id
            List<Integer> ids = new ArrayList<>();
            //所有的账号id
            List<Integer> lineId = new ArrayList<>();
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                ids.add(cdGroupTasksEntity.getId());
                lineId.add(cdGroupTasksEntity.getLineRegisterId());
            }
            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.groupByIds(ids);

            List<Integer> cdMaterialPhoneIds = cdMaterialPhoneEntities.stream().map(CdMaterialPhoneEntity::getId).collect(Collectors.toList());
            List<Integer> getGroupTaskIds = cdMaterialPhoneEntities.stream().map(CdMaterialPhoneEntity::getGroupTaskId).collect(Collectors.toList());
            List<Integer> difference = new ArrayList<>(ids);
            difference.removeAll(getGroupTaskIds);
            //
            List<CdGroupTasksEntity> cdGroupTasksEntityArrayListU = new ArrayList<>();

            if (CollUtil.isNotEmpty(difference)) {
                for (Integer i : difference) {
                    CdGroupTasksEntity cdGroupTasksEntity = new CdGroupTasksEntity();
                    cdGroupTasksEntity.setId(i);
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus7.getKey());
                    cdGroupTasksEntityArrayListU.add(cdGroupTasksEntity);
                }
                cdGroupTasksService.updateBatchById(cdGroupTasksEntityArrayListU);
            }

            List<CdMaterialPhoneEntity> materialPhoneEntities = cdMaterialPhoneService.listByIds(cdMaterialPhoneIds);
            if (CollUtil.isEmpty(materialPhoneEntities)) {
                return;
            }
            //获取用户
            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));


//            List<CdMaterialPhoneEntity> updates = new ArrayList<>();

            final CountDownLatch latch = new CountDownLatch(materialPhoneEntities.size());
//            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();
            for (CdMaterialPhoneEntity materialPhoneEntity : materialPhoneEntities) {
//                if (DateUtil.date().before(materialPhoneEntity.getStartDate())) {
//                    log.info("id = {},还没到执行时间");
//                    latch.countDown();
//                    continue;
//                }
                Lock lock = lockMap.computeIfAbsent(materialPhoneEntity.getId(), k -> new ReentrantLock());
                threadPoolTaskExecutor.submit(new Thread(()->{
                    if (lock.tryLock()) {
                        try {
                            if (StrUtil.isEmpty(materialPhoneEntity.getMid())) {
                                CdMaterialPhoneEntity update = new CdMaterialPhoneEntity();
                                update.setId(materialPhoneEntity.getId());
                                update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus10.getKey());
//                        updates.add(update);
                                cdMaterialPhoneService.updateById(update);
                                latch.countDown();
                                return;
                            }
                            //获取当前任务的运行账号
                            CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(materialPhoneEntity.getLineRegisterId());
                            if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                                latch.countDown();
                                return;
                            }

                            CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
                            cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
                            cdLineIpProxyDTO.setLzPhone(materialPhoneEntity.getContactKey());
                            String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
                            if (StrUtil.isEmpty(proxyIp)) {
                                latch.countDown();
                                return;
                            }
                            //如果是搜索模式
                            if (AddFriendType.AddFriendType8.getValue().equals(materialPhoneEntity.getAddFriendType())) {
                                if (addFriendByPhone(materialPhoneEntity, proxyIp, cdLineRegisterEntity, latch)) return;
                            }else {
                                //mid模式直接添加
                                if (addFriendById(materialPhoneEntity, proxyIp, cdLineRegisterEntity, latch)) return;
                            }
                            Thread.sleep(7000);
                        } catch (Exception e) {

                        } finally {
                            // 确保释放锁
                            lock.unlock();
                        }
                    } else {
                        // 获取锁失败，直接跳出
                        log.info("Could not acquire lock for = {}",materialPhoneEntity.getId());
                    }
                }));
            }
            latch.await(50,TimeUnit.SECONDS);
//            if (CollUtil.isNotEmpty(updates)) {
//                synchronized (lockPhoneObj) {
//                    cdMaterialPhoneService.updateBatchById(updates);
//                }
//            }
//            if (CollUtil.isNotEmpty(cdGroupTasksEntitiesUpdate)) {
//                synchronized (lockObj3) {
//                    cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
//                }
//            }
        }catch (Exception e){

        }finally {
            task6Lock.unlock();
        }
    }

    private boolean addFriendByPhone(CdMaterialPhoneEntity materialPhoneEntity, String proxyIp, CdLineRegisterEntity cdLineRegisterEntity, CountDownLatch latch) {
        SearchPhoneDTO searchPhoneDTO = new SearchPhoneDTO();
        searchPhoneDTO.setProxy(proxyIp);
        searchPhoneDTO.setPhone(materialPhoneEntity.getContactKey());
        searchPhoneDTO.setToken(cdLineRegisterEntity.getToken());
        SearchPhoneVO andAddContactsByPhone = lineService.searchPhone(searchPhoneDTO);

        CdMaterialPhoneEntity update = new CdMaterialPhoneEntity();
        update.setId(materialPhoneEntity.getId());
        update.setVideoProfile(searchPhoneDTO.getProxy());
        if (ObjectUtil.isNotNull(andAddContactsByPhone)) {
            if (200 != andAddContactsByPhone.getCode()) {
                update.setErrMsg(StrUtil.concat(true, materialPhoneEntity.getErrMsg(),andAddContactsByPhone.getMsg()));
                update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus12.getKey());
                CdGroupTasksEntity cdGroupTasksEntityUpdate = new CdGroupTasksEntity();
                cdGroupTasksEntityUpdate.setId(materialPhoneEntity.getGroupTaskId());
                cdGroupTasksEntityUpdate.setGroupStatus(GroupStatus.GroupStatus11.getKey());
                //cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntityUpdate);
                cdGroupTasksService.updateById(cdGroupTasksEntityUpdate);
                //updates.add(update);
                cdMaterialPhoneService.updateById(update);
                cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
                latch.countDown();
                return true;
            }else if (300 == andAddContactsByPhone.getCode()){
                update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus1.getKey());
            }else {
                Map<String, The818051863582> data = andAddContactsByPhone.getData();
                if (CollUtil.isNotEmpty(data)) {
                    The818051863582 the8180518635821 = data.values().stream().findFirst().get();
                    if (ObjectUtil.isNotNull(the8180518635821)) {
                        update.setMid(the8180518635821.getMid());
                        AddFriendsByMid addFriendsByMid = getAddFriendsByMid(update, searchPhoneDTO);
                        SearchPhoneVO searchPhoneVO = lineService.addFriendsByMid(addFriendsByMid);
                        if (ObjectUtil.isNotNull(searchPhoneVO) && 200 == searchPhoneVO.getCode()) {
                            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus9.getKey());
                            update.setErrMsg(StrUtil.concat(true, materialPhoneEntity.getErrMsg(),searchPhoneVO.getMsg()));
                        }else{
                            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus10.getKey());
                            CdGroupTasksEntity cdGroupTasksEntityUpdate = new CdGroupTasksEntity();
                            cdGroupTasksEntityUpdate.setId(materialPhoneEntity.getGroupTaskId());
                            cdGroupTasksEntityUpdate.setGroupStatus(GroupStatus.GroupStatus11.getKey());
                            //cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntityUpdate);
                            cdGroupTasksService.updateById(cdGroupTasksEntityUpdate);
                            //updates.add(update);
                            cdMaterialPhoneService.updateById(update);
                            cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
                            latch.countDown();
                            return true;
                        }
                    }
                }
            }
        }
        cdMaterialPhoneService.updateById(update);
        latch.countDown();
        return false;
    }

    private boolean addFriendById(CdMaterialPhoneEntity materialPhoneEntity, String proxyIp, CdLineRegisterEntity cdLineRegisterEntity, CountDownLatch latch) {
        AddFriendsByHomeRecommendDTO addFriendsByMid = new AddFriendsByHomeRecommendDTO();
        addFriendsByMid.setProxy(proxyIp);
        addFriendsByMid.setMid(materialPhoneEntity.getMid());
        addFriendsByMid.setToken(cdLineRegisterEntity.getToken());
        SearchPhoneVO searchPhoneVO = lineService.addFriendsByHomeRecommend(addFriendsByMid, materialPhoneEntity.getAddFriendType());
        CdMaterialPhoneEntity update = new CdMaterialPhoneEntity();
        update.setId(materialPhoneEntity.getId());
        if (ObjectUtil.isNull(searchPhoneVO)) {
            latch.countDown();
            return true;
        }
        update.setErrMsg(StrUtil.concat(true, materialPhoneEntity.getErrMsg(),searchPhoneVO.getMsg()));
        update.setVideoProfile(proxyIp);
        if (200 == searchPhoneVO.getCode()) {
            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus9.getKey());
        }else if (300 == searchPhoneVO.getCode()){
            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus1.getKey());
        }else {
            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus10.getKey());
            CdGroupTasksEntity cdGroupTasksEntityUpdate = new CdGroupTasksEntity();
            cdGroupTasksEntityUpdate.setId(materialPhoneEntity.getGroupTaskId());
            cdGroupTasksEntityUpdate.setGroupStatus(GroupStatus.GroupStatus11.getKey());
//                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntityUpdate);
            cdGroupTasksService.updateById(cdGroupTasksEntityUpdate);
//                        updates.add(update);
            cdMaterialPhoneService.updateById(update);
            cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
            latch.countDown();
            return true;
        }
//                    updates.add(update);
        cdMaterialPhoneService.updateById(update);
        latch.countDown();
        return false;
    }

    private static AddFriendsByMid getAddFriendsByMid(CdMaterialPhoneEntity cdGroupSubtasksEntity, SearchPhoneDTO searchPhoneDTO) {
        AddFriendsByMid addFriendsByMid = new AddFriendsByMid();
        addFriendsByMid.setProxy(searchPhoneDTO.getProxy());
        addFriendsByMid.setPhone(searchPhoneDTO.getPhone());
        addFriendsByMid.setMid(cdGroupSubtasksEntity.getMid());
        addFriendsByMid.setFriendAddType("phoneSearch");
        addFriendsByMid.setToken(searchPhoneDTO.getToken());
        return addFriendsByMid;
    }


    static ReentrantLock task1Lock = new ReentrantLock();

    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task1() {
        boolean b = task1Lock.tryLock();
        if (!b) {
            log.info("task1Lock 在执行任务");
            return;
        }
        try {
            List<CdGroupTasksEntity> list = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus1.getKey())
                    .last("limit 80")
            );
            if (CollUtil.isEmpty(list)) {
                log.info("GroupTask task1 list isEmpty");
                return;
            }

            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .in(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus4.getKey())
                    .eq(CdLineRegisterEntity::getOpenStatus,OpenStatus.OpenStatus3.getKey())
                    .eq(CdLineRegisterEntity::getCountryCode,"th")
                    .last("limit "+list.size())
                    .orderByDesc(CdLineRegisterEntity::getId)
            );

            List<CdLineRegisterEntity> cdLineRegisterEntitiesNewUpdate = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                CdGroupTasksEntity cdGroupTasksEntity = list.get(i);
                //如果类型是搜索，校验一下
                if (AddType.AddType1.getKey().equals(cdGroupTasksEntity.getAddType())) {
                    if (CollUtil.isEmpty(cdLineRegisterEntities) || cdLineRegisterEntities.size() != list.size()) {
                        log.info("GroupTask task1 cdLineRegisterEntities isEmpty");
                        return;
                    }
                    CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities.get(i);
                    cdLineRegisterEntity.setGroupTaskId(cdGroupTasksEntity.getId());
                    cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus7.getKey());
                    cdLineRegisterEntitiesNewUpdate.add(cdLineRegisterEntity);
                }
                cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus2.getKey());
            }
            synchronized (lockObj3) {
                cdGroupTasksService.updateBatchById(list);
                if (CollUtil.isNotEmpty(cdLineRegisterEntitiesNewUpdate)) {
                    cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesNewUpdate);
                }
            }
        }finally {
            task1Lock.unlock();
        }

    }



}
