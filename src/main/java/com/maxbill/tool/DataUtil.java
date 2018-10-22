package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

import static com.maxbill.tool.StringUtil.FLAG_COLON;

public class DataUtil {

    private static Map<String, Object> confMap = new HashMap();


    public DataUtil() {
        confMap.put("currentOpenConnect", null);
        confMap.put("currentJedisObject", null);
        confMap.put("jedisClusterObject", null);
    }

    public static void setConfig(String key, Object value) {
        confMap.put(key, value);
    }

    public static void clearConfig() {
        confMap.clear();
    }

    public static Object getConfig(String key) {
        return confMap.get(key);
    }

//    public static Jedis getCurrentJedisObject() throws Exception {
//        Connect connect = getCurrentOpenConnect();
//        if (null != connect) {
//            Jedis jedis = null;
//            if (connect.getIsha().equals("0")) {
//                jedis = RedisUtil.getJedis();
//            } else {
//                JedisCluster cluster = ClusterUtil.openCulter(DataUtil.getCurrentOpenConnect());
//                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
//                for (String nk : clusterNodes.keySet()) {
//                    if (StringUtil.getValueString(FLAG_COLON, nk).equals(connect.getRport())) {
//                        jedis = clusterNodes.get(nk).getResource();
//                    }
//                }
//                ClusterUtil.closeCulter();
//            }
//            return jedis;
//        } else {
//            return null;
//        }
//    }
//
//
//    public static JedisCluster getJedisClusterObject() throws Exception {
//        Connect connect = getCurrentOpenConnect();
//        if (null != connect) {
//            return ClusterUtil.getCluster(connect);
//        } else {
//            return null;
//        }
//    }


}
