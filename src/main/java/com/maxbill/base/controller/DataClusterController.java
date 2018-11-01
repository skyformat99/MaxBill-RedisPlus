package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.RedisNode;
import com.maxbill.base.bean.ZTreeBean;
import com.maxbill.tool.ClusterUtil;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.KeyUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxbill.base.bean.ResultInfo.*;
import static com.maxbill.tool.DataUtil.getCurrentJedisObject;

@Component
public class DataClusterController {

    public String treeInit() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
            Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
            JedisCluster cluster = ClusterUtil.openCulter(DataUtil.getCurrentOpenConnect());
            Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
            long total = 0l;
            for (String nk : clusterNodes.keySet()) {
                if (masterNode.keySet().contains(nk)) {
                    Jedis jedis = clusterNodes.get(nk).getResource();
                    total = total + jedis.dbSize();
                }
            }
            ZTreeBean zTreeBean = new ZTreeBean();
            zTreeBean.setId(KeyUtil.getUUIDKey());
            zTreeBean.setName("全部集群节点的KEY" + "(" + total + ")");
            zTreeBean.setPattern("");
            zTreeBean.setParent(true);
            zTreeBean.setCount(total);
            zTreeBean.setPage(1);
            zTreeBean.setIndex(0);
            resultMap.put("code", 200);
            resultMap.put("data", zTreeBean);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String likeInit(String pattern) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (StringUtils.isEmpty(pattern)) {
                pattern = "*";
            }
            List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
            Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
            JedisCluster cluster = ClusterUtil.openCulter(DataUtil.getCurrentOpenConnect());
            Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
            long total = 0l;
            for (String nk : clusterNodes.keySet()) {
                if (masterNode.keySet().contains(nk)) {
                    Jedis jedis = clusterNodes.get(nk).getResource();
                    total = total + jedis.keys(pattern).size();
                }
            }
            ZTreeBean zTreeBean = new ZTreeBean();
            zTreeBean.setId(KeyUtil.getUUIDKey());
            zTreeBean.setName("全部集群节点的KEY" + "(" + total + ")");
            zTreeBean.setPattern(pattern);
            zTreeBean.setParent(true);
            zTreeBean.setCount(total);
            zTreeBean.setPage(1);
            zTreeBean.setIndex(0);
            resultMap.put("code", 200);
            resultMap.put("data", zTreeBean);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String treeData(String id, int page, int count, String pattern) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (StringUtils.isEmpty(pattern)) {
                    pattern = "*";
                }
                List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
                Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                List<ZTreeBean> treeList = new ArrayList<>();
                List<String> keyList = new ArrayList<>();
                for (String nk : clusterNodes.keySet()) {
                    if (masterNode.keySet().contains(nk)) {
                        Jedis jedis = clusterNodes.get(nk).getResource();
                        keyList.addAll(jedis.keys(pattern));
                        RedisUtil.closeJedis(jedis);
                    }
                }
                int startIndex = (page - 1) * 50;
                int endIndex = page * 50;
                if (endIndex > count) {
                    endIndex = count;
                }
                keyList = keyList.subList(startIndex, endIndex);
                if (!keyList.isEmpty()) {
                    for (String key : keyList) {
                        ZTreeBean zTreeBean = new ZTreeBean();
                        zTreeBean.setId(KeyUtil.getUUIDKey());
                        zTreeBean.setPId(id);
                        zTreeBean.setName(key);
                        zTreeBean.setParent(false);
                        zTreeBean.setIcon("../image/data-key.png");
                        treeList.add(zTreeBean);
                    }
                }
                resultMap.put("code", 200);
                resultMap.put("data", treeList);
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


    public String keysData(String key) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    return getOkByJson(ClusterUtil.getKeyInfo(cluster, key));
                } else {
                    return getNoByJson("该key不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String renameKey(String oldKey, String newKey) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, oldKey)) {
                    if (!ClusterUtil.existsKey(cluster, newKey)) {
                        ClusterUtil.renameKey(cluster, oldKey, newKey);
                        return getOkByJson("重命名KEY成功");
                    } else {
                        return getNoByJson("该KEY已存在");
                    }
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String retimeKey(String key, int time) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    ClusterUtil.retimeKey(cluster, key, time);
                    return getOkByJson("设置KEY失效时间成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String deleteKey(String key) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    ClusterUtil.deleteKey(cluster, key);
                    return getOkByJson("删除KEY成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String updateStr(String key, String val) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    ClusterUtil.updateStr(cluster, key, val);
                    return getOkByJson("修改数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String insertVal(int type, String key, String val) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    //1:set,2:zset,3:list,4:hash
                    switch (type) {
                        case 1:
                            ClusterUtil.insertSet(cluster, key, val);
                            break;
                        case 2:
                            ClusterUtil.insertZset(cluster, key, val);
                            break;
                        case 3:
                            ClusterUtil.insertList(cluster, key, val);
                            break;
                        case 4:
                            String[] valArray = val.split(":");
                            String mapKey = valArray[0];
                            String mapVal = valArray[1];
                            ClusterUtil.insertHash(cluster, key, mapKey, mapVal);
                            break;
                        case 5:
                            cluster.set(key, val);
                            break;
                    }
                    return getOkByJson("添加数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String deleteVal(int type, String key, String val) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    //1:set,2:zset,3:list,4:hash
                    switch (type) {
                        case 1:
                            ClusterUtil.deleteSet(cluster, key, val);
                            break;
                        case 2:
                            ClusterUtil.deleteZset(cluster, key, val);
                            break;
                        case 3:
                            long keyIndex = Long.parseLong(val);
                            ClusterUtil.deleteList(cluster, key, keyIndex);
                            break;
                        case 4:
                            String mapKey = val;
                            ClusterUtil.deleteHash(cluster, key, mapKey);
                            break;
                    }
                    return getOkByJson("删除数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String insertkey(int type, String key, String val,int time) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                if (ClusterUtil.existsKey(cluster, key)) {
                    //1:set,2:zset,3:list,4:hash
                    switch (type) {
                        case 1:
                            ClusterUtil.insertSet(cluster, key, val);
                            break;
                        case 2:
                            ClusterUtil.insertZset(cluster, key, val);
                            break;
                        case 3:
                            ClusterUtil.insertList(cluster, key, val);
                            break;
                        case 4:
                            String[] valArray = val.split(":");
                            String mapKey = valArray[0];
                            String mapVal = valArray[1];
                            ClusterUtil.insertHash(cluster, key, mapKey, mapVal);
                            break;
                        case 5:
                            cluster.set(key, val);
                            break;
                    }
                    return getOkByJson("添加数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }



}
