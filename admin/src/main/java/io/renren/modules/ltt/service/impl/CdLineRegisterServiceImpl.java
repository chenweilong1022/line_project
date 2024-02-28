package io.renren.modules.ltt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.dto.IssueLiffViewDTO;
import io.renren.modules.ltt.dto.LineTokenJson;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
import io.renren.modules.ltt.enums.ExportStatus;
import io.renren.modules.ltt.enums.RegisterStatus;
import io.renren.modules.ltt.service.LineService;
import io.renren.modules.ltt.service.ProxyService;
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

import io.renren.modules.ltt.dao.CdLineRegisterDao;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import io.renren.modules.ltt.dto.CdLineRegisterDTO;
import io.renren.modules.ltt.vo.CdLineRegisterVO;
import io.renren.modules.ltt.service.CdLineRegisterService;
import io.renren.modules.ltt.conver.CdLineRegisterConver;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service("cdLineRegisterService")
@Game
public class CdLineRegisterServiceImpl extends ServiceImpl<CdLineRegisterDao, CdLineRegisterEntity> implements CdLineRegisterService {

    @Override
    public PageUtils<CdLineRegisterVO> queryPage(CdLineRegisterDTO cdLineRegister) {
        IPage<CdLineRegisterEntity> page = baseMapper.selectPage(
                new Query<CdLineRegisterEntity>(cdLineRegister).getPage(),
                new QueryWrapper<CdLineRegisterEntity>().lambda()
                        .orderByDesc(CdLineRegisterEntity::getId)
                        .eq(ObjectUtil.isNotNull(cdLineRegister.getRegisterStatus()),CdLineRegisterEntity::getRegisterStatus,cdLineRegister.getRegisterStatus())
                        .eq(StrUtil.isNotEmpty(cdLineRegister.getCountryCode()),CdLineRegisterEntity::getCountryCode,cdLineRegister.getCountryCode())
                        .eq(StrUtil.isNotEmpty(cdLineRegister.getPhone()),CdLineRegisterEntity::getPhone,cdLineRegister.getPhone())
                        .eq(ObjectUtil.isNotNull(cdLineRegister.getAccountExistStatus()),CdLineRegisterEntity::getAccountExistStatus,cdLineRegister.getAccountExistStatus())
                        .eq(ObjectUtil.isNotNull(cdLineRegister.getExportStatus()),CdLineRegisterEntity::getExportStatus,cdLineRegister.getExportStatus())
                        .eq(ObjectUtil.isNotNull(cdLineRegister.getOpenStatus()),CdLineRegisterEntity::getOpenStatus,cdLineRegister.getOpenStatus())
        );

        return PageUtils.<CdLineRegisterVO>page(page).setList(CdLineRegisterConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdLineRegisterVO getById(Integer id) {
        return CdLineRegisterConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdLineRegisterDTO cdLineRegister) {
        cdLineRegister.setRegisterStatus(RegisterStatus.RegisterStatus9.getKey());
        CdLineRegisterEntity cdLineRegisterEntity = CdLineRegisterConver.MAPPER.converDTO(cdLineRegister);
        return this.save(cdLineRegisterEntity);
    }

    @Override
    public boolean updateById(CdLineRegisterDTO cdLineRegister) {
        CdLineRegisterEntity cdLineRegisterEntity = CdLineRegisterConver.MAPPER.converDTO(cdLineRegister);
        return this.updateById(cdLineRegisterEntity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    @Override
    public PageUtils listByTaskId(CdLineRegisterDTO cdLineRegister) {
        IPage<CdLineRegisterVO> page = baseMapper.listPage(
                new Query<CdLineRegisterEntity>(cdLineRegister).getPage(),
                cdLineRegister
        );
        return PageUtils.<CdLineRegisterVO>page(page);
    }

    @Autowired
    private LineService lineService;
    @Autowired
    private ProxyService proxyService;
    @Override
    public void issueLiffView(List<Integer> list) {
        List<CdLineRegisterEntity> cdLineRegisterEntities = this.listByIds(list);
        for (CdLineRegisterEntity cdLineRegisterEntity : cdLineRegisterEntities) {
            String getflowip = proxyService.getflowip(cdLineRegisterEntity);
            if (StrUtil.isEmpty(getflowip)) {
                return;
            }
            IssueLiffViewDTO issueLiffViewDTO = new IssueLiffViewDTO();
            issueLiffViewDTO.setProxy(getflowip);
            issueLiffViewDTO.setToken(cdLineRegisterEntity.getToken());
            lineService.issueLiffView(issueLiffViewDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public byte[] importToken(List<Integer> list) {
        init();
        List<CdLineRegisterEntity> cdLineRegisterEntities = this.listByIds(list);
        //批量修改为已导出
        for (CdLineRegisterEntity cdLineRegisterEntity : cdLineRegisterEntities) {
            cdLineRegisterEntity.setExportStatus(ExportStatus.ExportStatus2.getKey());
        }
        this.updateBatchById(cdLineRegisterEntities);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        List<String> tokens = new ArrayList<>();
        List<String> tokens7 = new ArrayList<>();

        for (CdLineRegisterEntity cdLineRegisterEntity : cdLineRegisterEntities) {
            //封装模板数据
            Map<String, Object> map = new HashMap<>();
            map.put("token", cdLineRegisterEntity.getToken());
            VelocityContext context = new VelocityContext(map);
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("template/token.txt.vm", "UTF-8" );
            tpl.merge(context, sw);
            try {
                LineTokenJson lineTokenJson = JSON.parseObject(cdLineRegisterEntity.getToken(), LineTokenJson.class);
                String format = String.format("%s----%s----%s----%s----%s----%s\n", "66", lineTokenJson.getPhone().replaceFirst("66",""), lineTokenJson.getPassword(), lineTokenJson.getMid(), lineTokenJson.getAccessToken(), lineTokenJson.getRefreshToken());
                tokens.add(format);

                String format7 = String.format("%s----%s----%s----%s----%s----%s----%s\n", "66", lineTokenJson.getPhone().replaceFirst("66",""), lineTokenJson.getPassword(), lineTokenJson.getMid(), lineTokenJson.getAccessToken(), lineTokenJson.getRefreshToken(),lineTokenJson.getAuthToken());
                tokens7.add(format7);

                String packagePath = String.format("token/%s.txt",cdLineRegisterEntity.getPhone());
                zip.putNextEntry(new ZipEntry(packagePath));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
            }
        }

        try{
            //封装模板数据
            Map<String, Object> map = new HashMap<>();
            map.put("columns", tokens);
            VelocityContext context = new VelocityContext(map);
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("template/84data.vm", "UTF-8" );
            tpl.merge(context, sw);

            String packagePath = String.format("token/%s.txt","data");
            zip.putNextEntry(new ZipEntry(packagePath));
            IOUtils.write(sw.toString(), zip, "UTF-8");
            IOUtils.closeQuietly(sw);
            zip.closeEntry();

            //封装模板数据
            Map<String, Object> map7 = new HashMap<>();
            map7.put("columns", tokens7);
            VelocityContext context7 = new VelocityContext(map7);
            //渲染模板
            StringWriter sw7 = new StringWriter();
            Template tpl7 = Velocity.getTemplate("template/84data.vm", "UTF-8" );
            tpl7.merge(context7, sw7);

            String packagePath7 = String.format("token/%s.txt","data7");
            zip.putNextEntry(new ZipEntry(packagePath7));
            IOUtils.write(sw7.toString(), zip, "UTF-8");
            IOUtils.closeQuietly(sw7);
            zip.closeEntry();
        }catch (IOException e) {
        }
        IOUtils.closeQuietly(zip);
        byte[] byteArray = outputStream.toByteArray();
        return byteArray;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearProxy() {
        UpdateWrapper<CdLineRegisterEntity> registerEntityUpdateWrapper = new UpdateWrapper<>();
        registerEntityUpdateWrapper.setSql("`proxy` = null");
        this.update(registerEntityUpdateWrapper);
    }

    @Override
    public boolean unLock(Integer id) {
        CdLineRegisterDTO lineRegisterDTO = new CdLineRegisterDTO();
        lineRegisterDTO.setId(id);
        lineRegisterDTO.setRegisterStatus(RegisterStatus.RegisterStatus10.getKey());
        return this.updateById(lineRegisterDTO);
    }

    private static void init() {
        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        Velocity.init(prop);
    }

}
