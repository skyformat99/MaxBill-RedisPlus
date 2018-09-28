package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.bean.RedisNode;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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


    public static JedisCluster openCulter() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);
        config.setTestOnReturn(TEST_ON_RETURN);
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        List<RedisNode> nodeList = getClusterNode("192.168.77.141", 7001);
        for (RedisNode node : nodeList) {
            String host = StringUtil.getKeyString(FLAG_COLON, node.getAddr());
            String port = StringUtil.getValueString(FLAG_COLON, node.getAddr());
            nodes.add(new HostAndPort(host, Integer.valueOf(port)));
        }
        cluster = new JedisCluster(nodes, config);
        return cluster;
    }


    public static void closeCulter() {
        try {
            cluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<RedisNode> getClusterNode(String host, int port) {
        List<RedisNode> nodeList = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = new Jedis(host, port);
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


    public static void main(String[] args) {
        openCulter();
        for (int i = 1; i <= 30; i++) {
            cluster.set(i + "", i + "");
        }
        closeCulter();
    }
}
