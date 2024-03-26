package io.renren.modules.ltt.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.renren.common.validator.Assert;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.dto.AutoAssignGroupsDTO;
import io.renren.modules.ltt.dto.ImportZipDTO;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
import io.renren.modules.ltt.enums.*;
import io.renren.modules.ltt.service.*;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.ltt.dao.CdMaterialDao;
import io.renren.modules.ltt.entity.CdMaterialEntity;
import io.renren.modules.ltt.dto.CdMaterialDTO;
import io.renren.modules.ltt.vo.CdMaterialVO;
import io.renren.modules.ltt.conver.CdMaterialConver;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service("cdMaterialService")
@Game
public class CdMaterialServiceImpl extends ServiceImpl<CdMaterialDao, CdMaterialEntity> implements CdMaterialService {

    @Autowired
    private CdMaterialPhoneService cdMaterialPhoneService;
    @Autowired
    private CdLineRegisterService cdLineRegisterService;
    @Autowired
    private LineService lineService;
    @Autowired
    private CdGroupTasksService cdGroupTasksService;

    @Override
    public PageUtils<CdMaterialVO> queryPage(CdMaterialDTO cdMaterial) {
        IPage<CdMaterialVO> page = baseMapper.listPage(new Query<CdMaterialEntity>(cdMaterial).getPage(),cdMaterial);

        return PageUtils.<CdMaterialVO>page(page);
    }
    @Override
    public CdMaterialVO getById(Integer id) {
        return CdMaterialConver.MAPPER.conver(baseMapper.selectById(id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public byte[] exportSy(CdMaterialDTO cdMaterial) {
        //料子数据
        String materialText = HttpUtil.downloadString(cdMaterial.getMaterialUrl(), "UTF-8");
        String[] materialTextSplit = materialText.split("\n");
        Assert.isTrue(ArrayUtil.isEmpty(materialTextSplit),"txt不能为空");

        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities1 = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda());
        Set<String> collect = cdMaterialPhoneEntities1.stream().map(CdMaterialPhoneEntity::getContactKey).collect(Collectors.toSet());
        List<String> materialTextS = new ArrayList<>();
        for (String s : materialTextSplit) {
            s = s.replace("\r","").trim();
            String[] split = s.split("\t");
            if (split.length > 1) {
                s = split[0].trim();
            }
            if (collect.contains(s)) {
            }else {
                materialTextS.add(s);
            }
        }
        String collectByte = materialTextS.stream().map(phone -> phone + "\n").collect(Collectors.joining());
        return StrUtil.bytes(collectByte);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CdMaterialDTO cdMaterial) {
        //料子数据
        String materialText = HttpUtil.downloadString(cdMaterial.getMaterialUrl(), "UTF-8");
        String[] materialTextSplit = materialText.split("\n");
        Assert.isTrue(ArrayUtil.isEmpty(materialTextSplit),"txt不能为空");

        //已经存在的
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities1 = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda());
        Set<String> collect = cdMaterialPhoneEntities1.stream().map(CdMaterialPhoneEntity::getContactKey).collect(Collectors.toSet());
        //不存在的
        Set<String> materialTextS = new HashSet<>();
        for (String s : materialTextSplit) {
            s = s.replace("\r","").trim();
            String[] split = s.split("\t");
            String retS = "";
            if (split.length > 1) {
                retS = split[0].trim();
            }
            if (collect.contains(retS)) {
            }else {
                materialTextS.add(s);
            }
        }

        String remarkCopy = cdMaterial.getRemark();
        //水军分组
        List<String> navyUrlList = cdMaterial.getNavyUrlList();
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = new ArrayList<>();
        for (String s : navyUrlList) {
            //水军数据
            String navyText = HttpUtil.downloadString(s, "UTF-8");
            String[] navyTextSplit = navyText.split("\n");
            Assert.isTrue(ArrayUtil.isEmpty(navyTextSplit),"txt不能为空");
            int totalGroupPerson = 92;
            //剩余的人数
            int onlySize = totalGroupPerson - (navyTextSplit.length - 1);
            List<List<String>> materialTextSplitList = Lists.partition(materialTextS.stream().collect(Collectors.toList()), onlySize);
            List<List<String>> materialTextSplitList1 = materialTextSplitList.subList(0, cdMaterial.getNumber());
            List<List<String>> materialTextSplitList2 = materialTextSplitList.subList(cdMaterial.getNumber(), materialTextSplitList.size());

            materialTextS.clear();
            for (List<String> strings : materialTextSplitList2) {
                for (String string : strings) {
                    materialTextS.add(string);
                }
            }

            String groupName = navyTextSplit[0];
            cdMaterial.setRemark(String.format("%s#%s",remarkCopy,groupName));
            //分组之后的料子
            for (List<String> materialTextOnly : materialTextSplitList1) {
                if (materialTextOnly.size() < onlySize) {
                    continue;
                }
                Integer uploadNumber = navyTextSplit.length - 1 + materialTextOnly.size();
                //总上传
                cdMaterial.setUploadNumber(uploadNumber);
                //料子数量
                cdMaterial.setMaterialNumber(materialTextSplit.length);
                //水军数量
                cdMaterial.setNavyNumber(navyTextSplit.length - 1);
                cdMaterial.setSuccessesNumber(0);
                cdMaterial.setFailuresNumber(0);
                cdMaterial.setSynchronizationsNumber(0);
                cdMaterial.setCreateTime(DateUtil.date());
                cdMaterial.setDeleteFlag(DeleteFlag.NO.getKey());


                CdMaterialEntity cdMaterialEntity = CdMaterialConver.MAPPER.converDTO(cdMaterial);
                boolean save = this.save(cdMaterialEntity);



                DateTime now = DateUtil.date();
                DateTime end = DateUtil.offsetHour(now, 48);
                long between = DateUtil.between(end, now, DateUnit.SECOND, true);
                //下次添加好友的时间
                Long l = between / totalGroupPerson;

                for (String string : navyTextSplit) {
                    if (!StrUtil.containsAny(string,"+")) {
                        continue;
                    }
                    string = string.replace("\r","").trim();
                    String[] split = string.split("\t");
                    CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
                    if (split.length > 2) {
                        cdMaterialPhoneEntity.setContactKey(split[0].trim());
                        cdMaterialPhoneEntity.setMid(split[1].trim());
                        cdMaterialPhoneEntity.setDisplayName(split[2].trim());
                    }else if (split.length > 1) {
                        cdMaterialPhoneEntity.setContactKey(split[0].trim());
                        cdMaterialPhoneEntity.setMid(split[1].trim());
                    }else {
                        cdMaterialPhoneEntity.setContactKey(string);
                    }
                    cdMaterialPhoneEntity.setMaterialId(cdMaterialEntity.getId());
                    cdMaterialPhoneEntity.setMaterialType(MaterialType.MaterialType2.getKey());
                    cdMaterialPhoneEntity.setCreateTime(DateUtil.date());
                    cdMaterialPhoneEntity.setDeleteFlag(DeleteFlag.NO.getKey());
                    cdMaterialPhoneEntity.setStartDate(now);
                    now = DateUtil.offsetSecond(now,l.intValue());
                    cdMaterialPhoneEntities.add(cdMaterialPhoneEntity);
                }

                for (String string : materialTextOnly) {
                    string = string.replace("\r","").trim();
                    String[] split = string.split("\t");
                    CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
                    cdMaterialPhoneEntity.setMaterialId(cdMaterialEntity.getId());
                    if (split.length > 2) {
                        cdMaterialPhoneEntity.setContactKey(split[0].trim());
                        cdMaterialPhoneEntity.setMid(split[1].trim());
                        cdMaterialPhoneEntity.setDisplayName(split[2].trim());
                    }else if (split.length > 1) {
                        cdMaterialPhoneEntity.setContactKey(split[0].trim());
                        cdMaterialPhoneEntity.setMid(split[1].trim());
                    }else {
                        cdMaterialPhoneEntity.setContactKey(string);
                    }
                    cdMaterialPhoneEntity.setStartDate(now);
                    now = DateUtil.offsetSecond(now,l.intValue());
                    cdMaterialPhoneEntity.setMaterialType(MaterialType.MaterialType1.getKey());
                    cdMaterialPhoneEntity.setCreateTime(DateUtil.date());
                    cdMaterialPhoneEntity.setDeleteFlag(DeleteFlag.NO.getKey());
                    if (StrUtil.isEmpty(cdMaterialPhoneEntity.getContactKey())) {
                        continue;
                    }
                    cdMaterialPhoneEntities.add(cdMaterialPhoneEntity);
                }
            }
        }
        FileUtil.writeUtf8Lines(materialTextS,"/Users/chenweilong/Desktop/java代码/line_project/"+cdMaterial.getRemark()+"剩余.txt");
        cdMaterialPhoneService.saveBatch(cdMaterialPhoneEntities,cdMaterialPhoneEntities.size());
        return true;
    }

    @Override
    public boolean updateById(CdMaterialDTO cdMaterial) {
        CdMaterialEntity cdMaterialEntity = CdMaterialConver.MAPPER.converDTO(cdMaterial);
        return this.updateById(cdMaterialEntity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    int c = 600;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoAssignGroups(AutoAssignGroupsDTO cdMaterial) {

        //获取200个账号
        LambdaQueryWrapper<CdLineRegisterEntity> last = new QueryWrapper<CdLineRegisterEntity>().lambda()
                .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey())
                .orderByDesc(CdLineRegisterEntity::getId)
                .eq(CdLineRegisterEntity::getCountryCode, cdMaterial.getCountryCode())
                .last("limit " + 200);
//        if (MaterialPhoneType.MaterialType1.getKey().equals(cdMaterialVO.getType())) {
//            last.lt(CdLineRegisterEntity::getGroupCount, cdMaterial.getAccountGroupRestrictions());
//        }
        //注册账号 状态是拉过群或者是注册成功的 拉群次数小于限制数量的账号
        List<CdLineRegisterEntity> cdLineRegisterEntities1 = cdLineRegisterService.list(last);

        Queue<CdLineRegisterEntity> cdLineRegisterEntities2 = new LinkedList<>(cdLineRegisterEntities1);


        List<CdLineRegisterEntity> cdLineRegisterEntities = new ArrayList<>();
        List<Integer> ids = cdMaterial.getIds();
        Assert.isTrue(CollUtil.isEmpty(ids),"不存在料子");
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntitiesA = new ArrayList<>();
        for (Integer id : ids) {
            CdMaterialVO cdMaterialVO = this.getById(id);
            String[] split = cdMaterialVO.getRemark().split("#");
            if (split.length == 2) {
                cdMaterial.setGroupName(split[1]);
            }
            //获取 所有手机号
            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                            .eq(CdMaterialPhoneEntity::getMaterialId,id)
                            .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus1.getKey())
//                .last("limit " + cdMaterial.getNumberSingleGroups() * 20)
            );
            if (CollUtil.isEmpty(cdMaterialPhoneEntities)) {
                continue;
            }
            //根据数量分组 将所有的号码按照固定数量分组
            List<List<CdMaterialPhoneEntity>> partitions = Lists.partition(cdMaterialPhoneEntities, cdMaterial.getNumberSingleGroups());

            //所有分组
            for (int i1 = 0; i1 < partitions.size(); i1++) {
//                CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities.get(i1);
                List<CdMaterialPhoneEntity> cdMaterialPhoneEntities1 = partitions.get(i1);
//            cdLineRegisterEntity.setGroupCount(cdLineRegisterEntity.getGroupCount() + 1);
                //弹出执行账号
                CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities2.poll();
                cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus7.getKey());
                CdGroupTasksEntity cdGroupTasksEntity = new CdGroupTasksEntity();
                String groupName = String.format("%s-%d", cdMaterial.getGroupName(), c + 1);
                c++;
                cdGroupTasksEntity.setGroupName(groupName);
                cdGroupTasksEntity.setUploadGroupNumber(cdMaterialPhoneEntities1.size());
                cdGroupTasksEntity.setCurrentExecutionsNumber(0);
                cdGroupTasksEntity.setSuccessfullyAttractGroupsNumber(0);
                cdGroupTasksEntity.setGroupStatus(GroupStatus.GroupStatus1.getKey());
                cdGroupTasksEntity.setDeleteFlag(DeleteFlag.NO.getKey());
                cdGroupTasksEntity.setLineRegisterId(cdLineRegisterEntity.getId());
                cdGroupTasksEntity.setAddType(AddType.AddType2.getKey());
                cdGroupTasksEntity.setCreateTime(DateUtil.date());
                cdGroupTasksEntity.setMaterialId(id);
                cdGroupTasksEntity.setMaterialPhoneType(cdMaterialVO.getType());
                cdGroupTasksService.save(cdGroupTasksEntity);
                for (CdMaterialPhoneEntity cdMaterialPhoneEntity : cdMaterialPhoneEntities1) {
                    cdMaterialPhoneEntity.setLineRegisterId(cdLineRegisterEntity.getId());
                    cdMaterialPhoneEntity.setGroupTaskId(cdGroupTasksEntity.getId());
                    cdMaterialPhoneEntitiesA.add(cdMaterialPhoneEntity);
                }
                cdLineRegisterEntity.setGroupTaskId(cdGroupTasksEntity.getId());
                cdLineRegisterEntities.add(cdLineRegisterEntity);
            }
        }




        //任务保存
        //修改料子 保存水军
        cdMaterialPhoneService.updateBatchById(cdMaterialPhoneEntitiesA,8000);

        cdLineRegisterService.updateBatchById(cdLineRegisterEntities,8000);
    }

    @Override
    public byte[] importZip(ImportZipDTO importZipDTO) {
        init();


        List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                .in(CdGroupTasksEntity::getMaterialId,importZipDTO.getIds())
                .eq(CdGroupTasksEntity::getGroupStatus,GroupStatus.GroupStatus9.getKey())
        );

        List<CdMaterialPhoneEntity> list = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                .in(CdMaterialPhoneEntity::getMaterialId,importZipDTO.getIds())
                .in(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus5.getKey())
        );


        Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = list.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        StringWriter swAll = new StringWriter();

        int totalSuccessfullyAttractGroupsNumber = 0;
        for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {

            List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
            if (CollUtil.isEmpty(materialPhoneEntities)) {
                continue;
            }
            Integer successfullyAttractGroupsNumber = cdGroupTasksEntity.getSuccessfullyAttractGroupsNumber();
            totalSuccessfullyAttractGroupsNumber = totalSuccessfullyAttractGroupsNumber + successfullyAttractGroupsNumber;

            //封装模板数据
            Map<String, Object> map = new HashMap<>();
            map.put("columns", materialPhoneEntities);
            map.put("name", cdGroupTasksEntity.getGroupName());
            map.put("count", successfullyAttractGroupsNumber);
            map.put("url", cdGroupTasksEntity.getRoomId());
            VelocityContext context = new VelocityContext(map);
            //渲染模板
            StringWriter sw = new StringWriter();

            Template tpl = Velocity.getTemplate("template/url.txt.vm", "UTF-8" );
            tpl.merge(context, sw);

            swAll.append(sw.toString());
            swAll.append("\r\n");
            swAll.append("\r\n");
            swAll.append("\r\n");

            try {
                String packagePath = String.format("【群数%d】/%s-【人数-%d】-【群链-%s】.txt",cdGroupTasksEntities.size(),cdGroupTasksEntity.getGroupName(), successfullyAttractGroupsNumber,cdGroupTasksEntity.getRoomId());
                zip.putNextEntry(new ZipEntry(packagePath));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {

            }
        }

        StringWriter allWrite = new StringWriter();
        String oneText = String.format("共有【群-%d】-【人数-%d】",cdGroupTasksEntities.size(),totalSuccessfullyAttractGroupsNumber);
        allWrite.append(oneText);
        allWrite.append("\r\n");
        allWrite.append("\r\n");
        allWrite.append("\r\n");
        allWrite.append(swAll.toString());
        try {
            String packagePath = String.format("共有【群-%d】-【人数-%d】.txt",cdGroupTasksEntities.size(),totalSuccessfullyAttractGroupsNumber);
            zip.putNextEntry(new ZipEntry(packagePath));
            IOUtils.write(allWrite.toString(), zip, "UTF-8");
            IOUtils.closeQuietly(allWrite);
            zip.closeEntry();
        }catch (IOException e) {

        }
        try {
            String packagePath = String.format("【群数%d】/共有【群-%d】-【人数-%d】.txt",cdGroupTasksEntities.size(),cdGroupTasksEntities.size(),totalSuccessfullyAttractGroupsNumber);
            zip.putNextEntry(new ZipEntry(packagePath));
            IOUtils.write(allWrite.toString(), zip, "UTF-8");
            IOUtils.closeQuietly(allWrite);
            zip.closeEntry();
        }catch (IOException e) {

        }

        IOUtils.closeQuietly(zip);
        byte[] byteArray = outputStream.toByteArray();
        return byteArray;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reallocateToken(Collection<? extends Serializable> ids) {

        List<CdGroupTasksEntity> cdGroupTasksEntities = new ArrayList<>();
        for (Serializable id : ids) {
            CdGroupTasksEntity update = new CdGroupTasksEntity();
            update.setId((Integer) id);
            update.setGroupStatus(GroupStatus.GroupStatus12.getKey());
            cdGroupTasksEntities.add(update);
        }
        cdGroupTasksService.updateBatchById(cdGroupTasksEntities);
    }




    private static void init() {
        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        Velocity.init(prop);
    }

}
