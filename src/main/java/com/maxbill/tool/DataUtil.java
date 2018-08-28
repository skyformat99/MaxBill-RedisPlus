package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import redis.clients.jedis.Jedis;

public class DataUtil {

    public static Connect getCurrentOpenConnect() {
        return (Connect) WebUtil.getSessionAttribute("connect");
    }

    public static Jedis getCurrentJedisObject() {
        return (Jedis) WebUtil.getSessionAttribute("jedis");
    }
}
