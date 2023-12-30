package io.renren.modules.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    static ReentrantLock task10Lock = new ReentrantLock();
    //查询同步结果
    @Scheduled(fixedDelay = 10000)
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
                    .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus3.getKey())
            );
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
                String getflowip = proxyService.getflowip(cdLineRegisterEntity);
                if (StrUtil.isEmpty(getflowip)) {
                    return;
                }
                GetChatsDTO getChatsDTO = new GetChatsDTO();
                getChatsDTO.setProxy(getflowip);
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
                    Map<String, CdMaterialPhoneEntity> midCdMaterialPhoneEntityMap = materialPhoneEntities.stream().collect(Collectors.toMap(CdMaterialPhoneEntity::getMid, s -> s));
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
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task9() {
        boolean b = task9Lock.tryLock();
        if (!b) {
            return;
        }
        try {
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
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
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task8() {
        boolean b = task8Lock.tryLock();
        if (!b) {
            return;
        }

        try{
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
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
                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
                    .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus3.getKey())
            );
            //根据task分组通讯录
            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = cdMaterialPhoneEntities.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
            //获取账号
            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));

            //同步成功的数据
            List<CdGroupTasksEntity> tasksEntities = new ArrayList<>();

            //
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
                List<String> mids = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getMid).collect(Collectors.toList());
                if (CollUtil.isEmpty(mids)){
                    continue;
                }
                //获取当前任务的运行账号
                CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(cdGroupTasksEntity.getLineRegisterId());
                if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                    continue;
                }
                String getflowip = proxyService.getflowip(cdLineRegisterEntity);
                if (StrUtil.isEmpty(getflowip)) {
                    return;
                }
                CreateGroupMax createGroupMax = new CreateGroupMax();
                createGroupMax.setUserMidList(mids);
                createGroupMax.setProxy(getflowip);
                createGroupMax.setGroupName(cdGroupTasksEntity.getGroupName());
                createGroupMax.setToken(cdLineRegisterEntity.getToken());
                LineRegisterVO lineRegisterVO = lineService.createGroupMax(createGroupMax);
                if (ObjectUtil.isNotNull(lineRegisterVO) && 200 == lineRegisterVO.getCode()) {
                    cdGroupTasksEntity.setTaskId(lineRegisterVO.getData().getTaskId());
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus3.getKey());
                    tasksEntities.add(cdGroupTasksEntity);
                }
            }

            synchronized (lockObj3) {
                cdGroupTasksService.updateBatchById(tasksEntities);
            }
        }finally {
            task8Lock.unlock();
        }

    }


    static ReentrantLock task7Lock = new ReentrantLock();
    //查询同步结果
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task7() {
        boolean b = task7Lock.tryLock();
        if (!b) {
            return;
        }
        try {
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus6.getKey())
            );

            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
                log.info("GroupTask task7 list isEmpty");
                return;
            }
            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();
            //
            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                SyncContentsResultDTO syncContentsResultDTO = new SyncContentsResultDTO();
                syncContentsResultDTO.setTaskId(cdGroupTasksEntity.getTaskId());
                SyncContentsResultVO syncContentsResultVO = lineService.syncContentsResult(syncContentsResultDTO);
                //状态成功
                if (ObjectUtil.isNotNull(syncContentsResultVO) && 200 == syncContentsResultVO.getCode()){
                    SyncContentsResultVO.Data data = syncContentsResultVO.getData();
                    //同步成功
                    if (2 == data.getStatus()) {
                        //查询当前任务所有的通讯录
                        List<CdMaterialPhoneEntity> list = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                                .eq(CdMaterialPhoneEntity::getGroupTaskId,cdGroupTasksEntity.getId())
                        );
                        //获取所有的联系方式map
                        Map<String, SyncContentsResultVO.Data.ContactsMap> contactsMap = data.getContactsMap();
                        //设置任务状态为成同步通讯录成功
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus7.getKey());
                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                        //同步通讯录成功更新
                        for (CdMaterialPhoneEntity cdMaterialPhoneEntity : list) {
                            SyncContentsResultVO.Data.ContactsMap contactsMap1 = contactsMap.get(cdMaterialPhoneEntity.getContactKey());
                            cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus4.getKey());
                            if (ObjectUtil.isNotNull(contactsMap1)) {
                                cdMaterialPhoneEntity.setLuid(contactsMap1.getLuid());
                                cdMaterialPhoneEntity.setContactType(contactsMap1.getContactType());
                                cdMaterialPhoneEntity.setContactKey(contactsMap1.getContactKey());
                                cdMaterialPhoneEntity.setMid(contactsMap1.getContact().getMid());
                                cdMaterialPhoneEntity.setType(contactsMap1.getContact().getType());
                                cdMaterialPhoneEntity.setStatus(contactsMap1.getContact().getStatus());
                                cdMaterialPhoneEntity.setRelation(contactsMap1.getContact().getRelation());
                                cdMaterialPhoneEntity.setDisplayName(contactsMap1.getContact().getDisplayName());
                                cdMaterialPhoneEntity.setPhoneticName(contactsMap1.getContact().getPhoneticName());
                                cdMaterialPhoneEntity.setPictureStatus(contactsMap1.getContact().getPictureStatus());
                                cdMaterialPhoneEntity.setThumbnailUrl(contactsMap1.getContact().getThumbnailUrl());
                                cdMaterialPhoneEntity.setStatusMessage(contactsMap1.getContact().getStatusMessage());
                                cdMaterialPhoneEntity.setDisplayNameOverridden(contactsMap1.getContact().getDisplayNameOverridden());
                                cdMaterialPhoneEntity.setPicturePath(contactsMap1.getContact().getPicturePath());
                                cdMaterialPhoneEntity.setRecommendpArams(contactsMap1.getContact().getRecommendParams());
                                cdMaterialPhoneEntity.setFriendRequestStatus(contactsMap1.getContact().getFriendRequestStatus());
                                cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus3.getKey());
                            }
                        }
                        cdMaterialPhoneService.updateBatchById(list);
                    }else if (-1 == data.getStatus()) {
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus8.getKey());
                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                        //网络异常
                    }else if (-2 == data.getStatus()) {
                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus8.getKey());
                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
                    }
                }
            }
            synchronized (lockObj3) {
                // 批量修改通讯录状态
                cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
            }
        }finally {
            task7Lock.unlock();
        }
    }


    static ReentrantLock task6Lock = new ReentrantLock();

    //开始 同步手机通讯录
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task6() {
        boolean b = task6Lock.tryLock();
        if (!b) {
            return;
        }
        try {
            //获取当前需要同步通讯的任务
            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus2.getKey())
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
            //获取所有的通讯录数据
            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
            );
            //根据task分组通讯录
            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = cdMaterialPhoneEntities.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
            //获取账号
            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));
            //同步成功的数据
            List<CdGroupTasksEntity> tasksEntities = new ArrayList<>();

            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
                //获取当前任务的运行账号
                CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(cdGroupTasksEntity.getLineRegisterId());
                if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                    continue;
                }
                //获取所有的通讯录
                List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
                List<String> phoneList = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getContactKey).collect(Collectors.toList());
                SyncContentsDTO syncContentsDTO = new SyncContentsDTO();
                String getflowip = proxyService.getflowip(cdLineRegisterEntity);
                if (StrUtil.isEmpty(getflowip)) {
                    return;
                }
                syncContentsDTO.setProxy(getflowip);
                syncContentsDTO.setPhoneList(phoneList);
                syncContentsDTO.setToken(cdLineRegisterEntity.getToken());
                LineRegisterVO lineRegisterVO = lineService.syncContents(syncContentsDTO);
                if (ObjectUtil.isNotNull(lineRegisterVO) && 200 == lineRegisterVO.getCode()) {
                    cdGroupTasksEntity.setTaskId(lineRegisterVO.getData().getTaskId());
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus6.getKey());
                    tasksEntities.add(cdGroupTasksEntity);
                }
            }
            synchronized (lockObj3) {
                cdGroupTasksService.updateBatchById(tasksEntities);
            }
        }finally {
            task6Lock.unlock();
        }
    }





    //给当前群分配任务 line号
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task5() {
        List<CdGroupTasksEntity> list = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus3.getKey())
                .eq(CdGroupTasksEntity::getAddType, AddType.AddType1.getKey())
        );

        if (CollUtil.isEmpty(list)) {
            log.info("GroupTask task5 list isEmpty");
            return;
        }
        List<CdGroupTasksEntity> cdGroupTasksEntities = new ArrayList<>();
        for (CdGroupTasksEntity cdGroupTasksEntity : list) {
            if (StrUtil.isEmpty(cdGroupTasksEntity.getTaskId())) {
                continue;
            }
            RegisterResultDTO registerResultDTO = new RegisterResultDTO();
            registerResultDTO.setTaskId(cdGroupTasksEntity.getTaskId());
            CreateGroupResultVO groupResult = lineService.createGroupResult(registerResultDTO);
            if (ObjectUtil.isNotNull(groupResult) && 200 == groupResult.getCode()) {
                if (2 == groupResult.getData().getStatus()) {
                    cdGroupTasksEntity.setRoomId(groupResult.getData().getGroupInfo().getRoomId());
                    cdGroupTasksEntity.setChatRoomUrl(groupResult.getData().getGroupInfo().getChatRoomUrl());
                    cdGroupTasksEntity.setRoomTicketId(groupResult.getData().getGroupInfo().getRoomTicketId());
                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus5.getKey());
                    cdGroupTasksEntities.add(cdGroupTasksEntity);
                }
            }else {
                cdGroupTasksEntity.setRoomId(groupResult.getMsg());
                cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
                cdGroupTasksEntities.add(cdGroupTasksEntity);
            }
        }

        if (CollUtil.isEmpty(cdGroupTasksEntities)) {
            return;
        }
        synchronized (lockObj3) {
            cdGroupTasksService.updateBatchById(cdGroupTasksEntities);
        }
    }

    //给当前群分配任务 line号
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task4() {
        List<CdGroupTasksEntity> list = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus2.getKey())
                .eq(CdGroupTasksEntity::getAddType, AddType.AddType1.getKey())
        );

        if (CollUtil.isEmpty(list)) {
            log.info("GroupTask task4 list isEmpty");
            return;
        }

        List<CdGroupTasksEntity> cdGroupTasksEntities = new ArrayList<>();
        for (CdGroupTasksEntity cdGroupTasksEntity : list) {
            List<CdGroupSubtasksEntity> cdGroupSubtasksEntities = cdGroupSubtasksService.list(new QueryWrapper<CdGroupSubtasksEntity>().lambda()
                    .lt(CdGroupSubtasksEntity::getExecutionTime, DateUtil.date())
                    .eq(CdGroupSubtasksEntity::getSearchStatus, SearchStatus.SearchStatus2.getKey())
                    .eq(CdGroupSubtasksEntity::getGroupTasksId,cdGroupTasksEntity.getId())
            );
            if (CollUtil.isEmpty(cdGroupSubtasksEntities)) {
                continue;
            }
            if (cdGroupTasksEntity.getUploadGroupNumber() == cdGroupSubtasksEntities.size()) {

                //查询是否已经分配了拉群账号
                CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterService.getOne(new QueryWrapper<CdLineRegisterEntity>().lambda()
                        .eq(CdLineRegisterEntity::getGroupTaskId,cdGroupTasksEntity.getId())
                );
                if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                    continue;
                }

                cdGroupTasksEntity.setCurrentExecutionsNumber(cdGroupTasksEntity.getUploadGroupNumber());
                cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus3.getKey());
                cdGroupTasksEntities.add(cdGroupTasksEntity);
                poolExecutor.submit(() -> {
                    List<String> collect = cdGroupSubtasksEntities.stream().filter(item -> StrUtil.isNotEmpty(item.getMid())).map(CdGroupSubtasksEntity::getMid).collect(Collectors.toList());
                    String getflowip = proxyService.getflowip(cdLineRegisterEntity);
                    if (StrUtil.isEmpty(getflowip)) {
                        return;
                    }
                    CreateGroupMax createGroupMax = new CreateGroupMax();
                    createGroupMax.setUserMidList(collect);
                    createGroupMax.setProxy(getflowip);
                    createGroupMax.setGroupName(cdGroupTasksEntity.getGroupName());
                    createGroupMax.setToken(cdLineRegisterEntity.getToken());
                    LineRegisterVO groupMax = lineService.createGroupMax(createGroupMax);
                    if (ObjectUtil.isNotNull(groupMax) && 200 == groupMax.getCode()) {
                        DataLineRegisterVO data = groupMax.getData();
                        if (ObjectUtil.isNotNull(data) && StrUtil.isNotEmpty(data.getTaskId())) {
                            cdGroupTasksEntity.setTaskId(data.getTaskId());
                            synchronized (lockObj3) {
                                cdGroupTasksService.updateById(cdGroupTasksEntity);
                            }
                        }

                    }
                });
            }
        }
        if (CollUtil.isEmpty(cdGroupTasksEntities)) {
            return;
        }
        synchronized (lockObj3) {
            cdGroupTasksService.updateBatchById(cdGroupTasksEntities);
        }
    }


    //给当前群分配任务 line号
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task3() {
        List<CdGroupSubtasksEntity> list = cdGroupSubtasksService.list(new QueryWrapper<CdGroupSubtasksEntity>().lambda()
                .lt(CdGroupSubtasksEntity::getExecutionTime, DateUtil.date())
                .eq(CdGroupSubtasksEntity::getSubtaskStatus, GroupSubTasksStatus.GroupStatus2.getKey())
                .eq(CdGroupSubtasksEntity::getSearchStatus, SearchStatus.SearchStatus1.getKey())
        );

        if (CollUtil.isEmpty(list)) {
            log.info("GroupTask task3 list isEmpty");
            return;
        }

        List<CdGroupSubtasksEntity> cdGroupSubtasksEntities = new ArrayList<>();

        for (CdGroupSubtasksEntity cdGroupSubtasksEntity : list) {
            //查询是否已经分配了拉群账号
            CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterService.getOne(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .eq(CdLineRegisterEntity::getGroupTaskId,cdGroupSubtasksEntity.getGroupTasksId())
            );
            if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                continue;
            }
            cdGroupSubtasksEntity.setAddStatus(AddStatus.AddStatus2.getKey());
            cdGroupSubtasksEntity.setSearchStatus(SearchStatus.SearchStatus2.getKey());
            cdGroupSubtasksEntities.add(cdGroupSubtasksEntity);
            poolExecutor.submit(() -> {
                String getflowip = proxyService.getflowip(cdLineRegisterEntity);
                if (StrUtil.isEmpty(getflowip)) {
                    return;
                }
                SearchPhoneDTO searchPhoneDTO = new SearchPhoneDTO();
                searchPhoneDTO.setProxy(getflowip);
                searchPhoneDTO.setPhone(cdGroupSubtasksEntity.getPhone());
                searchPhoneDTO.setToken(cdLineRegisterEntity.getToken());
                SearchPhoneVO andAddContactsByPhone = lineService.searchPhone(searchPhoneDTO);
                String errMsg = "";
                if (ObjectUtil.isNotNull(andAddContactsByPhone)) {
                    if (200 != andAddContactsByPhone.getCode()) {
                        errMsg = errMsg + andAddContactsByPhone.getMsg();
                        cdGroupSubtasksEntity.setSubtaskStatus(GroupSubTasksStatus.GroupStatus5.getKey());
                    }else {
                        cdGroupSubtasksEntity.setProxy(searchPhoneDTO.getProxy());
                        Map<String, The818051863582> data = andAddContactsByPhone.getData();
                        if (CollUtil.isNotEmpty(data)) {
                            The818051863582 the8180518635821 = data.values().stream().findFirst().get();
                            if (ObjectUtil.isNotNull(the8180518635821)) {
                                cdGroupSubtasksEntity.setSubtaskStatus(GroupSubTasksStatus.GroupStatus4.getKey());
                                cdGroupSubtasksEntity.setMid(the8180518635821.getMid());

                                AddFriendsByMid addFriendsByMid = getAddFriendsByMid(cdGroupSubtasksEntity, searchPhoneDTO);
                                SearchPhoneVO searchPhoneVO = lineService.addFriendsByMid(addFriendsByMid);
                                if (ObjectUtil.isNotNull(addFriendsByMid) && 200 != searchPhoneVO.getCode()) {
                                    errMsg = errMsg + searchPhoneVO.getMsg();
                                }
                            }
                        }
                    }
                    if (StrUtil.isNotEmpty(errMsg)) {
                        cdGroupSubtasksEntity.setErrMsg(errMsg);
                    }
                }


                synchronized (lockObj3) {
                    cdGroupSubtasksService.updateById(cdGroupSubtasksEntity);
                }
            });
        }
        if (CollUtil.isEmpty(cdGroupSubtasksEntities)) {
            return;
        }
        synchronized (lockObj3) {
            cdGroupSubtasksService.updateBatchById(cdGroupSubtasksEntities);
        }
    }

    private static AddFriendsByMid getAddFriendsByMid(CdGroupSubtasksEntity cdGroupSubtasksEntity, SearchPhoneDTO searchPhoneDTO) {
        AddFriendsByMid addFriendsByMid = new AddFriendsByMid();
        addFriendsByMid.setProxy(searchPhoneDTO.getProxy());
        addFriendsByMid.setPhone(searchPhoneDTO.getPhone());
        addFriendsByMid.setMid(cdGroupSubtasksEntity.getMid());
        addFriendsByMid.setFriendAddType("phoneSearch");
        addFriendsByMid.setToken(searchPhoneDTO.getToken());
        return addFriendsByMid;
    }


    //给当前群分配任务 line号
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task2() {
        List<CdGroupSubtasksEntity> list = cdGroupSubtasksService.list(new QueryWrapper<CdGroupSubtasksEntity>().lambda()
                .lt(CdGroupSubtasksEntity::getExecutionTime, DateUtil.date())
                .eq(CdGroupSubtasksEntity::getSubtaskStatus, GroupSubTasksStatus.GroupStatus1.getKey())
        );
        if (CollUtil.isEmpty(list)) {
            log.info("GroupTask task2 list isEmpty");
            return;
        }

        List<CdGroupSubtasksEntity> cdGroupSubtasksEntities = new ArrayList<>();
        for (CdGroupSubtasksEntity cdGroupSubtasksEntity : list) {
            //查询是否已经分配了拉群账号
            CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterService.getOne(new QueryWrapper<CdLineRegisterEntity>().lambda()
                    .eq(CdLineRegisterEntity::getGroupTaskId,cdGroupSubtasksEntity.getGroupTasksId())
            );
            if (ObjectUtil.isNull(cdLineRegisterEntity)) {
                continue;
            }
            cdGroupSubtasksEntity.setSubtaskStatus(GroupSubTasksStatus.GroupStatus2.getKey());
            cdGroupSubtasksEntities.add(cdGroupSubtasksEntity);
        }
        if (CollUtil.isEmpty(cdGroupSubtasksEntities)) {
            return;
        }
        synchronized (lockObj3) {
            cdGroupSubtasksService.updateBatchById(cdGroupSubtasksEntities);
        }
    }


    //给当前群分配任务 line号
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void task1() {
        List<CdGroupTasksEntity> list = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus1.getKey())
        );
        if (CollUtil.isEmpty(list)) {
            log.info("GroupTask task1 list isEmpty");
            return;
        }

        List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
                .eq(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus4.getKey())
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
    }



}
