package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.bean.ZTreeBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class RedisUtil {

    //可用连接实例的最大数目，默认值为8；
    private static int MAX_TOTAL = 100;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 50;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 5000;

    //超时时间
    private static final int TIME_OUT = 60000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static boolean TEST_ON_RETURN = true;

    private static JedisPool jedisPool;

    private static ReentrantLock lock = new ReentrantLock();

    private RedisUtil() {
    }

    /**
     * 初始化JedisPool
     */
    private static void initJedisPool(Connect connect) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);
        config.setTestOnReturn(TEST_ON_RETURN);
        jedisPool = new JedisPool(config, connect.getHost(), Integer.valueOf(connect.getPort()), TIME_OUT, connect.getPass());
    }

    /**
     * 从JedisPool中获取Jedis
     */
    public static Jedis openJedis(Connect connect) {
        //防止吃初始化时多线程竞争问题
        lock.lock();
        initJedisPool(connect);
        lock.unlock();
        return jedisPool.getResource();
    }

    /**
     * 释放Jedis连接
     */
    public static void closeJedis(Jedis jedis) {
        jedis.close();
    }


    public static List<ZTreeBean> getKeyTree(Jedis jedis, ZTreeBean rootTree) {
        List<ZTreeBean> treeList = new ArrayList<>();
        treeList.add(rootTree);
        Set<String> keySet = jedis.keys("*");
        ZTreeBean zTreeBean = null;
        if (null != keySet) {
            for (String key : keySet) {
                zTreeBean = new ZTreeBean();
                zTreeBean.setId(KeyUtil.getUUIDKey());
                zTreeBean.setPId(rootTree.getId());
                zTreeBean.setName(key);
                treeList.add(zTreeBean);
            }
        }
        return treeList;
    }


    public static void main(String[] args) {
        Connect connect = new Connect();
        connect.setHost("maxbill");
        connect.setPort("6379");
        connect.setPass("123456");
        Jedis jedis = openJedis(connect);
        jedis.set("a-b-1", "1");
        jedis.set("a-b-2", "2");
        jedis.set("a-b-3", "3");
        jedis.keys("*");
        closeJedis(jedis);
    }

}
