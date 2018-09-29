package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
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
            if ("1".equals(connect.getType())) {
                JschUtil.openSSH(connect);
                jedis = new Jedis(connect.getRhost(), 55555);
            } else {
                jedis = new Jedis(connect.getRhost(), Integer.valueOf(connect.getRport()));
            }
            String serverInfo = jedis.info("server");
            String[] server = serverInfo.split("\n");
            for (String info : server) {
                String key = StringUtil.getKeyString(FLAG_COLON, info);
                String value = StringUtil.getValueString(FLAG_COLON, info);
                if (key.equals("redis_mode") && value.equals("cluster")) {
                    isCulter = true;
                }
            }
            System.out.println();
            if (null != jedis) {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCulter;
    }


    public static void main(String[] args) {
        Connect connect = new Connect();
        connect.setRhost("192.168.77.141");
        connect.setRport("7001");
        connect.setType("0");
        openCulter(connect);
        ScanParams scanParams = new ScanParams();
        scanParams.count(50);
        scanParams.match("{*}");
        ScanResult<String> resList = cluster.scan(String.valueOf(0), scanParams);
        System.out.println(resList.getResult().toString());

    }
}
