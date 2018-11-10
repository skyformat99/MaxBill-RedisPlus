package com.maxbill.base.controller;

import com.maxbill.base.bean.RedisNode;
import com.maxbill.base.bean.ZTreeBean;
import com.maxbill.core.desktop.Desktop;
import com.maxbill.tool.*;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.maxbill.base.bean.ResultInfo.*;

@Component
public class DataClusterController {


    public String nodeInfo() {
        try {
            List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
            if (null != nodeList) {
                return getOkByJson(nodeList);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String treeInit() {
        try {
            JedisCluster cluster = ClusterUtil.openCulter(DataUtil.getCurrentOpenConnect());
            if (null != cluster) {
                List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
                Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                long total = 0l;
                for (String nk : clusterNodes.keySet()) {
                    if (masterNode.keySet().contains(nk)) {
                        Jedis jedis = clusterNodes.get(nk).getResource();
                        total = total + jedis.dbSize();
                    }
                }
                ZTreeBean ztreeBean = new ZTreeBean();
                ztreeBean.setId(KeyUtil.getUUIDKey());
                ztreeBean.setName("全部集群节点的KEY" + "(" + total + ")");
                ztreeBean.setPattern("");
                ztreeBean.setParent(true);
                ztreeBean.setCount(total);
                ztreeBean.setPage(1);
                ztreeBean.setIndex(0);
                return getOkByJson(ztreeBean);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String likeInit(String pattern) {
        try {
            JedisCluster cluster = ClusterUtil.openCulter(DataUtil.getCurrentOpenConnect());
            if (null != cluster) {
                if (StringUtils.isEmpty(pattern)) {
                    pattern = "*";
                }
                List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
                Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                long total = 0l;
                for (String nk : clusterNodes.keySet()) {
                    if (masterNode.keySet().contains(nk)) {
                        Jedis jedis = clusterNodes.get(nk).getResource();
                        total = total + jedis.keys(pattern).size();
                    }
                }
                ZTreeBean ztreeBean = new ZTreeBean();
                ztreeBean.setId(KeyUtil.getUUIDKey());
                ztreeBean.setName("全部集群节点的KEY" + "(" + total + ")");
                ztreeBean.setPattern(pattern);
                ztreeBean.setParent(true);
                ztreeBean.setCount(total);
                ztreeBean.setPage(1);
                ztreeBean.setIndex(0);
                return getOkByJson(ztreeBean);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String treeData(String id, int page, int count, String pattern) {
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
                        zTreeBean.setIcon("../image/data-01.png");
                        treeList.add(zTreeBean);
                    }
                }
                return getOkByJson(treeList);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
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


    public String insertkey(int type, String key, String val, int time) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                //1:set,2:zset,3:list,4:hash,5:string
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
                if (time != -1) {
                    ClusterUtil.retimeKey(cluster, key, time);
                }
                return getOkByJson("新增数据成功");
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String removeKey() {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
                Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                for (String nk : clusterNodes.keySet()) {
                    if (masterNode.keySet().contains(nk)) {
                        Jedis jedis = clusterNodes.get(nk).getResource();
                        jedis.flushDB();
                        jedis.close();
                    }
                }
                return getOkByJson("清空数据成功");
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String backupKey(String pattern) {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                List<RedisNode> nodeList = ClusterUtil.getClusterNode(DataUtil.getCurrentOpenConnect());
                Map<String, RedisNode> masterNode = ClusterUtil.getMasterNode(nodeList);
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                StringBuffer dataBuffer = new StringBuffer("");
                String baseUrl = System.getProperty("user.home");
                String fileName = "RedisPlus-" + DateUtil.formatDate(new Date(), DateUtil.DATE_STR_FILE) + ".bak";
                String filePath = baseUrl + "/" + fileName;
                for (String nk : clusterNodes.keySet()) {
                    if (masterNode.keySet().contains(nk)) {
                        Jedis jedis = clusterNodes.get(nk).getResource();
                        dataBuffer.append(ClusterUtil.backupKey(jedis, pattern));
                        jedis.close();
                    }
                }
                boolean expFlag = FileUtil.writeStringToFile(filePath, dataBuffer.toString());
                if (expFlag) {
                    return getOkByJson("各个节点数据成功导出至当前用户目录中");
                } else {
                    return getNoByJson("导出数据失败");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String recoveKey() {
        try {
            JedisCluster cluster = DataUtil.getJedisClusterObject();
            if (null != cluster) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(Desktop.getRootStage());
                ClusterUtil.recoveKey(cluster, FileUtil.readFileToString(file.toString()));
                return getOkByJson("还原数据成功");
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


}
