package io.renren.modules.ltt.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import io.renren.common.utils.ConfigConstant;
import io.renren.datasources.annotation.Game;
import io.renren.modules.ltt.service.FirefoxService;
import io.renren.modules.ltt.vo.GetPhoneVo;
import io.renren.modules.ltt.vo.SearchPhoneVO;
import io.renren.modules.ltt.vo.YfhubGetPhoneVO;
import io.renren.modules.sys.entity.ProjectWorkEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 21:28
 */
@Service("yfhubServiceImpl")
@Game
public class YfhubServiceImpl implements FirefoxService {

    String token = "2864d7218a964a17a1425b7b2d58b381";
    String projectId = "30033";
    @Resource(name = "caffeineCacheProjectWorkEntity")
    private Cache<String, ProjectWorkEntity> caffeineCacheProjectWorkEntity;

    //获取手机号
    public GetPhoneVo getPhone(){
        String getUrl = String.format("http://www.yfhub.xyz:8889/messageDataApi/getPhoneNumber?token=%s&projectId=%s&num=1", token, projectId);
        String resp = HttpUtil.get(getUrl);
        YfhubGetPhoneVO yfhubGetPhoneVO = JSON.parseObject(resp, YfhubGetPhoneVO.class);
        if (1 == yfhubGetPhoneVO.getCode()) {
            return new GetPhoneVo().setPkey(yfhubGetPhoneVO.getData()).setPhone(yfhubGetPhoneVO.getData());
        }
        return null;
    }

    @Override
    public String getPhoneCode(String pKey) {
        String getUrl = String.format("http://www.yfhub.xyz:8889/messageDataApi/getSmsContent?token=%s&projectId=%s&phoneNumber=%s", token, projectId,pKey);
        String resp = HttpUtil.get(getUrl);
        YfhubGetPhoneVO yfhubGetPhoneVO = JSON.parseObject(resp, YfhubGetPhoneVO.class);
        if (1 == yfhubGetPhoneVO.getCode()) {
            return yfhubGetPhoneVO.getData();
        }
        return null;
    }

    @Override
    public boolean setRel(String pKey) {
        String getUrl = String.format("http://www.yfhub.xyz:8889/messageDataApi/putBackList?token=%s&projectId=%s&phoneNumber=%s&sign=1", token, projectId,pKey);
        String resp = HttpUtil.get(getUrl);
        YfhubGetPhoneVO yfhubGetPhoneVO = JSON.parseObject(resp, YfhubGetPhoneVO.class);
        if (1 == yfhubGetPhoneVO.getCode()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean withBlackMobile(String pKey) {
        return false;
    }
}
