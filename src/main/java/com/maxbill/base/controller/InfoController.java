package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.RedisInfo;
import com.maxbill.tool.RedisUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.util.Slowlog;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxbill.tool.DataUtil.getCurrentJedisObject;

@Component
public class InfoController {

    public String getBaseInfo() {
        Map resultMap = new HashMap();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = RedisUtil.getRedisInfoList(jedis);
                resultMap.put("code", 200);
                resultMap.put("data", redisInfo);
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String getLogsInfo() {
        Map resultMap = new HashMap();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                List<Slowlog> logs = RedisUtil.getRedisLog(jedis);
                Collections.reverse(logs);
                resultMap.put("code", 200);
                resultMap.put("data", logs);
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    //    public ResponseBean realInfo() {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = DataUtil.getCurrentJedisObject();
//            if (null != jedis) {
//                Map resultMap = new HashMap();
//                resultMap.put("key", DateUtil.formatDate(new Date(), DateUtil.TIME_STR));
//                RedisInfo redisInfo = getRedisInfo(jedis);
//                String[] memory = redisInfo.getMemory().split("\n");
//                String val01 = StringUtil.getValueString(":", memory[1]).replace("\r", "");
//                String[] cpu = redisInfo.getCpu().split("\n");
//                String val02 = StringUtil.getValueString(":", cpu[1]).replace("\r", "");
//                resultMap.put("val01", (float) (Math.round((Float.valueOf(val01) / 1048576) * 100)) / 100);
//                resultMap.put("val02", Float.valueOf(val02));
//                responseBean.setData(resultMap);
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//            log.error(e);
//        }
//        return responseBean;
//    }
}
