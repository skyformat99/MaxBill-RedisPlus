package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class DataUtil {


    public static Connect getCurrentOpenConnect() {
        return (Connect) WebUtil.getSessionAttribute("connect");
    }

    public static Jedis getCurrentJedisObject() {
        Connect connect = getCurrentOpenConnect();
        if (null != connect) {
            return RedisUtil.openJedis(connect);
        } else {
            return null;
        }
    }

}
