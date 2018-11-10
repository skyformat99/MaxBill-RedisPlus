package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.RedisInfo;
import com.maxbill.base.bean.RedisNode;
import com.maxbill.base.bean.ResultInfo;
import com.maxbill.tool.ClusterUtil;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.RedisUtil;
import com.maxbill.tool.StringUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;

import static com.maxbill.tool.RedisUtil.getRedisInfo;

@Component
public class InfoClusterController {

    public String getBaseInfo() {
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = RedisUtil.getRedisInfoList(jedis);
                return ResultInfo.getOkByJson(redisInfo);
            } else {
                return ResultInfo.disconnect();
            }
        } catch (Exception e) {
            return ResultInfo.exception(e);
        }
    }


    public String getNodeInfo() {
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                return ResultInfo.getOkByJson(ClusterUtil.getClusterRelation(jedis.clusterNodes()));
            } else {
                return ResultInfo.disconnect();
            }
        } catch (Exception e) {
            return ResultInfo.exception(e);
        }
    }


    public String getMemInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = getRedisInfo(jedis);
                String[] memory = redisInfo.getMemory().split("\n");
                String value01 = StringUtil.getValueString(":", memory[2]).replace("\r", "");
                String value02 = StringUtil.getValueString(":", memory[5]).replace("\r", "");
                resultMap.put("value01", Float.valueOf(value01.substring(0, value01.length() - 1)));
                resultMap.put("value02", Float.valueOf(value02.substring(0, value02.length() - 1)));
            } else {
                resultMap.put("value01", 0);
                resultMap.put("value02", 0);
            }
        } catch (Exception e) {
            resultMap.put("value01", 0);
            resultMap.put("value02", 0);
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
                String value01 = StringUtil.getValueString(":", cpu[1]).replace("\r", "");
                String value02 = StringUtil.getValueString(":", cpu[2]).replace("\r", "");
                resultMap.put("value01", Float.valueOf(value01));
                resultMap.put("value02", Float.valueOf(value02));
            } else {
                resultMap.put("value01", 0);
                resultMap.put("value02", 0);
            }
        } catch (Exception e) {
            resultMap.put("value01", 0);
            resultMap.put("value02", 0);
        }
        return JSON.toJSONString(resultMap);
    }

    public String getKeyInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> xdata = new ArrayList<>();
        List<Long> ydata = new ArrayList<>();
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
                Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                for (String nk : clusterNodes.keySet()) {
                    if (masterNode.keySet().contains(nk)) {
                        Jedis jedis = clusterNodes.get(nk).getResource();
                        xdata.add(nk.split(":")[1]);
                        ydata.add(jedis.dbSize());
                        jedis.close();
                    }
                }
                resultMap.put("xdata", xdata);
                resultMap.put("ydata", ydata);
            } else {
                resultMap.put("xdata", Collections.EMPTY_LIST);
                resultMap.put("ydata", Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("xdata", Collections.EMPTY_LIST);
            resultMap.put("ydata", Collections.EMPTY_LIST);
        }
        return JSON.toJSONString(resultMap);
    }

    public String getNetInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = DataUtil.getCurrentJedisObject();
            if (null != jedis) {
                RedisInfo redisInfo = getRedisInfo(jedis);
                String[] stats = redisInfo.getStats().split("\n");
                String value01 = StringUtil.getValueString(":", stats[6]).replace("\r", "");
                String value02 = StringUtil.getValueString(":", stats[7]).replace("\r", "");
                resultMap.put("value01", Float.valueOf(value01));
                resultMap.put("value02", Float.valueOf(value02));
            } else {
                resultMap.put("value01", 0);
                resultMap.put("value02", 0);
            }
        } catch (Exception e) {
            resultMap.put("value01", 0);
            resultMap.put("value02", 0);
        }
        return JSON.toJSONString(resultMap);
    }
}

