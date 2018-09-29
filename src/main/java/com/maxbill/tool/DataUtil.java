package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class DataUtil {


    public static Connect getCurrentOpenConnect() {
        return (Connect) WebUtil.getSessionAttribute("connect");
    }

    public static Jedis getCurrentJedisObject() {
        Connect connect = getCurrentOpenConnect();
        if (null != connect) {
            return RedisUtil.getJedis();
        } else {
            return null;
        }
    }


    public static JedisCluster getJedisClusterObject() {
        Connect connect = getCurrentOpenConnect();
        if (null != connect) {
            return ClusterUtil.getCluster(connect);
        } else {
            return null;
        }
    }


}
