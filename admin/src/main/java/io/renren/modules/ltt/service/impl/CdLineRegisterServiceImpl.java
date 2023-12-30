package io.renren.modules.ltt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.dto.IssueLiffViewDTO;
import io.renren.modules.ltt.entity.CdGroupTasksEntity;
import io.renren.modules.ltt.entity.CdMaterialPhoneEntity;
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
        );

        return PageUtils.<CdLineRegisterVO>page(page).setList(CdLineRegisterConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdLineRegisterVO getById(Integer id) {
        return CdLineRegisterConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdLineRegisterDTO cdLineRegister) {
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
    public byte[] importToken(List<Integer> list) {
        init();
        List<CdLineRegisterEntity> cdLineRegisterEntities = this.listByIds(list);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

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
                String packagePath = String.format("token/%s.txt",cdLineRegisterEntity.getPhone());
                zip.putNextEntry(new ZipEntry(packagePath));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {

            }
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

    private static void init() {
        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        Velocity.init(prop);
    }

}
