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

    private static Object getConfig(String key) {
        return confMap.get(key);
    }

    public static Connect getCurrentOpenConnect() {
        return (Connect) DataUtil.getConfig("currentOpenConnect");
    }


    public static Jedis getCurrentJedisObject() {
        return (Jedis) DataUtil.getConfig("currentJedisObject");
    }


    public static JedisCluster getJedisClusterObject() {
        return (JedisCluster) DataUtil.getConfig("jedisClusterObject");
    }


}
