//package io.renren.modules.task;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import io.renren.modules.ltt.dto.*;
//import io.renren.modules.ltt.entity.*;
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
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Lock;
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
//public class GroupTask {
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
//
//    private static final Object lockObj3 = new Object();
//    private static final Object lockRegisterObj3 = new Object();
//    private static final Object lockPhoneObj = new Object();
//
//    static ReentrantLock task10Lock = new ReentrantLock();
//
//    //查询同步结果
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task10() {
//        boolean b = task10Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try{
//
//            //获取当前需要同步通讯的任务
//            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
//                    .eq(CdGroupTasksEntity::getAddType, AddType.AddType2.getKey())
//                    .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus5.getKey())
//                    .last("limit 5")
//                    .orderByDesc(CdGroupTasksEntity::getId)
//            );
//
//            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
//                log.info("GroupTask task10 list isEmpty");
//                return;
//            }
//
//            //获取所有的任务id
//            List<Integer> ids = new ArrayList<>();
//            //所有的账号id
//            List<Integer> lineId = new ArrayList<>();
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                ids.add(cdGroupTasksEntity.getId());
//                lineId.add(cdGroupTasksEntity.getLineRegisterId());
//            }
//            //获取所有的通讯录数据
//            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
//                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
//                    .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus9.getKey())
//            );
//            if (CollUtil.isEmpty(cdMaterialPhoneEntities)) {
//                for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                    cdGroupTasksEntity.setSuccessfullyAttractGroupsNumber(0);
//                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus10.getKey());
//                }
//                synchronized(lockObj3) {
//                    cdGroupTasksService.updateBatchById(cdGroupTasksEntities);
//                }
//                return;
//            }
//            //根据task分组通讯录
//            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = cdMaterialPhoneEntities.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
//            //获取账号
//            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
//            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));
//
//            //同步成功的数据
//            List<CdGroupTasksEntity> tasksEntities = new ArrayList<>();
//            List<CdMaterialPhoneEntity> materialPhoneEntitiesUpdate = new ArrayList<>();
//
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
//                if (CollUtil.isEmpty(materialPhoneEntities)){
//                    continue;
//                }
//                //获取当前任务的运行账号
//                CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(cdGroupTasksEntity.getLineRegisterId());
//                if (ObjectUtil.isNull(cdLineRegisterEntity)) {
//                    continue;
//                }
//
//                CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
//                cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
//                cdLineIpProxyDTO.setLzPhone(materialPhoneEntities.get(0).getContactKey());
//                String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
//                if (StrUtil.isEmpty(proxyIp)) {
//                    return;
//                }
//                GetChatsDTO getChatsDTO = new GetChatsDTO();
//                getChatsDTO.setProxy(proxyIp);
//                getChatsDTO.setChatRoomId(cdGroupTasksEntity.getRoomId());
//                getChatsDTO.setToken(cdLineRegisterEntity.getToken());
//                GetChatsVO chats = lineService.getChats(getChatsDTO);
//                if (ObjectUtil.isNotNull(chats) && 200 == chats.getCode()) {
//                    GetChatsVO.Data data = chats.getData();
//                    if (ObjectUtil.isNull(data)) continue;
//                    List<GetChatsVO.Data.Chat> dataChats = data.getChats();
//                    if (CollUtil.isEmpty(dataChats)) continue;
//                    GetChatsVO.Data.Chat chat = dataChats.get(0);
//                    if (ObjectUtil.isNull(chat)) continue;
//                    GetChatsVO.Data.Chat.Extra extra = chat.getExtra();
//                    if (ObjectUtil.isNull(extra)) continue;
//                    GetChatsVO.Data.Chat.Extra.GroupExtra groupExtra = extra.getGroupExtra();
//                    if (ObjectUtil.isNull(groupExtra)) continue;
//                    Map<String, Long> memberMids = groupExtra.getMemberMids();
//                    Map<String, CdMaterialPhoneEntity> midCdMaterialPhoneEntityMap = materialPhoneEntities.stream().filter(item -> StrUtil.isNotEmpty(item.getMid())).collect(Collectors.toMap(CdMaterialPhoneEntity::getMid, s -> s,(v1,v2) -> v2));
//                    for (String key : memberMids.keySet()) {
//                        CdMaterialPhoneEntity cdMaterialPhoneEntity = midCdMaterialPhoneEntityMap.get(key);
//                        if (ObjectUtil.isNotNull(cdMaterialPhoneEntity)) {
//                            cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus5.getKey());
//                            materialPhoneEntitiesUpdate.add(cdMaterialPhoneEntity);
//                        }
//                    }
//                    cdGroupTasksEntity.setSuccessfullyAttractGroupsNumber(memberMids.keySet().size());
//                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus9.getKey());
//                    tasksEntities.add(cdGroupTasksEntity);
//                }
//            }
//
//            synchronized(lockObj3) {
//                cdMaterialPhoneService.updateBatchById(materialPhoneEntitiesUpdate);
//                cdGroupTasksService.updateBatchById(tasksEntities);
//            }
//        }finally {
//            task10Lock.unlock();
//        }
//
//    }
//
//    static ReentrantLock task9Lock = new ReentrantLock();
//
//    //查询同步结果
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task9() {
//        boolean b = task9Lock.tryLock();
//        if (!b) {
//            log.info("task9Lock 在执行任务");
//            return;
//        }
//        try {
//            //获取当前需要同步通讯的任务
//            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
//                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
//                    .last("limit 10")
//                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus3.getKey())
//            );
//
//            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
//                log.info("GroupTask task9 list isEmpty");
//                return;
//            }
//
//            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();
//
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                RegisterResultDTO registerResultDTO = new RegisterResultDTO();
//                registerResultDTO.setTaskId(cdGroupTasksEntity.getTaskId());
//                CreateGroupResultVO groupResult = lineService.createGroupResult(registerResultDTO);
//                if (ObjectUtil.isNotNull(groupResult) && 200 == groupResult.getCode()) {
//                    if (2 == groupResult.getData().getStatus()) {
//                        cdGroupTasksEntity.setRoomId(groupResult.getData().getGroupInfo().getRoomId());
//                        cdGroupTasksEntity.setChatRoomUrl(groupResult.getData().getGroupInfo().getChatRoomUrl());
//                        cdGroupTasksEntity.setRoomTicketId(groupResult.getData().getGroupInfo().getRoomTicketId());
//                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus5.getKey());
//                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
//                    }else if (-1 == groupResult.getData().getStatus()) {
//                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
//                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
//                        cdLineRegisterService.unLock(cdGroupTasksEntity.getLineRegisterId());
//                    }else if (-2 == groupResult.getData().getStatus()) {
//                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
//                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
//                    }
//                }else {
//                    cdGroupTasksEntity.setRoomId(groupResult.getMsg());
//                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus4.getKey());
//                    cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntity);
//                }
//            }
//
//            synchronized (lockObj3) {
//                cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
//            }
//        }finally {
//            task9Lock.unlock();
//        }
//    }
//
//
//    static ReentrantLock task8Lock = new ReentrantLock();
//
//    //查询同步结果
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task8() {
//        boolean b = task8Lock.tryLock();
//        if (!b) {
//            log.info("task8Lock 在执行任务");
//            return;
//        }
//
//        try{
//            //获取当前需要同步通讯的任务
//            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
//                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
//                    .last("limit 10")
//                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus7.getKey())
//            );
//            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
//                log.info("GroupTask task8 list isEmpty");
//                return;
//            }
//            //获取所有的任务id
//            List<Integer> ids = new ArrayList<>();
//            //所有的账号id
//            List<Integer> lineId = new ArrayList<>();
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                ids.add(cdGroupTasksEntity.getId());
//                lineId.add(cdGroupTasksEntity.getLineRegisterId());
//            }
//            //获取所有的通讯录数据
//            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
//                    .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus9.getKey())
//                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
//            );
//            //根据task分组通讯录
//            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = cdMaterialPhoneEntities.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
//            //获取账号
//            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
//            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));
//
//            //同步成功的数据
//            List<CdGroupTasksEntity> tasksEntities = new ArrayList<>();
//            final CountDownLatch latch = new CountDownLatch(cdGroupTasksEntities.size());
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                threadPoolTaskExecutor.submit(new Thread(()->{
//                    List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
//                    if (CollUtil.isEmpty(materialPhoneEntities)) {
//                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus8.getKey());
//                        tasksEntities.add(cdGroupTasksEntity);
//                        latch.countDown();
//                        return;
//                    }
//                    List<String> mids = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getMid).collect(Collectors.toList());
//                    if (CollUtil.isEmpty(mids)){
//                        latch.countDown();
//                        return;
//                    }
//                    //获取当前任务的运行账号
//                    CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(cdGroupTasksEntity.getLineRegisterId());
//                    if (ObjectUtil.isNull(cdLineRegisterEntity)) {
//                        latch.countDown();
//                        return;
//                    }
//                    CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
//                    cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
//                    cdLineIpProxyDTO.setLzPhone(materialPhoneEntities.get(0).getContactKey());
//                    String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
//                    if (StrUtil.isEmpty(proxyIp)) {
//                        latch.countDown();
//                        return;
//                    }
//                    CreateGroupMax createGroupMax = new CreateGroupMax();
//                    createGroupMax.setUserMidList(mids);
//                    createGroupMax.setProxy(proxyIp);
//                    createGroupMax.setGroupName(cdGroupTasksEntity.getGroupName());
//                    createGroupMax.setToken(cdLineRegisterEntity.getToken());
//                    LineRegisterVO lineRegisterVO = lineService.createGroupMax(createGroupMax);
//                    if (ObjectUtil.isNotNull(lineRegisterVO) && 200 == lineRegisterVO.getCode()) {
//                        cdGroupTasksEntity.setTaskId(lineRegisterVO.getData().getTaskId());
//                        cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus3.getKey());
//                        tasksEntities.add(cdGroupTasksEntity);
//                    }
//                    latch.countDown();
//                }));
//            }
//
//            latch.await();
//            if (CollUtil.isNotEmpty(tasksEntities)) {
//                synchronized (lockObj3) {
//                    cdGroupTasksService.updateBatchById(tasksEntities);
//                }
//            }
//        }catch (Exception e){
//
//        }finally {
//            task8Lock.unlock();
//        }
//
//    }
//
//    static ReentrantLock task7Lock = new ReentrantLock();
//    @Autowired
//    ThreadPoolTaskExecutor threadPoolTaskExecutor;
//
//    @Autowired
//    private CdLineIpProxyService cdLineIpProxyService;
//
//    static ReentrantLock task6Lock = new ReentrantLock();
//
//
//    static ReentrantLock task4Lock = new ReentrantLock();
//
//    /**
//     * 加失败的自动重新分配
//     */
//    @Scheduled(fixedDelay = 5000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task4() {
//        boolean b = task4Lock.tryLock();
//        if (!b) {
//            return;
//        }
//        try {
//            //获取当前需要同步通讯的任务
//            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
//                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
//                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus12.getKey())
//                    .last("limit 1")
//            );
//
//            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
//                log.info("GroupTask task6 list isEmpty");
//                return;
//            }
//            //任务ids
//            List<Integer> ids = cdGroupTasksEntities.stream().map(CdGroupTasksEntity::getId).collect(Collectors.toList());
//            List<CdMaterialPhoneEntity> list = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
//                    .in(CdMaterialPhoneEntity::getGroupTaskId,ids)
//            );
//            Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = list.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));
//
//            //获取200个账号
//            LambdaQueryWrapper<CdLineRegisterEntity> last = new QueryWrapper<CdLineRegisterEntity>().lambda()
//                    .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey())
//                    .orderByDesc(CdLineRegisterEntity::getId)
//                    .last("limit " + 200);
//3459	投資初心者育成所-27			c43093886e627742e23b5c0a0983f0c6c			78	0	77	7	1	2024-04-04 02:50:01	bbea6932-5218-4963-b1bb-9f141a50e17d	948	1			5
//3460	投資初心者育成所-28			c21d78b2eeb986b37ac2f571fdbd475dc			78	0	77	7	1	2024-04-04 02:50:01	bd461d48-dd75-49b4-8a48-b8cda750ee81	947	1			5
//3461	投資初心者育成所-29			cf70a95a8a2f8591b1484a12fcc851d90			78	0	69	9	1	2024-04-04 02:50:01	2fa61fe4-8000-427a-a43e-dc675c31ac3f	946	1			5
//3462	投資初心者育成所-30			c9c956242758048f4bf168120e921712a			78	0	78	7	1	2024-04-04 02:50:01	d66e73ed-3dfc-40da-878e-b6e830872ca0	945	1			5
//3463	投資初心者育成所-31			c12f24fba38bb3d0a6e9a86fb3d830ffd			78	0	76	7	1	2024-04-04 02:50:01	68329005-e0b9-485e-909d-55a961424690	944	1			5
//3464	投資初心者育成所-32			c24aacf7e3d2232658f0cc27252c74da5			78	0	76	7	1	2024-04-04 02:50:01	3e1659d7-e754-4021-893a-46a40d9e4a01	943	1			5
//3465	投資初心者育成所-33						78	0	0	8	1	2024-04-04 02:50:01		937	1			5
//3466	投資初心者育成所-34			c7e4f431fd542d3f61e6128e16139484b			78	0	79	7	1	2024-04-04 02:50:01	b6266e4d-a7de-4b81-b27c-4602afe3162e	936	1			5
//3467	投資初心者育成所-35			c9b37e26a76281bede51eafea6a59b68e			78	0	74	3	1	2024-04-04 02:50:01	42543eaf-f125-44ce-b950-754d801fb201	935	1			5
//3468	投資初心者育成所-36			c754ae68442c41b77d430c613b9628fba			78	0	59	8	1	2024-04-04 02:50:01	559fa671-3229-4d54-b745-615f0c03ee95	938	1			5
//3469	投資初心者育成所-37			cda4c8f6f8f34fd1130b4c22789dc6b9e			78	0	64	8	1	2024-04-04 02:50:01	50f49e72-6891-46f1-9ebb-e8789278cc5c	949	1			5
//3470	投資初心者育成所-38			c73b4bebe9f9f2ccae9b42e69add0c381			78	0	79	8	1	2024-04-04 02:50:01	b9a09fa5-441d-41c3-b76e-a8ce13bb2ea8	954	1			5
//3471	投資初心者育成所-39			c0e07a6799ca02c67d3a5dd9a0a795fd7			78	0	65	7	1	2024-04-04 02:50:01	b38fdd4b-48e3-466a-8379-efa5ecd62806	953	1			5
//3472	投資初心者育成所-40			c9b9f93379f868a3faa9ac166608cca8a			78	0	70	7	1	2024-04-04 02:50:01	f86730d3-b623-4a42-bd8f-b9b579d35094	952	1			5


//3349	一から始める投資クラブ-1						75	0	0	8	1	2024-04-04 02:46:00	40c7e5c5-4c4f-49c4-88b2-0163fba34c05	934	1			5
//        3350	一から始める投資クラブ-2			c49103983ce58a5d508cc45b70c4561d7			75	0	73	9	1	2024-04-04 02:46:00	c235e807-02f5-486b-9dc0-888eceb2537c	933	1			5
//        3351	一から始める投資クラブ-3			c0019f53b959711593a05c57284435a86			75	0	74	9	1	2024-04-04 02:46:00	d369b84b-8bc0-4a9a-9d67-c0d86e17ce1a	932	1			5
//        3352	一から始める投資クラブ-4			c52de0d57cdc167a92be324347b31b148			75	0	76	9	1	2024-04-04 02:46:00	4dc11f36-9bbe-4f8b-9b13-b24e48cee9fa	931	1			5
//        3353	一から始める投資クラブ-5			c80dbd12348aefee4e35d2e3a041b09a8			75	0	82	9	1	2024-04-04 02:46:00	6aacb4f0-7bfb-423a-a052-ca0567643433	930	1			5
//        3354	一から始める投資クラブ-6			c16c46099afd3a9183c0f2ceb3b5dfd3b			75	0	80	9	1	2024-04-04 02:46:00	f01f7ace-4202-4e0c-8774-2e47bc05037e	929	1			5
//        3355	一から始める投資クラブ-7			c05f3ebb958254ca18a6dda273751164e			75	0	82	9	1	2024-04-04 02:46:00	a88b5bf0-8c23-41ea-b166-b0403bf7477e	928	1			5
//        3356	一から始める投資クラブ-8			cd418d8815b3626023cabe2bb3df9c4e4			75	0	83	9	1	2024-04-04 02:46:00	6f0c296c-de6b-47f2-9c38-a48a420ab765	927	1			5
//        3357	一から始める投資クラブ-9			cfb66a4301a3eb79931b3a07a2115e825			75	0	75	9	1	2024-04-04 02:46:00	e456ff3c-f6be-497c-9aed-0895a1e6343f	926	1			5
//        3358	一から始める投資クラブ-10			ce54c108d5c175eef78d9698837c023ed			75	0	84	9	1	2024-04-04 02:46:00	12bcb80a-1ea3-4a34-9d2e-ec825ee25d40	925	1			5
//        3359	一から始める投資クラブ-11			c7f22d4480d51931913f2df32053d4bcf			75	0	78	9	1	2024-04-04 02:46:00	5cd595fc-718f-4b58-8a58-44791dd20a88	924	1			5
//        3360	一から始める投資クラブ-12			c27ead29892d7668b1fe130d61090fe89			75	0	74	9	1	2024-04-04 02:46:00	d58e137c-9799-42fe-af38-8fa69deed29d	923	1			5
//        3361	一から始める投資クラブ-13			ce2e5b3252ef3c6769bbf893544efe11f			75	0	75	9	1	2024-04-04 02:46:00	6a80cfeb-723e-4fe9-af09-d0882eaeb3e1	922	1			5
//        3362	一から始める投資クラブ-14			c795e92689c306ad2c016354d257d0101			75	0	77	9	1	2024-04-04 02:46:00	8d17cc54-64d8-4161-b5d2-c3af71a9ea5b	921	1			5
//        3363	一から始める投資クラブ-15			cd778d7664b91dd75ffe05b87f70249a4			75	0	74	9	1	2024-04-04 02:46:01	f0119052-40cd-4b4b-8efd-3a7f5378034c	866	1			5
//        3364	一から始める投資クラブ-16						75	0	0	8	1	2024-04-04 02:46:01	18ed8d84-431c-43a1-a7c6-04e017983704	705	1			5
//        3365	一から始める投資クラブ-17			cbe36b466ba32153965f284f61d4dd3b3			75	0	77	9	1	2024-04-04 02:46:01	45dc24cb-4099-41d4-a3eb-f8f598a474ba	800	1			5
//        3366	一から始める投資クラブ-18			c9e44a7e4d217c8a9669f2ecca1610366			75	0	80	9	1	2024-04-04 02:46:01	21c9622b-ca3f-4641-a892-c8ad696b52e1	799	1			5
//        3367	一から始める投資クラブ-19						75	0	0	8	1	2024-04-04 02:46:01		798	1			5
//        3368	一から始める投資クラブ-20						75	0	0	8	1	2024-04-04 02:46:01		797	1			5
//        3369	一から始める投資クラブ-21			cd4322a7321dee1d9c9f2eac5f149f49e			75	0	75	9	1	2024-04-04 02:46:01	d6a61d4f-62b3-4539-aa31-3ee51e7f0845	796	1			5
//        3370	一から始める投資クラブ-22			c8a83778e490bd100aca1d57f594bdddc			75	0	81	9	1	2024-04-04 02:46:01	6406f85d-f275-408e-9961-a6e8f1e0775d	795	1			5
//        3371	一から始める投資クラブ-23			c08935bc36f8dc2762dd57b9cb6645bdd			75	0	77	9	1	2024-04-04 02:46:01	c263b6f0-090d-4e96-a1ad-4db040fde29e	794	1			5
//        3372	一から始める投資クラブ-24			c9750619b2bc3105a7402bac54f65095f			75	0	77	9	1	2024-04-04 02:46:01	a671ae0e-0461-479f-b279-ebc7856c5cdd	793	1			5
//        3373	一から始める投資クラブ-25			c7d310346255e4e7093c57fe009f5d135			75	0	75	9	1	2024-04-04 02:46:01	f8f047e6-97f6-4f0f-96a6-ba53a4d0bbca	792	1			5
//        3374	一から始める投資クラブ-26						75	0	0	8	1	2024-04-04 02:46:01	d5fc9b7a-c562-460d-8172-61287957f6d1	704	1			5
//        3375	一から始める投資クラブ-27			c60f25b15469705f001d8558272884f35			75	0	77	9	1	2024-04-04 02:46:01	dcf1615f-42d0-48d1-997a-7bf3b79fe599	790	1			5
//        3376	一から始める投資クラブ-28			c4e886ff25a17474187dd8d6c7ece41b6			75	0	79	9	1	2024-04-04 02:46:01	13636a43-8eba-48f3-ada8-73c3634b4ec1	789	1			5
//        3377	一から始める投資クラブ-29			c5d55194beb966f7a4c3d0bc7ef1f9e72			75	0	81	9	1	2024-04-04 02:46:01	e7afb9f2-e906-42e4-83bf-f4b20b6e7326	788	1			5
//        3378	一から始める投資クラブ-30			c23f171028390817ea279796d76ea7038			75	0	79	9	1	2024-04-04 02:46:01	94799bb1-bfa5-4372-9686-43bd0d3cf5bb	787	1			5
//        3379	一から始める投資クラブ-31			c169e6d83d888a5fd32b0e2968a88ce7a			75	0	75	9	1	2024-04-04 02:46:01	178241e6-c81e-4d5d-b2fc-72143a0cc553	786	1			5
//        3380	一から始める投資クラブ-32			c91db6e81293925eee35fa77289ea0f60			75	0	77	9	1	2024-04-04 02:46:01	ad36586f-d486-4ad6-b114-fd22901be6ac	785	1			5
//        3381	一から始める投資クラブ-33			cb8d70785c2deb7d26a22193fab36de76			75	0	81	9	1	2024-04-04 02:46:01	d6b9617f-86c8-4044-aac4-9c01736b964d	784	1			5
//        3382	一から始める投資クラブ-34			cc347c345dcada6f7165fa21be3354368			75	0	80	9	1	2024-04-04 02:46:01	3d56813f-0b66-4367-9fe7-a562c3c01a95	783	1			5
//        3383	一から始める投資クラブ-35			c1d719a0744a98dac90e9d55eb119b095			75	0	75	9	1	2024-04-04 02:46:01	2b128c9f-de34-4b6d-9419-6a5e97a175e9	782	1			5
//        3384	一から始める投資クラブ-36			c5eb5af9cfe7d58fe12d7e0d3b5fcdef0			75	0	78	9	1	2024-04-04 02:46:01	179484b6-01b8-492b-9b3d-901792ff6f0f	781	1			5
//        3385	一から始める投資クラブ-37			c8322fd110c1a28fc30e906fdc7bbd1ef			75	0	78	9	1	2024-04-04 02:46:01	a57ee576-2f87-487d-bab1-f7f67b0420bb	780	1			5
//        3386	一から始める投資クラブ-38			cc923ed6bea4e41845d70890224b9a185			75	0	80	9	1	2024-04-04 02:46:01	7f005637-3338-4904-9c77-79fc2db54ad6	779	1			5
//        3387	一から始める投資クラブ-39			c57305823dd10014f09d9c84ff8904de5			75	0	75	9	1	2024-04-04 02:46:01	5890c139-be9a-4836-a1ae-6e5f52244d14	778	1			5
//        3388	一から始める投資クラブ-40			c8f7a52ed3258e2a702c6415ca6663c0d			75	0	78	9	1	2024-04-04 02:46:01	6532b53c-07bd-410d-975f-d58981e5a22d	777	1			5
//        3389	一から始める投資クラブ-41			ccea688eb37c946109d0ded90a9aa286b			75	0	78	9	1	2024-04-04 02:46:01	0c45edbe-d1d5-4515-8ab2-5da4e49f5c38	703	1			5
//        3390	一から始める投資クラブ-42			cffef0bc46c0b4a2ecfcd8818b200d24f			75	0	81	9	1	2024-04-04 02:46:01	29514518-dc8c-4b7f-94a4-4a916bfc8eaa	775	1			5
//        3391	株新人クラブ-1			c37d976ce099bd08fe8f25093b80f35b0			77	0	81	9	1	2024-04-04 02:46:44	061d671f-4b31-4f19-9a22-47f5ec1d4585	774	1			5
//        3392	株新人クラブ-2						77	0	0	8	1	2024-04-04 02:46:44		773	1			5
//        3393	株新人クラブ-3			c495de79278fd25df927bbb8f105fde89			77	0	79	9	1	2024-04-04 02:46:44	a606fae7-fb6a-4f0d-a35f-da90609920f5	772	1			5
//        3394	株新人クラブ-4			c12edbb68989d517097b5315c1fe7d3ba			77	0	84	9	1	2024-04-04 02:46:44	7cf10092-66cb-40dd-8ec4-1b4d83c2405c	771	1			5
//        3395	株新人クラブ-5			cfac8b7b5b4c3bf27e54b75a0991f54dd			77	0	84	9	1	2024-04-04 02:46:44	fa7c9dee-1262-48ee-b265-a9125e5f655e	770	1			5
//        3396	株新人クラブ-6			cbb8278e960a172782158e4dad9c30e00			77	0	79	9	1	2024-04-04 02:46:44	944f25a3-597a-46fb-8c5f-7adc307bbe67	769	1			5
//        3397	株新人クラブ-7			ccd986c4aad7e3c5ca5fc355dd0728947			77	0	80	9	1	2024-04-04 02:46:44	ed283fee-e11e-4113-9bf1-2252e15e1c71	768	1			5
//        3398	株新人クラブ-8			c263d98887858a4b41c1c645bfaac6382			77	0	81	9	1	2024-04-04 02:46:44	64518614-d5f3-4846-8b0f-e360a9dfe1dd	767	1			5
//        3399	株新人クラブ-9			cdf7d977e33089ae8a1fea85ee9261d68			77	0	68	9	1	2024-04-04 02:46:44	9bb2c6bf-89b2-4903-b793-9ddad782da45	766	1			5
//        3400	株新人クラブ-10			cddb1e3c78cd84153546bb70d0c1fc2c1			77	0	82	9	1	2024-04-04 02:46:44	5d10eb9a-81d6-47d1-ab88-3108fc06bae0	765	1			5
//        3401	株新人クラブ-11			c66e03952256a11fcde5c24d6a71e4db6			77	0	78	9	1	2024-04-04 02:46:44	da4950c3-1ead-47e7-bd64-622a79b98b7e	764	1			5
//        3402	株新人クラブ-12			c26470589993de5bd81fd0e0aa7090f69			77	0	82	9	1	2024-04-04 02:46:44	f062a2f4-849a-4d78-ad01-13bcd1e347d8	763	1			5
//        3403	株新人クラブ-13			cfb5782f540b0fd17f58d251b8155fcea			77	0	84	9	1	2024-04-04 02:46:44	20d6f101-17dc-4fc9-aa83-b558cd3fbb50	762	1			5
//        3404	株新人クラブ-14			c3c453f2508e902a3ad26319251c5d75f			77	0	75	9	1	2024-04-04 02:46:44	2c68fd4f-74cc-4667-8179-b34bb5db5962	761	1			5
//        3405	株新人クラブ-15			ca6478c4a99e1e4840e02e7a6fe9ca21a			77	0	83	9	1	2024-04-04 02:46:44	8ef48f17-0696-41a2-bcba-571ef837642b	760	1			5
//        3406	株新人クラブ-16			c253fc9708ddad1b01aa71e133d667c6b			77	0	79	9	1	2024-04-04 02:46:44	aabb99c5-9a63-4dd2-8719-d7ab38baa01f	759	1			5
//        3407	株新人クラブ-17			cd65614f4582df36f1ffdcd0e03d97b20			77	0	77	9	1	2024-04-04 02:46:44	0c3615cf-fa20-4553-a22c-7aa8f2364432	758	1			5
//        3408	株新人クラブ-18			caca17110595d79450a21386535951f8a			77	0	79	9	1	2024-04-04 02:46:44	8f57e581-ca24-48e6-b1db-a87be2a44bbf	757	1			5
//        3409	株新人クラブ-19			c5610108d978b8f78970cbac251f7871f			77	0	78	9	1	2024-04-04 02:46:44	4d7d7638-0c5b-4e1d-ab26-7d12650f49f7	756	1			5
//        3410	株新人クラブ-20			c32a3a4e5860c528f37238781f8587e39			77	0	78	9	1	2024-04-04 02:46:44	b98b22fd-da77-41e2-aee6-925a4f552acf	755	1			5
//        3411	株新人クラブ-21			c2186275dffe2236ec7d5c4470ae6a6dc			77	0	83	9	1	2024-04-04 02:46:44	f651a974-3a23-485a-8480-004a33b69e0a	754	1			5
//        3412	株新人クラブ-22			c55e9786ccc4081dda8e4ac2eef10834b			77	0	79	9	1	2024-04-04 02:46:44	89bbe642-54cf-4e83-b30b-9f67db4109d9	753	1			5
//        3413	株新人クラブ-23			c7f65ec45b84dd00b058ce1c00c330dc3			77	0	77	9	1	2024-04-04 02:46:44	e71bfe1f-dc1f-48c9-96e2-3149823af3df	752	1			5
//        3414	株新人クラブ-24			c55c0db71a560fba3caab7072b3474f27			77	0	78	9	1	2024-04-04 02:46:44	e0752bb6-b185-418d-8da2-013161c0a1c0	751	1			5
//        3415	株新人クラブ-25			c08cbf41036c58f2990e7a55c82516b77			77	0	85	9	1	2024-04-04 02:46:44	716fdd49-46da-45ba-8cf0-82a93e1b0c2a	750	1			5
//        3416	株新人クラブ-26			caf348a974862220f7274cbab3ed9e9aa			77	0	81	9	1	2024-04-04 02:46:44	538515f6-9ff9-4a68-a14c-b9c4438e89a9	749	1			5
//        3417	株新人クラブ-27			c932a0c315de7376d4fa5aad0aab3d08b			77	0	78	9	1	2024-04-04 02:46:44	820541ac-22cc-440c-95f1-22f7dace9a10	748	1			5
//        3418	株新人クラブ-28			c1e99eaf2e1f0c836b34e44c4671ad174			77	0	75	9	1	2024-04-04 02:46:44	7a189e88-38ca-4065-9397-33478d1dd436	747	1			5
//        3419	株新人クラブ-29			ce750e18a5c07fb56151295b22ffac682			77	0	77	9	1	2024-04-04 02:46:44	c778458d-9f92-49a5-b60b-509e63777ea9	746	1			5
//        3420	株新人クラブ-30			c5351c127db8ebcf942e562954d41765e			77	0	78	9	1	2024-04-04 02:46:44	16a5bc1f-5c9d-4c2a-8d9c-0c5c42167724	745	1			5
//        3421	株新人クラブ-31			c1ec8919e000ebf3ba1bb6019680185f4			77	0	73	9	1	2024-04-04 02:46:44	aca9446c-8351-4c97-803d-03fe8fb5a0c5	744	1			5
//        3422	株新人クラブ-32			cfbbaaf194b30df840fe69f8bacfef6ee			77	0	78	9	1	2024-04-04 02:46:44	bdafa7bb-491a-4fa6-9e4c-a970d581c821	743	1			5
//        3423	株新人クラブ-33			c038929beca245f0a814b568285396b15			77	0	79	9	1	2024-04-04 02:46:44	becf0dfd-733f-4d07-aa38-1872cbefcff0	742	1			5
//        3424	株新人クラブ-34			c39830bfaf4a4f13754ac9c36c7489725			77	0	83	9	1	2024-04-04 02:46:44	38e8f2cb-1c33-4a58-a615-2262a011056f	741	1			5
//        3425	株新人クラブ-35			cc964214b8f1dd1956891d837fa5ac669			77	0	74	9	1	2024-04-04 02:46:44	629a5bbb-5713-431a-88bf-9dec25f6f584	740	1			5
//        3426	株新人クラブ-36			c71c9501cadc2143c7150b64e6dd2fdcc			77	0	80	9	1	2024-04-04 02:46:44	829ffde3-0673-4b09-97ae-10ed06cf730f	702	1			5
//        3427	株新人クラブ-37			ccad4c2a56de8f9328abcbc4f77cd4d6a			77	0	70	9	1	2024-04-04 02:46:45	dd1a13d9-bd38-423d-bcec-9e72708f6bc2	738	1			5
//        3428	株新人クラブ-38			c724205354df688a9806f95b8b9210379			77	0	77	9	1	2024-04-04 02:46:45	1b5ae78c-3ca6-4202-95a0-b9635df3ac55	737	1			5
//        3429	株新人クラブ-39			c49cc31dc6ae51d10321aec0059a14f36			77	0	81	9	1	2024-04-04 02:46:45	0def2a08-5c51-450c-b767-cb19ae33fa51	736	1			5
//        3430	株新人クラブ-40			c77f0fa7f5b0bfb0e22eacd475754eda9			77	0	80	9	1	2024-04-04 02:46:45	2b5e36eb-2be3-4d1c-bf3f-d13377f264b8	735	1			5
//        3431	株新人クラブ-41			cd126f36eadb49f6230a4c2aa1480ed1b			77	0	78	9	1	2024-04-04 02:46:45	05d0a7d6-956e-4587-8223-a0aaf6ec3493	734	1			5
//        3432	株新人クラブ-42			c87054816d6754d7d04edc5e5c0956909			77	0	84	9	1	2024-04-04 02:46:45	98ff7349-bd0a-42df-91ba-787483abd0c6	733	1			5
//        3433	投資初心者育成所-1			c71daa02c8d6aec5bb6343022253ab582			78	0	79	9	1	2024-04-04 02:50:00	bcb852e8-0bdd-4322-9031-3d8250ac52c7	732	1			5
//        3434	投資初心者育成所-2			c65b4e2ae4f6e0d115e7d0670d4ee30d4			78	0	77	9	1	2024-04-04 02:50:00	78c5ad28-ffea-4855-8fff-13320f073582	731	1			5
//        3435	投資初心者育成所-3			cc097d290f3b6dddc35ffa6ccda833a33			78	0	83	9	1	2024-04-04 02:50:00	4a51971f-4352-462d-8481-7090dd95fd97	730	1			5
//        3436	投資初心者育成所-4			c4164d565cdbe73f0fde126f8e43f5cc9			78	0	82	9	1	2024-04-04 02:50:00	c22ee156-1f6b-4112-884d-b37d2592340e	729	1			5
//        3437	投資初心者育成所-5			c5601103589bea03a9f192384210ba943			78	0	81	9	1	2024-04-04 02:50:00	b8439537-94a0-46f1-8525-acef5c04ce27	728	1			5
//        3438	投資初心者育成所-6			c6871ed37ac552b9c86f7209f8534500d			78	0	83	9	1	2024-04-04 02:50:00	afc493c3-a5eb-48fc-bc31-7276c0be6577	727	1			5
//        3439	投資初心者育成所-7			cf4aae8813d35741587b296aa2a9b076a			78	0	80	9	1	2024-04-04 02:50:00	a5c0dfa9-f350-4839-9856-53dbbc49f890	726	1			5
//        3440	投資初心者育成所-8			ce3180b1da0de90f73fdf8f8fdced8949			78	0	82	9	1	2024-04-04 02:50:00	a65ccb4e-06d9-4e42-aab1-156f0ccd0fab	725	1			5
//        3441	投資初心者育成所-9			c659fe5a9d7164d0ae1aff4f87f3cdbf9			78	0	86	9	1	2024-04-04 02:50:00	7038f049-0d99-4a89-97ac-0bece0209340	724	1			5
//        3442	投資初心者育成所-10			ca49b446ddd36ca631efff78396100c8c			78	0	77	9	1	2024-04-04 02:50:00	513da5c9-fd61-4baa-ad2f-2b89ebabea6f	723	1			5
//        3443	投資初心者育成所-11			c1a65eab4176bff4344046542cc1cf671			78	0	80	9	1	2024-04-04 02:50:00	70fb410d-bbd3-40bc-ace1-086b49e7fabc	722	1			5
//        3444	投資初心者育成所-12			c311a3fc7c02acab42b5ab63ebbb3b6d8			78	0	87	9	1	2024-04-04 02:50:00	bfaac080-5cf4-4b3a-a0f3-344cc54fff18	721	1			5
//        3445	投資初心者育成所-13			c1d9ce4acf449ec1251333766e5f45279			78	0	84	9	1	2024-04-04 02:50:00	e2a0e71a-45c1-44ae-9c59-b4f6028309a4	720	1			5
//        3446	投資初心者育成所-14			c420c7e941f693fb8f0d261afdf247ef5			78	0	88	9	1	2024-04-04 02:50:00	5e4b2383-364f-4492-b74c-7d9951e02fef	719	1			5
//        3447	投資初心者育成所-15						78	0	0	8	1	2024-04-04 02:50:00		718	1			5
//        3448	投資初心者育成所-16			cfa7255f05aa59f3ce1ef7c636b82047f			78	0	79	9	1	2024-04-04 02:50:00	d96d387e-686e-4b24-8840-f454ae9a43da	717	1			5
//        3449	投資初心者育成所-17			c89293099610989a5cfa1f12f4589e5b5			78	0	83	9	1	2024-04-04 02:50:00	3fd666a1-fa14-4cf1-82c0-d0a277229585	716	1			5
//        3450	投資初心者育成所-18			c6e300125becc1d2b068db1fb0625d02a			78	0	86	9	1	2024-04-04 02:50:00	da751e24-8371-4287-9c10-e3d6ce32e1bc	715	1			5
//        3451	投資初心者育成所-19			ccd1d42890f1f20ea028bd8e6d5728487			78	0	82	9	1	2024-04-04 02:50:00	5d9a0eb3-c36a-40f5-b06d-0a878c8e36c0	701	1			5
//        3452	投資初心者育成所-20			c5077fb619ac9a27b5fec4609c7293fcc			78	0	86	9	1	2024-04-04 02:50:01	65d52862-f4dc-4db9-989b-3ab7727f0fd8	713	1			5
//        3453	投資初心者育成所-21			c59c8d615ed2e114e02d2db176fe4ef1b			78	0	83	9	1	2024-04-04 02:50:01	f0e3b1f9-d1ca-40fe-99f3-fd962192b8e3	712	1			5
//        3454	投資初心者育成所-22			c9848d24d66935f58182bbc6585b86d96			78	0	80	9	1	2024-04-04 02:50:01	33a791a8-5124-4635-8594-5b41566093cf	711	1			5
//        3455	投資初心者育成所-23			c79f363bb93dbb19df04fdfd30da79365			78	0	86	9	1	2024-04-04 02:50:01	1aab32f3-dd4f-4f61-af7d-0c2be16d90e6	710	1			5
//        3456	投資初心者育成所-24			c7ac28a6bb964cc4bd4fba63f0d6bbcfc			78	0	79	9	1	2024-04-04 02:50:01	c4bd94d7-4020-4ab7-afb4-32ac45a4ffa3	709	1			5
//        3457	投資初心者育成所-25			cd1d8f938b8ab4d36f18a6b58deb4b775			78	0	72	9	1	2024-04-04 02:50:01	5df93abe-3e15-45a0-a990-940d47f41005	708	1			5
//        3458	投資初心者育成所-26			cb49ad0bd006934f4412e096dd2916f02			78	0	81	9	1	2024-04-04 02:50:01	1fa05ea9-a103-40eb-be8a-3b8b8a297fd9	700	1			5
//        3459	投資初心者育成所-27			c43093886e627742e23b5c0a0983f0c6c			78	0	77	9	1	2024-04-04 02:50:01	bbea6932-5218-4963-b1bb-9f141a50e17d	948	1			5
//        3460	投資初心者育成所-28			c21d78b2eeb986b37ac2f571fdbd475dc			78	0	77	9	1	2024-04-04 02:50:01	bd461d48-dd75-49b4-8a48-b8cda750ee81	947	1			5
//        3461	投資初心者育成所-29			cf70a95a8a2f8591b1484a12fcc851d90			78	0	69	9	1	2024-04-04 02:50:01	2fa61fe4-8000-427a-a43e-dc675c31ac3f	946	1			5
//        3462	投資初心者育成所-30			c9c956242758048f4bf168120e921712a			78	0	78	9	1	2024-04-04 02:50:01	d66e73ed-3dfc-40da-878e-b6e830872ca0	945	1			5
//        3463	投資初心者育成所-31			c12f24fba38bb3d0a6e9a86fb3d830ffd			78	0	76	9	1	2024-04-04 02:50:01	68329005-e0b9-485e-909d-55a961424690	944	1			5
//        3464	投資初心者育成所-32			c24aacf7e3d2232658f0cc27252c74da5			78	0	76	9	1	2024-04-04 02:50:01	3e1659d7-e754-4021-893a-46a40d9e4a01	943	1			5
//        3465	投資初心者育成所-33			c55573acdd270db90f051fc42deec5b79			78	0	85	9	1	2024-04-04 02:50:01	b6574223-ac95-474e-9de8-e3a9b0b19d50	706	1			5
//        3466	投資初心者育成所-34			c7e4f431fd542d3f61e6128e16139484b			78	0	79	9	1	2024-04-04 02:50:01	b6266e4d-a7de-4b81-b27c-4602afe3162e	936	1			5
//        3467	投資初心者育成所-35			c07d9516a1d544ab2f9a6b57d5e368f63			78	0	75	9	1	2024-04-04 02:50:01	42543eaf-f125-44ce-b950-754d801fb201	935	1			5
//        3468	投資初心者育成所-36			c754ae68442c41b77d430c613b9628fba			78	0	59	9	1	2024-04-04 02:50:01	559fa671-3229-4d54-b745-615f0c03ee95	938	1			5
//        3469	投資初心者育成所-37			cda4c8f6f8f34fd1130b4c22789dc6b9e			78	0	64	9	1	2024-04-04 02:50:01	50f49e72-6891-46f1-9ebb-e8789278cc5c	949	1			5
//        3470	投資初心者育成所-38			c73b4bebe9f9f2ccae9b42e69add0c381			78	0	79	9	1	2024-04-04 02:50:01	b9a09fa5-441d-41c3-b76e-a8ce13bb2ea8	954	1			5
//        3471	投資初心者育成所-39			c0e07a6799ca02c67d3a5dd9a0a795fd7			78	0	65	9	1	2024-04-04 02:50:01	b38fdd4b-48e3-466a-8379-efa5ecd62806	953	1			5
//        3472	投資初心者育成所-40			c9b9f93379f868a3faa9ac166608cca8a			78	0	70	9	1	2024-04-04 02:50:01	f86730d3-b623-4a42-bd8f-b9b579d35094	952	1			5
//            List<CdLineRegisterEntity> cdLineRegisterEntities1 = cdLineRegisterService.list(last);
//            if (CollUtil.isEmpty(cdLineRegisterEntities1)) {
//                log.info("line token is empty donot fenpei");
//                return;
//            }
//            Queue<CdLineRegisterEntity> cdLineRegisterEntities2 = new LinkedList<>(cdLineRegisterEntities1);
//
//            List<CdLineRegisterEntity> cdLineRegisterEntitiesUpdate = new ArrayList<>();
//            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();
//            List<CdMaterialPhoneEntity> cdMaterialPhoneEntitiesUpdate = new ArrayList<>();
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                //获取所有失败的手机号
//                List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
//                CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities2.poll();
//                cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus7.getKey());
//                cdLineRegisterEntity.setGroupTaskId(cdGroupTasksEntity.getId());
//                cdLineRegisterEntitiesUpdate.add(cdLineRegisterEntity);
//
//                CdGroupTasksEntity update = new CdGroupTasksEntity();
//                update.setId(cdGroupTasksEntity.getId());
//                update.setGroupStatus(GroupStatus.GroupStatus1.getKey());
//                update.setLineRegisterId(cdLineRegisterEntity.getId());
//                cdGroupTasksEntitiesUpdate.add(update);
//
//                for (CdMaterialPhoneEntity cdMaterialPhoneEntity : materialPhoneEntities) {
//                    cdMaterialPhoneEntity.setLineRegisterId(cdLineRegisterEntity.getId());
//                    cdMaterialPhoneEntity.setGroupTaskId(cdGroupTasksEntity.getId());
//                    cdMaterialPhoneEntity.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus1.getKey());
//                    cdMaterialPhoneEntitiesUpdate.add(cdMaterialPhoneEntity);
//                }
//            }
//
//            if (CollUtil.isNotEmpty(cdMaterialPhoneEntitiesUpdate)) {
//                synchronized (lockPhoneObj) {
//                    cdMaterialPhoneService.updateBatchById(cdMaterialPhoneEntitiesUpdate);
//                }
//            }
//            if (CollUtil.isNotEmpty(cdGroupTasksEntitiesUpdate)) {
//                synchronized (lockObj3) {
//                    cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
//                }
//            }
//
//            if (CollUtil.isNotEmpty(cdLineRegisterEntitiesUpdate)) {
//                synchronized (lockRegisterObj3) {
//                    cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesUpdate);
//                }
//            }
//        }catch (Exception e) {
//            log.error("err = {}",e.getMessage());
//        }finally {
//            task4Lock.unlock();
//        }
//    }
//
//
//    private final ConcurrentHashMap<Integer, Lock> lockMap = new ConcurrentHashMap<>();
//
//
//
//    //开始 同步手机通讯录
//    @Scheduled(fixedDelay = 1000)
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    public void task6() {
//        boolean b = task6Lock.tryLock();
//        if (!b) {
//            log.info("task6Lock 在执行任务");
//            return;
//        }
//        try {
//            //获取当前需要同步通讯的任务
//            List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
//                    .eq(CdGroupTasksEntity::getAddType,AddType.AddType2.getKey())
//                    .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus2.getKey())
//                    .last("limit 80")
//            );
//
//            if (CollUtil.isEmpty(cdGroupTasksEntities)) {
//                log.info("GroupTask task6 list isEmpty");
//                return;
//            }
//            //获取所有的任务id
//            List<Integer> ids = new ArrayList<>();
//            //所有的账号id
//            List<Integer> lineId = new ArrayList<>();
//            for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {
//                ids.add(cdGroupTasksEntity.getId());
//                lineId.add(cdGroupTasksEntity.getLineRegisterId());
//            }
//            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.groupByIds(ids);
//
//            List<Integer> cdMaterialPhoneIds = cdMaterialPhoneEntities.stream().map(CdMaterialPhoneEntity::getId).collect(Collectors.toList());
//            List<Integer> getGroupTaskIds = cdMaterialPhoneEntities.stream().map(CdMaterialPhoneEntity::getGroupTaskId).collect(Collectors.toList());
//            List<Integer> difference = new ArrayList<>(ids);
//            difference.removeAll(getGroupTaskIds);
//            //
//            List<CdGroupTasksEntity> cdGroupTasksEntityArrayListU = new ArrayList<>();
//
//            if (CollUtil.isNotEmpty(difference)) {
//                for (Integer i : difference) {
//                    CdGroupTasksEntity cdGroupTasksEntity = new CdGroupTasksEntity();
//                    cdGroupTasksEntity.setId(i);
//                    cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus7.getKey());
//                    cdGroupTasksEntityArrayListU.add(cdGroupTasksEntity);
//                }
//                cdGroupTasksService.updateBatchById(cdGroupTasksEntityArrayListU);
//            }
//
//            List<CdMaterialPhoneEntity> materialPhoneEntities = cdMaterialPhoneService.listByIds(cdMaterialPhoneIds);
//            if (CollUtil.isEmpty(materialPhoneEntities)) {
//                return;
//            }
//            //获取用户
//            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.listByIds(lineId);
//            Map<Integer, CdLineRegisterEntity> integerCdLineRegisterEntityMap = cdLineRegisterEntities.stream().collect(Collectors.toMap(CdLineRegisterEntity::getId, s -> s));
//
//
////            List<CdMaterialPhoneEntity> updates = new ArrayList<>();
//
//            final CountDownLatch latch = new CountDownLatch(materialPhoneEntities.size());
////            List<CdGroupTasksEntity> cdGroupTasksEntitiesUpdate = new ArrayList<>();
//            for (CdMaterialPhoneEntity materialPhoneEntity : materialPhoneEntities) {
////                if (DateUtil.date().before(materialPhoneEntity.getStartDate())) {
////                    log.info("id = {},还没到执行时间");
////                    latch.countDown();
////                    continue;
////                }
//                Lock lock = lockMap.computeIfAbsent(materialPhoneEntity.getId(), k -> new ReentrantLock());
//                threadPoolTaskExecutor.submit(new Thread(()->{
//                    if (lock.tryLock()) {
//                        try {
//                            if (StrUtil.isEmpty(materialPhoneEntity.getMid())) {
//                                CdMaterialPhoneEntity update = new CdMaterialPhoneEntity();
//                                update.setId(materialPhoneEntity.getId());
//                                update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus10.getKey());
////                        updates.add(update);
//                                cdMaterialPhoneService.updateById(update);
//                                latch.countDown();
//                                return;
//                            }
//                            //获取当前任务的运行账号
//                            CdLineRegisterEntity cdLineRegisterEntity = integerCdLineRegisterEntityMap.get(materialPhoneEntity.getLineRegisterId());
//                            if (ObjectUtil.isNull(cdLineRegisterEntity)) {
//                                latch.countDown();
//                                return;
//                            }
//
//                            CdLineIpProxyDTO cdLineIpProxyDTO = new CdLineIpProxyDTO();
//                            cdLineIpProxyDTO.setTokenPhone(cdLineRegisterEntity.getPhone());
//                            cdLineIpProxyDTO.setLzPhone(materialPhoneEntity.getContactKey());
//                            String proxyIp = cdLineIpProxyService.getProxyIp(cdLineIpProxyDTO);
//                            if (StrUtil.isEmpty(proxyIp)) {
//                                latch.countDown();
//                                return;
//                            }
//                            //如果是搜索模式
//                            if (AddFriendType.AddFriendType8.getValue().equals(materialPhoneEntity.getAddFriendType())) {
//                                if (addFriendByPhone(materialPhoneEntity, proxyIp, cdLineRegisterEntity, latch)) return;
//                            }else {
//                                //mid模式直接添加
//                                if (addFriendById(materialPhoneEntity, proxyIp, cdLineRegisterEntity, latch)) return;
//                            }
//                            Thread.sleep(7000);
//                        } catch (Exception e) {
//
//                        } finally {
//                            // 确保释放锁
//                            lock.unlock();
//                        }
//                    } else {
//                        // 获取锁失败，直接跳出
//                        log.info("Could not acquire lock for = {}",materialPhoneEntity.getId());
//                    }
//                }));
//            }
//            latch.await(50,TimeUnit.SECONDS);
////            if (CollUtil.isNotEmpty(updates)) {
////                synchronized (lockPhoneObj) {
////                    cdMaterialPhoneService.updateBatchById(updates);
////                }
////            }
////            if (CollUtil.isNotEmpty(cdGroupTasksEntitiesUpdate)) {
////                synchronized (lockObj3) {
////                    cdGroupTasksService.updateBatchById(cdGroupTasksEntitiesUpdate);
////                }
////            }
//        }catch (Exception e){
//
//        }finally {
//            task6Lock.unlock();
//        }
//    }
//
//    private boolean addFriendByPhone(CdMaterialPhoneEntity materialPhoneEntity, String proxyIp, CdLineRegisterEntity cdLineRegisterEntity, CountDownLatch latch) {
//        SearchPhoneDTO searchPhoneDTO = new SearchPhoneDTO();
//        searchPhoneDTO.setProxy(proxyIp);
//        searchPhoneDTO.setPhone(materialPhoneEntity.getContactKey());
//        searchPhoneDTO.setToken(cdLineRegisterEntity.getToken());
//        SearchPhoneVO andAddContactsByPhone = lineService.searchPhone(searchPhoneDTO);
//
//        CdMaterialPhoneEntity update = new CdMaterialPhoneEntity();
//        update.setId(materialPhoneEntity.getId());
//        update.setVideoProfile(searchPhoneDTO.getProxy());
//        if (ObjectUtil.isNotNull(andAddContactsByPhone)) {
//            if (200 != andAddContactsByPhone.getCode()) {
//                update.setErrMsg(StrUtil.concat(true, materialPhoneEntity.getErrMsg(),andAddContactsByPhone.getMsg()));
//                update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus12.getKey());
//                CdGroupTasksEntity cdGroupTasksEntityUpdate = new CdGroupTasksEntity();
//                cdGroupTasksEntityUpdate.setId(materialPhoneEntity.getGroupTaskId());
//                cdGroupTasksEntityUpdate.setGroupStatus(GroupStatus.GroupStatus11.getKey());
//                //cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntityUpdate);
//                cdGroupTasksService.updateById(cdGroupTasksEntityUpdate);
//                //updates.add(update);
//                cdMaterialPhoneService.updateById(update);
//                cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
//                latch.countDown();
//                return true;
//            }else if (300 == andAddContactsByPhone.getCode()){
//                update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus1.getKey());
//            }else {
//                Map<String, The818051863582> data = andAddContactsByPhone.getData();
//                if (CollUtil.isNotEmpty(data)) {
//                    The818051863582 the8180518635821 = data.values().stream().findFirst().get();
//                    if (ObjectUtil.isNotNull(the8180518635821)) {
//                        update.setMid(the8180518635821.getMid());
//                        AddFriendsByMid addFriendsByMid = getAddFriendsByMid(update, searchPhoneDTO);
//                        SearchPhoneVO searchPhoneVO = lineService.addFriendsByMid(addFriendsByMid);
//                        if (ObjectUtil.isNotNull(searchPhoneVO) && 200 == searchPhoneVO.getCode()) {
//                            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus9.getKey());
//                            update.setErrMsg(StrUtil.concat(true, materialPhoneEntity.getErrMsg(),searchPhoneVO.getMsg()));
//                        }else{
//                            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus10.getKey());
//                            CdGroupTasksEntity cdGroupTasksEntityUpdate = new CdGroupTasksEntity();
//                            cdGroupTasksEntityUpdate.setId(materialPhoneEntity.getGroupTaskId());
//                            cdGroupTasksEntityUpdate.setGroupStatus(GroupStatus.GroupStatus11.getKey());
//                            //cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntityUpdate);
//                            cdGroupTasksService.updateById(cdGroupTasksEntityUpdate);
//                            //updates.add(update);
//                            cdMaterialPhoneService.updateById(update);
//                            cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
//                            latch.countDown();
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        cdMaterialPhoneService.updateById(update);
//        latch.countDown();
//        return false;
//    }
//
//    private boolean addFriendById(CdMaterialPhoneEntity materialPhoneEntity, String proxyIp, CdLineRegisterEntity cdLineRegisterEntity, CountDownLatch latch) {
//        AddFriendsByHomeRecommendDTO addFriendsByMid = new AddFriendsByHomeRecommendDTO();
//        addFriendsByMid.setProxy(proxyIp);
//        addFriendsByMid.setMid(materialPhoneEntity.getMid());
//        addFriendsByMid.setToken(cdLineRegisterEntity.getToken());
//        SearchPhoneVO searchPhoneVO = lineService.addFriendsByHomeRecommend(addFriendsByMid, materialPhoneEntity.getAddFriendType());
//        CdMaterialPhoneEntity update = new CdMaterialPhoneEntity();
//        update.setId(materialPhoneEntity.getId());
//        if (ObjectUtil.isNull(searchPhoneVO)) {
//            latch.countDown();
//            return true;
//        }
//        update.setErrMsg(StrUtil.concat(true, materialPhoneEntity.getErrMsg(),searchPhoneVO.getMsg()));
//        update.setVideoProfile(proxyIp);
//        if (200 == searchPhoneVO.getCode()) {
//            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus9.getKey());
//        }else if (300 == searchPhoneVO.getCode()){
//            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus1.getKey());
//        }else {
//            update.setMaterialPhoneStatus(MaterialPhoneStatus.MaterialPhoneStatus10.getKey());
//            CdGroupTasksEntity cdGroupTasksEntityUpdate = new CdGroupTasksEntity();
//            cdGroupTasksEntityUpdate.setId(materialPhoneEntity.getGroupTaskId());
//            cdGroupTasksEntityUpdate.setGroupStatus(GroupStatus.GroupStatus11.getKey());
////                        cdGroupTasksEntitiesUpdate.add(cdGroupTasksEntityUpdate);
//            cdGroupTasksService.updateById(cdGroupTasksEntityUpdate);
////                        updates.add(update);
//            cdMaterialPhoneService.updateById(update);
//            cdLineRegisterService.unLock(cdLineRegisterEntity.getId());
//            latch.countDown();
//            return true;
//        }
////                    updates.add(update);
//        cdMaterialPhoneService.updateById(update);
//        latch.countDown();
//        return false;
//    }
//
//    private static AddFriendsByMid getAddFriendsByMid(CdMaterialPhoneEntity cdGroupSubtasksEntity, SearchPhoneDTO searchPhoneDTO) {
//        AddFriendsByMid addFriendsByMid = new AddFriendsByMid();
//        addFriendsByMid.setProxy(searchPhoneDTO.getProxy());
//        addFriendsByMid.setPhone(searchPhoneDTO.getPhone());
//        addFriendsByMid.setMid(cdGroupSubtasksEntity.getMid());
//        addFriendsByMid.setFriendAddType("phoneSearch");
//        addFriendsByMid.setToken(searchPhoneDTO.getToken());
//        return addFriendsByMid;
//    }
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
//            log.info("task1Lock 在执行任务");
//            return;
//        }
//        try {
//            List<CdGroupTasksEntity> list = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
//                    .eq(CdGroupTasksEntity::getGroupStatus, GroupStatus.GroupStatus1.getKey())
//                    .last("limit 80")
//            );
//            if (CollUtil.isEmpty(list)) {
//                log.info("GroupTask task1 list isEmpty");
//                return;
//            }
//
//            List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.list(new QueryWrapper<CdLineRegisterEntity>().lambda()
//                    .in(CdLineRegisterEntity::getRegisterStatus,RegisterStatus.RegisterStatus4.getKey())
//                    .eq(CdLineRegisterEntity::getOpenStatus,OpenStatus.OpenStatus3.getKey())
//                    .eq(CdLineRegisterEntity::getCountryCode,"th")
//                    .last("limit "+list.size())
//                    .orderByDesc(CdLineRegisterEntity::getId)
//            );
//
//            List<CdLineRegisterEntity> cdLineRegisterEntitiesNewUpdate = new ArrayList<>();
//
//            for (int i = 0; i < list.size(); i++) {
//                CdGroupTasksEntity cdGroupTasksEntity = list.get(i);
//                //如果类型是搜索，校验一下
//                if (AddType.AddType1.getKey().equals(cdGroupTasksEntity.getAddType())) {
//                    if (CollUtil.isEmpty(cdLineRegisterEntities) || cdLineRegisterEntities.size() != list.size()) {
//                        log.info("GroupTask task1 cdLineRegisterEntities isEmpty");
//                        return;
//                    }
//                    CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities.get(i);
//                    cdLineRegisterEntity.setGroupTaskId(cdGroupTasksEntity.getId());
//                    cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus7.getKey());
//                    cdLineRegisterEntitiesNewUpdate.add(cdLineRegisterEntity);
//                }
//                cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus2.getKey());
//            }
//            synchronized (lockObj3) {
//                cdGroupTasksService.updateBatchById(list);
//                if (CollUtil.isNotEmpty(cdLineRegisterEntitiesNewUpdate)) {
//                    cdLineRegisterService.updateBatchById(cdLineRegisterEntitiesNewUpdate);
//                }
//            }
//        }finally {
//            task1Lock.unlock();
//        }
//
//    }
//
//
//
//}
