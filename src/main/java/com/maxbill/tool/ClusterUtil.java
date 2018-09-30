package com.maxbill.tool;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.Connect;
import com.maxbill.base.bean.KeyBean;
import com.maxbill.base.bean.RedisNode;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;

import java.util.*;

import static com.maxbill.tool.StringUtil.FLAG_COLON;

public class ClusterUtil {

    //可用连接实例的最大数目，默认值为8；
    private static int MAX_TOTAL = 50;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 10;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 5000;

    //超时时间
    private static final int TIME_OUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static boolean TEST_ON_RETURN = true;

    private static JedisCluster cluster;


    public static JedisCluster openCulter(Connect connect) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);
        config.setTestOnReturn(TEST_ON_RETURN);
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        List<RedisNode> nodeList = getClusterNode(connect);
        for (RedisNode node : nodeList) {
            String host = StringUtil.getKeyString(FLAG_COLON, node.getAddr());
            String port = StringUtil.getValueString(FLAG_COLON, node.getAddr());
            nodes.add(new HostAndPort(host, Integer.valueOf(port)));
        }
        String pass = connect.getRpass();
        if (StringUtils.isEmpty(pass)) {
            cluster = new JedisCluster(nodes, TIME_OUT, config);
        } else {
            cluster = new JedisCluster(nodes, 1000, 1000, 1, pass, config);
        }
        return cluster;
    }


    public static void closeCulter() {
        try {
            cluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static JedisCluster getCluster(Connect connect) {
        if (cluster != null) {
            return cluster;
        } else {
            return openCulter(connect);
        }
    }

    public static List<RedisNode> getClusterNode(Connect connect) {
        List<RedisNode> nodeList = new ArrayList<>();
        Jedis jedis = null;
        try {
            if ("1".equals(connect.getType())) {
                jedis = new Jedis(connect.getRhost(), 55555);
            } else {
                jedis = new Jedis(connect.getRhost(), Integer.valueOf(connect.getRport()));
            }
            String clusterNodes = jedis.clusterNodes();
            String[] nodes = clusterNodes.split("\n");
            for (String node : nodes) {
                String[] nodeFileds = node.split(" ");
                RedisNode redisNode = new RedisNode();
                redisNode.setId(nodeFileds[0]);
                redisNode.setAddr(nodeFileds[1]);
                redisNode.setFlag(nodeFileds[2]);
                redisNode.setPid(nodeFileds[3]);
                redisNode.setPing(nodeFileds[4]);
                redisNode.setPong(nodeFileds[5]);
                redisNode.setEpoch(nodeFileds[6]);
                redisNode.setState(nodeFileds[7]);
                if (nodeFileds.length == 9) {
                    redisNode.setHash(nodeFileds[8]);
                }
                nodeList.add(redisNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                jedis.close();
            }
            return nodeList;
        }
    }

    public static Map<String, RedisNode> getMasterNode(List<RedisNode> nodeList) {
        Map<String, RedisNode> nodeMap = new HashMap<>();
        for (RedisNode node : nodeList) {
            if (node.getFlag().indexOf("master") > -1) {
                nodeMap.put(node.getAddr(), node);
            }
        }
        return nodeMap;
    }


    public static boolean isCulter(Connect connect) {
        boolean isCulter = false;
        Jedis jedis = null;
        try {
            String pass = connect.getRpass();
            if ("1".equals(connect.getType())) {
                JschUtil.openSSH(connect);
                jedis = new Jedis(connect.getRhost(), 55555);
            } else {
                jedis = new Jedis(connect.getRhost(), Integer.valueOf(connect.getRport()));
            }
            if (!StringUtils.isEmpty(pass)) {
                jedis.auth(pass);
            }
            String serverInfo = jedis.info("server");
            String[] server = serverInfo.split("\n");
            for (String info : server) {
                String key = StringUtil.getKeyString(FLAG_COLON, info);
                String value = StringUtil.getValueString(FLAG_COLON, info);
                if (key.equals("redis_mode") && value.equals("cluster\r")) {
                    isCulter = true;
                }
            }
            if (null != jedis) {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCulter;
    }


    /**
     * 判断key是否存在
     */
    public static boolean existsKey(JedisCluster jedisCluster, String key) {
        return jedisCluster.exists(key);
    }


    /**
     * 重命名key
     */
    public static String renameKey(JedisCluster jedisCluster, String oldKey, String newKey) {
        return jedisCluster.rename(oldKey, newKey);
    }

    /**
     * 设置key时间
     */
    public static long retimeKey(JedisCluster jedisCluster, String key, int time) {
        return jedisCluster.expire(key, time);
    }

    /**
     * 删除key
     */
    public static long deleteKey(JedisCluster jedisCluster, String key) {
        return jedisCluster.del(key);
    }

    /**
     * 修改String的Value
     */
    public static String updateStr(JedisCluster jedisCluster, String key, String val) {
        return jedisCluster.set(key, val);
    }

    /**
     * 添加Set的item
     */
    public static long insertSet(JedisCluster jedisCluster, String key, String val) {
        return jedisCluster.sadd(key, val);
    }

    /**
     * 添加Zset的item
     */
    public static long insertZset(JedisCluster jedisCluster, String key, String val) {
        return jedisCluster.zadd(key, 1, val);
    }

    /**
     * 添加List的item
     */
    public static long insertList(JedisCluster jedisCluster, String key, String val) {
        return jedisCluster.rpush(key, val);
    }

    /**
     * 添加Hase的key和val
     */
    public static long insertHash(JedisCluster jedisCluster, String key, String mapKey, String mapVal) {
        return jedisCluster.hset(key, mapKey, mapVal);
    }

    /**
     * 删除Set的item
     */
    public static long deleteSet(JedisCluster jedisCluster, String key, String val) {
        return jedisCluster.srem(key, val);
    }

    /**
     * 删除Zset的item
     */
    public static long deleteZset(JedisCluster jedisCluster, String key, String val) {
        return jedisCluster.zrem(key, val);
    }

    /**
     * 删除List的item
     */
    public static long deleteList(JedisCluster jedisCluster, String key, long keyIndex) {
        String tempItem = KeyUtil.getUUIDKey();
        jedisCluster.lset(key, keyIndex, tempItem);
        return jedisCluster.lrem(key, 0, tempItem);
    }

    /**
     * 删除List的item
     */
    public static long deleteHash(JedisCluster jedisCluster, String key, String mapKey) {
        return jedisCluster.hdel(key, mapKey);
    }

    /**
     * 获取Redis Key信息
     */
    public static KeyBean getKeyInfo(JedisCluster jedisCluster, String key) {
        KeyBean keyBean = new KeyBean();
        keyBean.setKey(key);
        keyBean.setType(jedisCluster.type(key));
        keyBean.setTtl(jedisCluster.ttl(key));
        //none (key不存在)
        //string (字符串)
        //list (列表)
        //set (集合)
        //zset (有序集)
        //hash (哈希表)
        switch (keyBean.getType()) {
            case "set":
                Set<String> set = jedisCluster.smembers(key);
                StringBuffer setBuf = new StringBuffer();
                for (String info : set) {
                    setBuf.append(info).append(",");
                }
                String textSet = setBuf.toString();
                keyBean.setText(textSet.substring(0, textSet.length() - 1));
                keyBean.setJson(JSON.toJSONString(set));
                keyBean.setRaws(keyBean.getText().replace(",", "</br>"));
                break;
            case "none":
                keyBean.setText("");
                break;
            case "list":
                List<String> list = jedisCluster.lrange(key, 0, -1);
                StringBuffer listBuf = new StringBuffer();
                for (String info : list) {
                    listBuf.append(info).append(",");
                }
                String textList = listBuf.toString();
                keyBean.setText(textList.substring(0, textList.length() - 1));
                keyBean.setJson(JSON.toJSONString(list));
                keyBean.setRaws(keyBean.getText().replace(",", "</br>"));
                break;
            case "zset":
                Set<String> zset = jedisCluster.zrange(key, 0, -1);
                StringBuffer zsetBuf = new StringBuffer();
                for (String info : zset) {
                    zsetBuf.append(info).append(",");
                }
                String textZset = zsetBuf.toString();
                keyBean.setText(textZset.substring(0, textZset.length() - 1));
                keyBean.setJson(JSON.toJSONString(zset));
                keyBean.setRaws(keyBean.getText().replace(",", "</br>"));
                break;
            case "hash":
                Map<String, String> map = jedisCluster.hgetAll(key);
                StringBuffer mapBuf = new StringBuffer();
                for (Map.Entry entry : map.entrySet()) {
                    mapBuf.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
                }
                String textMap = mapBuf.toString();
                keyBean.setText(textMap.substring(0, textMap.length() - 1));
                keyBean.setJson(JSON.toJSONString(map));
                keyBean.setRaws(keyBean.getText().replace(",", "</br>"));
                break;
            case "string":
                keyBean.setText(jedisCluster.get(key));
                keyBean.setJson(JSON.toJSONString(keyBean.getText()));
                keyBean.setRaws(keyBean.getText());
                break;
        }
        keyBean.setSize(keyBean.getText().getBytes().length);
        return keyBean;
    }


    public static void main(String[] args) {


    }
}
