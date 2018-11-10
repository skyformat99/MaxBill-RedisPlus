package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.RedisInfo;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.RedisUtil;
import com.maxbill.tool.StringUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.util.Slowlog;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxbill.base.bean.ResultInfo.*;
import static com.maxbill.tool.DataUtil.getCurrentJedisObject;
import static com.maxbill.tool.RedisUtil.getRedisInfo;

@Component
public class InfoSinglesController {

    public String getBaseInfo() {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                return getOkByJson(RedisUtil.getRedisInfoList(jedis));
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String getLogsInfo() {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                List<Slowlog> logs = RedisUtil.getRedisLog(jedis);
                Collections.reverse(logs);
                return getOkByJson(logs);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String getMemInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = getRedisInfo(jedis);
                String[] memory = redisInfo.getMemory().split("\n");
                String val01 = StringUtil.getValueString(":", memory[2]).replace("\r", "");
                String val02 = StringUtil.getValueString(":", memory[5]).replace("\r", "");
                resultMap.put("val01", Float.valueOf(val01.substring(0, val01.length() - 1)));
                resultMap.put("val02", Float.valueOf(val02.substring(0, val02.length() - 1)));
            } else {
                resultMap.put("val01", 0);
                resultMap.put("val02", 0);
            }
        } catch (Exception e) {
            resultMap.put("val01", 0);
            resultMap.put("val02", 0);
        }
        return JSON.toJSONString(resultMap);
    }

    public String getCpuInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = getRedisInfo(jedis);
                String[] cpu = redisInfo.getCpu().split("\n");
                String val01 = StringUtil.getValueString(":", cpu[1]).replace("\r", "");
                String val02 = StringUtil.getValueString(":", cpu[2]).replace("\r", "");
                resultMap.put("val01", Float.valueOf(val01));
                resultMap.put("val02", Float.valueOf(val02));
            } else {
                resultMap.put("val01", 0);
                resultMap.put("val02", 0);
            }
        } catch (Exception e) {
            resultMap.put("val01", 0);
            resultMap.put("val02", 0);
        }
        return JSON.toJSONString(resultMap);
    }

    public String getKeyInfo() {
        Long[] keys = new Long[16];
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                for (int i = 0; i < 16; i++) {
                    keys[i] = RedisUtil.dbSize(jedis, i);
                }
            } else {
                for (int i = 0; i < 16; i++) {
                    keys[i] = 0l;
                }
            }
        } catch (Exception e) {
            for (int i = 0; i < 16; i++) {
                keys[i] = 0l;
            }
        }
        return JSON.toJSONString(keys);
    }

    public String getNetInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = getRedisInfo(jedis);
                String[] stats = redisInfo.getStats().split("\n");
                String val01 = StringUtil.getValueString(":", stats[6]).replace("\r", "");
                String val02 = StringUtil.getValueString(":", stats[7]).replace("\r", "");
                resultMap.put("val01", Float.valueOf(val01));
                resultMap.put("val02", Float.valueOf(val02));
            } else {
                resultMap.put("val01", 0);
                resultMap.put("val02", 0);
            }
        } catch (Exception e) {
            resultMap.put("val01", 0);
            resultMap.put("val02", 0);
        }
        return JSON.toJSONString(resultMap);
    }
}

