package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.ConfigBean;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.RedisUtil;
import com.maxbill.tool.StringUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConfController {

    public String getConfInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                List<ConfigBean> configs = RedisUtil.getRedisConfig(jedis);
                resultMap.put("code", 200);
                resultMap.put("data", configs);
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String setConfInfo(String conf) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                Map<String, String> confMap = new HashMap<>();
                String[] confArray = conf.split("&");
                for (String str : confArray) {
                    confMap.put(StringUtil.getKeyString(str, "="), StringUtil.getValueString(str, "="));
                }
                RedisUtil.setRedisConfig(jedis, confMap);
                resultMap.put("code", 200);
                resultMap.put("msgs", "修改配置成功");
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "部分数据修改异常");
        }
        return JSON.toJSONString(resultMap);
    }

}
