package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.KeyBean;
import com.maxbill.base.bean.ZTreeBean;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.KeyUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataSinglesController {

    public Integer sayHello() {
        System.out.println("1111111111");
        return 1;
    }

    public String treeInit() {
        List<ZTreeBean> treeList = new ArrayList<>();
        try {
            Jedis jedis = (Jedis) DataUtil.getConfig("currentJedisObject");
            if (null != jedis) {
                String role = jedis.info("server");
                for (int i = 0; i < 16; i++) {
                    long dbSize = 0l;
                    if (i > 0 && role.indexOf("redis_mode:cluster") > -1) {
                        break;
                    }
                    if (role.indexOf("redis_mode:cluster") > -1) {
                        dbSize = RedisUtil.dbSize(jedis, null);
                    } else {
                        dbSize = RedisUtil.dbSize(jedis, i);
                    }
                    ZTreeBean zTreeBean = new ZTreeBean();
                    zTreeBean.setId(KeyUtil.getUUIDKey());
                    zTreeBean.setName("DB" + i + " (" + dbSize + ")");
                    zTreeBean.setPattern("");
                    zTreeBean.setParent(true);
                    zTreeBean.setCount(dbSize);
                    zTreeBean.setPage(1);
                    zTreeBean.setIndex(i);
                    treeList.add(zTreeBean);
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(treeList);
    }

    public String likeInit(int index, String pattern) {
        Map resultMap = new HashMap();
        try {
            Jedis jedis = (Jedis) DataUtil.getConfig("currentJedisObject");
            if (null != jedis) {
                long keysCount = RedisUtil.getKeysCount(jedis, index, pattern);
                ZTreeBean zTreeBean = new ZTreeBean();
                zTreeBean.setId(KeyUtil.getUUIDKey());
                zTreeBean.setName("DB" + index + " (" + keysCount + ")");
                zTreeBean.setParent(true);
                zTreeBean.setCount(keysCount);
                zTreeBean.setPage(1);
                zTreeBean.setPattern(pattern);
                zTreeBean.setIndex(index);
                resultMap.put("code", 200);
                resultMap.put("data", zTreeBean);
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String treeData(String id, int index, int page, int count, String pattern) {
        List<ZTreeBean> treeList = new ArrayList<>();
        try {
            Jedis jedis = (Jedis) DataUtil.getConfig("currentJedisObject");
            if (null != jedis) {
                treeList = RedisUtil.getKeyTree(jedis, index, id, pattern);
                int startIndex = (page - 1) * 50;
                int endIndex = page * 50;
                if (endIndex > count) {
                    endIndex = count;
                }
                treeList = treeList.subList(startIndex, endIndex);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(treeList);
    }

    public String keysData(int index, String keys) {
        Map resultMap = new HashMap();
        try {
            Jedis jedis = (Jedis) DataUtil.getConfig("currentJedisObject");
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, keys)) {
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "打开连接成功");
                    resultMap.put("data", RedisUtil.getKeyInfo(jedis, index, keys));
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该key不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


}
