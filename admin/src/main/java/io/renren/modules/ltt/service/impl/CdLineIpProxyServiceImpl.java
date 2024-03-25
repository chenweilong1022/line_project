package io.renren.modules.ltt.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import io.renren.common.utils.*;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.enums.CountryCode;
import io.renren.modules.ltt.vo.PhoneCountryVO;
import io.renren.modules.sys.entity.ProjectWorkEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.ltt.dao.CdLineIpProxyDao;
import io.renren.modules.ltt.entity.CdLineIpProxyEntity;
import io.renren.modules.ltt.dto.CdLineIpProxyDTO;
import io.renren.modules.ltt.vo.CdLineIpProxyVO;
import io.renren.modules.ltt.service.CdLineIpProxyService;
import io.renren.modules.ltt.conver.CdLineIpProxyConver;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


@Service("cdLineIpProxyService")
@Game
public class CdLineIpProxyServiceImpl extends ServiceImpl<CdLineIpProxyDao, CdLineIpProxyEntity> implements CdLineIpProxyService {

    @Override
    public PageUtils<CdLineIpProxyVO> queryPage(CdLineIpProxyDTO cdLineIpProxy) {
        IPage<CdLineIpProxyEntity> page = baseMapper.selectPage(
                new Query<CdLineIpProxyEntity>(cdLineIpProxy).getPage(),
                new QueryWrapper<CdLineIpProxyEntity>()
        );

        return PageUtils.<CdLineIpProxyVO>page(page).setList(CdLineIpProxyConver.MAPPER.conver(page.getRecords()));
    }
    @Override
    public CdLineIpProxyVO getById(Integer id) {
        return CdLineIpProxyConver.MAPPER.conver(baseMapper.selectById(id));
    }

    @Override
    public boolean save(CdLineIpProxyDTO cdLineIpProxy) {
        CdLineIpProxyEntity cdLineIpProxyEntity = CdLineIpProxyConver.MAPPER.converDTO(cdLineIpProxy);
        return this.save(cdLineIpProxyEntity);
    }

    @Override
    public boolean updateById(CdLineIpProxyDTO cdLineIpProxy) {
        CdLineIpProxyEntity cdLineIpProxyEntity = CdLineIpProxyConver.MAPPER.converDTO(cdLineIpProxy);
        return this.updateById(cdLineIpProxyEntity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Resource(name = "caffeineCacheProjectWorkEntity")
    private Cache<String, ProjectWorkEntity> caffeineCacheProjectWorkEntity;

    @Resource(name = "caffeineCacheListString")
    private Cache<String, Queue<String>> caffeineCacheListString;
    @Override
    public String getProxyIp(CdLineIpProxyDTO cdLineIpProxyDTO ) {
        try {
            //如果是ip2world
            ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
            if (ObjectUtil.isNull(projectWorkEntity)) {
                return null;
            }
            PhoneCountryVO phoneNumberInfo = PhoneUtil.getPhoneNumberInfo(cdLineIpProxyDTO.getLzPhone());
            Long countryCode = phoneNumberInfo.getCountryCode();
            String regions = EnumUtil.queryValueByKey(countryCode.intValue(), CountryCode.values());
            CdLineIpProxyEntity one = this.getOne(new QueryWrapper<CdLineIpProxyEntity>().lambda()
                    .eq(CdLineIpProxyEntity::getTokenPhone,cdLineIpProxyDTO.getTokenPhone())
                    .eq(CdLineIpProxyEntity::getLzCountry,String.valueOf(countryCode))
            );

            if (ObjectUtil.isNotNull(one)) {
                String ip = one.getIp();
                boolean proxyUse = isProxyUse(ip,regions);
                if (proxyUse) {
                    return socks5Pre(ip);
                }
            }



            Queue<String> getflowip = caffeineCacheListString.getIfPresent(regions);
            String ip = null;
            if (CollUtil.isEmpty(getflowip)) {
                String getPhoneHttp = String.format("https://tq.lunaproxy.com/getflowip?neek=1136881&num=500&type=1&sep=1&regions=%s&ip_si=1&level=1&sb=",regions);
                String resp = HttpUtil.get(getPhoneHttp);
                if (JSONUtil.isJson(resp)) {
                    return null;
                }
                String[] split = resp.split("\r\n");
                Queue<String> getflowipNew = new LinkedList<>();
                for (String s : split) {
                    getflowipNew.offer(s);
                }
                ip = getflowipNew.poll();
                caffeineCacheListString.put(regions,getflowipNew);
            }else {
                ip = getflowip.poll();
                caffeineCacheListString.put(regions,getflowip);
            }

            if (ObjectUtil.isNull(one)) {
                CdLineIpProxyEntity save = new CdLineIpProxyEntity();
                save.setIp(ip);
                save.setTokenPhone(cdLineIpProxyDTO.getTokenPhone());
                save.setLzCountry(String.valueOf(countryCode));
                this.save(save);
            }else {
                one.setIp(ip);
                this.updateById(one);
            }
            return socks5Pre(ip);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static String socks5Pre(String ip) {
        if (ip.contains("socks5://")) {
            return ip;
        }
        return String.format("socks5://%s", ip);
    }

    private boolean isProxyUse(String ip,String country) {
        try {
            String format = String.format("curl --socks5 %s ipinfo.io",ip);
            List<String> strings = RuntimeUtil.execForLines(format);
            for (String string : strings) {
                if (string.toLowerCase().contains("country") && string.toLowerCase().contains(country)) {
                    return true;
                }
            }
            return false;
        }catch (Exception e) {

        }
        return false;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

}
