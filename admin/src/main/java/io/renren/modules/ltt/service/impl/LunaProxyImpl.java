package io.renren.modules.ltt.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ImageUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.renren.common.utils.ConfigConstant;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.entity.CdLineRegisterEntity;
import io.renren.modules.ltt.entity.CdStaticProxyEntity;
import io.renren.modules.ltt.enums.ProxyStatus;
import io.renren.modules.ltt.service.CdLineRegisterService;
import io.renren.modules.ltt.service.CdStaticProxyService;
import io.renren.modules.ltt.service.ProxyService;
import io.renren.modules.sys.entity.ProjectWorkEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 22:03
 */
@Service("lunaProxyImpl")
@Game
@Slf4j
public class LunaProxyImpl implements ProxyService {


    @Resource(name = "caffeineCacheListString")
    private Cache<String, Queue<String>> caffeineCacheListString;
    @Resource(name = "caffeineCacheListCdStaticProxyEntity")
    private Cache<String, Queue<CdStaticProxyEntity>> caffeineCacheListCdStaticProxyEntity;

    @Resource(name = "caffeineCacheProjectWorkEntity")
    private Cache<String, ProjectWorkEntity> caffeineCacheProjectWorkEntity;
    @Autowired
    private CdLineRegisterService cdLineRegisterService;



    private boolean isProxyUse(String ip) {
        try {
            String format = String.format("curl --socks5 %s ipinfo.io",ip);
            List<String> strings = RuntimeUtil.execForLines(format);
            for (String string : strings) {
                if (string.toLowerCase().contains("country") && string.toLowerCase().contains("th")) {
                    return true;
                }
            }
            return false;
        }catch (Exception e) {

        }
        return false;
    }


    @Override
    public synchronized String getflowip(CdLineRegisterEntity cdLineRegisterEntity) {
        String proxy = cdLineRegisterEntity.getProxy();
        if (StrUtil.isNotEmpty(proxy)) {
            //如果失效了，并且不是泰国ip 去更换ip
            boolean proxyUse = isProxyUse(proxy);
            if (proxyUse) {
                return String.format("socks5://%s", proxy);
            }
        }
        //如果是ip2world
        ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);
        if (ObjectUtil.isNull(projectWorkEntity)) {
            return null;
        }
        if (ProxyStatus.ProxyStatus2.getKey().equals(projectWorkEntity.getProxy())) {
            String ip = getflowip1(cdLineRegisterEntity);
            if (StrUtil.isEmpty(ip)) {
                return ip;
            }
            return String.format("socks5://%s", ip);
        }else if (ProxyStatus.ProxyStatus3.getKey().equals(projectWorkEntity.getProxy())) {
            if (StrUtil.isNotEmpty(cdLineRegisterEntity.getProxy())) {
                return cdLineRegisterEntity.getProxy();
            }
            String ip = getflowip2();
            if (StrUtil.isEmpty(ip)) {
                return ip;
            }
            String format = String.format("socks5://%s", ip);
            if (ObjectUtil.isNotNull(cdLineRegisterEntity.getId())) {
                cdLineRegisterEntity.setProxy(format);
            }
            //静态代理修改
            cdLineRegisterService.updateById(cdLineRegisterEntity);
            return format;
        }
        Queue<String> getflowip = caffeineCacheListString.getIfPresent("getflowip");
        String ip = null;
        if (CollUtil.isEmpty(getflowip)) {
            String getPhoneHttp = String.format("https://tq.lunaproxy.com/getflowip?neek=1136881&num=500&type=1&sep=1&regions=%s&ip_si=1&level=1&sb=",cdLineRegisterEntity.getCountryCode());
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
            caffeineCacheListString.put("getflowip",getflowipNew);
        }else {
            ip = getflowip.poll();
            caffeineCacheListString.put("getflowip",getflowip);
        }
        log.info("proxy = {}","lunaproxy");
        if (StrUtil.isEmpty(ip)) {
            log.info("proxy = {}","lunaproxy empty");
            return ip;
        }
        //更新代理ip
        CdLineRegisterEntity update = new CdLineRegisterEntity();
        update.setId(cdLineRegisterEntity.getId());
        update.setProxy(ip);
        cdLineRegisterService.updateById(update);
        log.info("proxy = {} ip = {}","lunaproxy",ip);
        return String.format("socks5://%s", ip);
    }

    public static void main(String[] args) {

        String encode = Base64.encode(new File("/Users/chenweilong/Downloads/2024-01-29 15.56.46.jpg"));
        System.out.println(encode);

    }

    @Override
    public String getflowip1(CdLineRegisterEntity cdLineRegisterEntity) {
        Queue<String> getflowip = caffeineCacheListString.getIfPresent("getflowip");
        String ip = null;
        if (CollUtil.isEmpty(getflowip)) {
            String getPhoneHttp = String.format("http://api.proxy.ip2world.com/getProxyIp?regions=%s&lb=1&return_type=txt&protocol=socks5&num=50",cdLineRegisterEntity.getCountryCode());
            String resp = HttpUtil.get(getPhoneHttp);
            String[] split = resp.split("\r\n");
            Queue<String> getflowipNew = new LinkedList<>();
            for (String s : split) {
                getflowipNew.offer(s);
            }
            ip = getflowipNew.poll();
            caffeineCacheListString.put("getflowip",getflowipNew);
        }else {
            ip = getflowip.poll();
            caffeineCacheListString.put("getflowip",getflowip);
        }
        log.info("proxy = {}","ip2world");
        return ip;
    }

    @Autowired
    private CdStaticProxyService cdStaticProxyService;

    @Override
    public String getflowip2() {
        ProjectWorkEntity projectWorkEntity = caffeineCacheProjectWorkEntity.getIfPresent(ConfigConstant.PROJECT_WORK_KEY);

        Queue<CdStaticProxyEntity> getflowip2 = caffeineCacheListCdStaticProxyEntity.getIfPresent("getflowip2");
        String ip = null;
        if (CollUtil.isEmpty(getflowip2)) {
            //获取可以使用的所有proxy
            List<CdStaticProxyEntity> list = cdStaticProxyService.list(new QueryWrapper<CdStaticProxyEntity>().lambda()
                    .lt(CdStaticProxyEntity::getUseCount,projectWorkEntity.getProxyUseCount())
            );
            if(CollUtil.isEmpty(list)) {
                return null;
            }

            Queue<CdStaticProxyEntity> getflowipNew = new LinkedList<>();
            for (CdStaticProxyEntity cdStaticProxyEntity : list) {
                for (int i = 0; i < projectWorkEntity.getProxyUseCount(); i++) {
                    getflowipNew.offer(cdStaticProxyEntity);
                }
            }
            CdStaticProxyEntity poll = getflowipNew.poll();
            LambdaUpdateWrapper<CdStaticProxyEntity> updateWrapper = new UpdateWrapper<CdStaticProxyEntity>().lambda()
                    .eq(CdStaticProxyEntity::getId, poll.getId())
                    .setSql("`use_count`=`use_count`+1");
            cdStaticProxyService.update(updateWrapper);

            ip = String.format("%s:%s@%s",poll.getUsername(),poll.getPassword(),poll.getIpProxy());
            caffeineCacheListCdStaticProxyEntity.put("getflowip2",getflowipNew);
        }else {
            CdStaticProxyEntity poll = getflowip2.poll();

            LambdaUpdateWrapper<CdStaticProxyEntity> updateWrapper = new UpdateWrapper<CdStaticProxyEntity>().lambda()
                    .eq(CdStaticProxyEntity::getId, poll.getId())
                    .setSql("`use_count`=`use_count`+1");

            cdStaticProxyService.update(updateWrapper);
            ip = String.format("%s:%s@%s",poll.getUsername(),poll.getPassword(),poll.getIpProxy());
            caffeineCacheListCdStaticProxyEntity.put("getflowip2",getflowip2);
        }
        log.info("proxy = {}","ip2world");
        return ip;
    }
}
