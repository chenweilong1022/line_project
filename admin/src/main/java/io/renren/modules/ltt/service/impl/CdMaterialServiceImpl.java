package io.renren.modules.ltt.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.renren.common.utils.DateUtils;
import io.renren.common.validator.Assert;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.conver.CdMaterialPhoneConver;
import io.renren.modules.ltt.dto.AutoAssignGroupsDTO;
import io.renren.modules.ltt.dto.ImportZipDTO;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
import io.renren.modules.ltt.enums.*;
import io.renren.modules.ltt.service.CdGroupTasksService;
import io.renren.modules.ltt.service.CdLineRegisterService;
import io.renren.modules.ltt.service.CdMaterialPhoneService;
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
import io.renren.modules.ltt.service.CdMaterialService;
import io.renren.modules.ltt.conver.CdMaterialConver;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
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
    private CdGroupTasksService cdGroupTasksService;

    @Override
    public PageUtils<CdMaterialVO> queryPage(CdMaterialDTO cdMaterial) {
        IPage<CdMaterialEntity> page = baseMapper.selectPage(
                new Query<CdMaterialEntity>(cdMaterial).getPage(),
                new QueryWrapper<CdMaterialEntity>()
        );

        return PageUtils.<CdMaterialVO>page(page).setList(CdMaterialConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdMaterialVO getById(Integer id) {
        return CdMaterialConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CdMaterialDTO cdMaterial) {
        //料子数据
        String materialText = HttpUtil.downloadString(cdMaterial.getMaterialUrl(), "UTF-8");
        String[] materialTextSplit = materialText.split("\n");
        Assert.isTrue(ArrayUtil.isEmpty(materialTextSplit),"txt不能为空");
        //水军数据
        String navyText = HttpUtil.downloadString(cdMaterial.getNavyUrl(), "UTF-8");
        String[] navyTextSplit = navyText.split("\n");
        Assert.isTrue(ArrayUtil.isEmpty(navyTextSplit),"txt不能为空");
        Integer uploadNumber = navyTextSplit.length + materialTextSplit.length;
        //总上传
        cdMaterial.setUploadNumber(uploadNumber);
        //料子数量
        cdMaterial.setMaterialNumber(materialTextSplit.length);
        //水军数量
        cdMaterial.setNavyNumber(navyTextSplit.length);
        cdMaterial.setSuccessesNumber(0);
        cdMaterial.setFailuresNumber(0);
        cdMaterial.setSynchronizationsNumber(0);
        cdMaterial.setCreateTime(DateUtil.date());
        cdMaterial.setDeleteFlag(DeleteFlag.NO.getKey());
        CdMaterialEntity cdMaterialEntity = CdMaterialConver.MAPPER.converDTO(cdMaterial);
        boolean save = this.save(cdMaterialEntity);
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = new ArrayList<>();

        for (String string : materialTextSplit) {
            string = string.replace("\r","").trim();
            String[] split = string.split("\t");
            CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
            cdMaterialPhoneEntity.setMaterialId(cdMaterialEntity.getId());
            if (split.length > 1) {
                cdMaterialPhoneEntity.setContactKey(split[0].trim());
                cdMaterialPhoneEntity.setMid(split[1].trim());
            }else {
                cdMaterialPhoneEntity.setContactKey(string);
            }

            cdMaterialPhoneEntity.setMaterialType(MaterialType.MaterialType1.getKey());
            cdMaterialPhoneEntity.setCreateTime(DateUtil.date());
            cdMaterialPhoneEntity.setDeleteFlag(DeleteFlag.NO.getKey());
            cdMaterialPhoneEntities.add(cdMaterialPhoneEntity);
        }

        for (String string : navyTextSplit) {
            string = string.replace("\r","").trim();
            if (StrUtil.isNotEmpty(string)) {
                CdMaterialPhoneEntity cdMaterialPhoneEntity = new CdMaterialPhoneEntity();
                cdMaterialPhoneEntity.setMaterialId(cdMaterialEntity.getId());
                cdMaterialPhoneEntity.setGroupTaskId(-1);
                cdMaterialPhoneEntity.setContactKey(string);
                cdMaterialPhoneEntity.setMaterialType(MaterialType.MaterialType2.getKey());
                cdMaterialPhoneEntity.setCreateTime(DateUtil.date());
                cdMaterialPhoneEntity.setDeleteFlag(DeleteFlag.NO.getKey());
                cdMaterialPhoneEntities.add(cdMaterialPhoneEntity);
            }
        }

        List<List<CdMaterialPhoneEntity>> partition = Lists.partition(cdMaterialPhoneEntities, 1000);
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities2 = new ArrayList<>();
        for (List<CdMaterialPhoneEntity> materialPhoneEntities : partition) {
            List<String> contactKeys = materialPhoneEntities.stream().map(CdMaterialPhoneEntity::getContactKey).collect(Collectors.toList());
            List<CdMaterialPhoneEntity> list = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda().in(CdMaterialPhoneEntity::getContactKey,contactKeys));
            Map<String, CdMaterialPhoneEntity> stringCdMaterialPhoneEntityMap = list.stream().collect(Collectors.toMap(CdMaterialPhoneEntity::getContactKey, item -> item));
            for (CdMaterialPhoneEntity materialPhoneEntity : materialPhoneEntities) {
                if (!stringCdMaterialPhoneEntityMap.containsKey(materialPhoneEntity.getContactKey())) {
                    cdMaterialPhoneEntities2.add(materialPhoneEntity);
                }
            }
        }
        cdMaterialPhoneService.saveBatch(cdMaterialPhoneEntities2);
        return save;
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

    int c = 81;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoAssignGroups(AutoAssignGroupsDTO cdMaterial) {
        CdMaterialVO cdMaterialVO = this.getById(cdMaterial.getId());
        Assert.isNull(cdMaterialVO,"不存在料子");
        //获取 所有手机号
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                .eq(CdMaterialPhoneEntity::getMaterialId,cdMaterial.getId())
                .eq(CdMaterialPhoneEntity::getMaterialType,MaterialType.MaterialType1.getKey())
                .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus1.getKey())
//                .last("limit " + cdMaterial.getNumberSingleGroups() * 20)
        );
        //获取水军
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntities2 = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                .eq(CdMaterialPhoneEntity::getMaterialId,cdMaterial.getId())
                .eq(CdMaterialPhoneEntity::getMaterialType,MaterialType.MaterialType2.getKey())
        );
        //根据数量分组 将所有的号码按照固定数量分组
        List<List<CdMaterialPhoneEntity>> partitions = Lists.partition(cdMaterialPhoneEntities, cdMaterial.getNumberSingleGroups());
        LambdaQueryWrapper<CdLineRegisterEntity> last = new QueryWrapper<CdLineRegisterEntity>().lambda()
                .eq(CdLineRegisterEntity::getOpenStatus, OpenStatus.OpenStatus3.getKey())
                .in(CdLineRegisterEntity::getRegisterStatus, RegisterStatus.RegisterStatus4.getKey(), RegisterStatus.RegisterStatus7.getKey())
                .orderByAsc(CdLineRegisterEntity::getId)
                .eq(CdLineRegisterEntity::getCountryCode, cdMaterial.getCountryCode())
                .last("limit " + partitions.size() + 10);
//        if (MaterialPhoneType.MaterialType1.getKey().equals(cdMaterialVO.getType())) {
//            last.lt(CdLineRegisterEntity::getGroupCount, cdMaterial.getAccountGroupRestrictions());
//        }
        //注册账号 状态是拉过群或者是注册成功的 拉群次数小于限制数量的账号
        List<CdLineRegisterEntity> cdLineRegisterEntities = cdLineRegisterService.list(last);
        List<CdMaterialPhoneEntity> cdMaterialPhoneEntitiesA = new ArrayList<>();
//        List<CdMaterialPhoneEntity> cdMaterialPhoneEntitiesB = new ArrayList<>();
//        List<CdGroupTasksEntity> cdGroupTasksEntities = new ArrayList<>();




        //所有分组
        for (int i1 = 0; i1 < partitions.size(); i1++) {
            CdLineRegisterEntity cdLineRegisterEntity = cdLineRegisterEntities.get(i1);


            List<CdMaterialPhoneEntity> cdMaterialPhoneEntities1 = partitions.get(i1);
//            cdLineRegisterEntity.setGroupCount(cdLineRegisterEntity.getGroupCount() + 1);
            cdLineRegisterEntity.setRegisterStatus(RegisterStatus.RegisterStatus7.getKey());
            CdGroupTasksEntity cdGroupTasksEntity = new CdGroupTasksEntity();
            String groupName = String.format("%s(%d)", "tt_group", c + 1);
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
            cdGroupTasksEntity.setMaterialId(cdMaterialVO.getId());
            cdGroupTasksEntity.setMaterialPhoneType(cdMaterialVO.getType());
            cdGroupTasksService.save(cdGroupTasksEntity);
            for (CdMaterialPhoneEntity cdMaterialPhoneEntity : cdMaterialPhoneEntities1) {
                cdMaterialPhoneEntity.setLineRegisterId(cdLineRegisterEntity.getId());
                cdMaterialPhoneEntity.setGroupTaskId(cdGroupTasksEntity.getId());
                cdMaterialPhoneEntitiesA.add(cdMaterialPhoneEntity);
            }

//            for (CdMaterialPhoneEntity cdMaterialPhoneEntity : cdMaterialPhoneEntities2) {
//                CdMaterialPhoneEntity cdMaterialPhoneEntityCopy = CdMaterialPhoneConver.MAPPER.conver1(cdMaterialPhoneEntity);
//                cdMaterialPhoneEntityCopy.setId(null);
//                cdMaterialPhoneEntityCopy.setLineRegisterId(cdLineRegisterEntity.getId());
//                cdMaterialPhoneEntityCopy.setGroupTaskId(cdGroupTasksEntity.getId());
//                cdMaterialPhoneEntitiesB.add(cdMaterialPhoneEntityCopy);
//            }
        }

        //任务保存
//        cdGroupTasksService.saveBatch(cdGroupTasksEntities);
        //修改料子 保存水军
        cdMaterialPhoneService.updateBatchById(cdMaterialPhoneEntitiesA);
//        cdMaterialPhoneService.saveBatch(cdMaterialPhoneEntitiesB);

        cdLineRegisterService.updateBatchById(cdLineRegisterEntities);
    }

    @Override
    public byte[] importZip(ImportZipDTO importZipDTO) {
        init();


        List<CdGroupTasksEntity> cdGroupTasksEntities = cdGroupTasksService.list(new QueryWrapper<CdGroupTasksEntity>().lambda()
                .eq(CdGroupTasksEntity::getMaterialId,importZipDTO.getId())
        );

        List<CdMaterialPhoneEntity> list = cdMaterialPhoneService.list(new QueryWrapper<CdMaterialPhoneEntity>().lambda()
                .eq(CdMaterialPhoneEntity::getMaterialId,importZipDTO.getId())
                .eq(CdMaterialPhoneEntity::getMaterialPhoneStatus,MaterialPhoneStatus.MaterialPhoneStatus3.getKey())
        );


        Map<Integer, List<CdMaterialPhoneEntity>> integerListMap = list.stream().collect(Collectors.groupingBy(CdMaterialPhoneEntity::getGroupTaskId));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        StringWriter swAll = new StringWriter();

        int totalSuccessfullyAttractGroupsNumber = 0;
        for (CdGroupTasksEntity cdGroupTasksEntity : cdGroupTasksEntities) {

            List<CdMaterialPhoneEntity> materialPhoneEntities = integerListMap.get(cdGroupTasksEntity.getId());
            Integer successfullyAttractGroupsNumber = materialPhoneEntities.size();
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






    private static void init() {
        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        Velocity.init(prop);
    }

}
