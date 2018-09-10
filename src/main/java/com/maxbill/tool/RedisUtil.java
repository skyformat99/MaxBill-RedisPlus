package com.maxbill.tool;

import com.maxbill.base.bean.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Slowlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    //redis连接池
    private static JedisPool jedisPool;

    //资源锁
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
        if (StringUtils.isEmpty(connect.getPass())) {
            jedisPool = new JedisPool(config, connect.getHost(), Integer.valueOf(connect.getPort()), TIME_OUT);
        } else {
            jedisPool = new JedisPool(config, connect.getHost(), Integer.valueOf(connect.getPort()), TIME_OUT, connect.getPass());
        }
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
     * 判断key是否存在
     */
    public static boolean existsKey(Jedis jedis, String key, int index) {
        jedis.select(index);
        return jedis.exists(key);
    }

    /**
     * 重命名key
     */
    public static String renameKey(Jedis jedis, String oldKey, String newKey, int index) {
        jedis.select(index);
        return jedis.rename(oldKey, newKey);
    }


    /**
     * 删除key
     */
    public static long deleteKey(Jedis jedis, String key, int index) {
        jedis.select(index);
        return jedis.del(key);
    }

    /**
     * 获取库的key值
     */
    public static long dbSize(Jedis jedis, int index) {
        jedis.select(index);
        return jedis.dbSize();
    }

    /**
     * 释放Jedis连接
     */
    public static void closeJedis(Jedis jedis) {
        jedis.close();
    }


    /**
     * 设置list
     */
    public static void setList(Jedis jedis, String key, List<T> list) {
        jedis.set(key.getBytes(), ObjectUtil.serialize(list));
    }

//    /**
//     * 获取list
//     */
//    public static <T> List<T> getList(Jedis jedis, String key) {
//        if (jedis == null || !jedis.exists(key.getBytes())) {
//            return null;
//        }
//        byte[] in = jedis.get(key.getBytes());
//        List<T> list = (List<T>) ObjectUtil.deserialize(in);
//        return list;
//    }
//
//    /**
//     * 设置map
//     */
//    public static <T> void setMap(Jedis jedis, String key, Map<String, T> map) {
//        jedis.set(key.getBytes(), ObjectUtil.serialize(map));
//    }
//
//    /**
//     * 获取map
//     */
//    public static <T> Map<String, T> getMap(Jedis jedis, String key) {
//        if (jedis == null || !jedis.exists(key.getBytes())) {
//            return null;
//        }
//        byte[] in = jedis.get(key.getBytes());
//        Map<String, T> map = (Map<String, T>) ObjectUtil.deserialize(in);
//        return map;
//    }

    public static long getKeysCount(Jedis jedis, int index, String pattern) {
        long startTime = System.currentTimeMillis();
        jedis.select(index);
        if (StringUtils.isEmpty(pattern)) {
            pattern = "*";
        }
        Set<String> keySet = jedis.keys(pattern);
        long endTime = System.currentTimeMillis();
        System.err.println("查询keys耗时：" + (endTime - startTime));
        return keySet.size();
    }


    public static List<ZTreeBean> getKeyTree(Jedis jedis, int index, String pid, String pattern) {
        List<ZTreeBean> treeList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        jedis.select(index);
        if (StringUtils.isEmpty(pattern)) {
            pattern = "*";
        }
        Set<String> keySet = jedis.keys(pattern);
        long endTime = System.currentTimeMillis();
        System.err.println("查询keys耗时：" + (endTime - startTime));
        ZTreeBean zTreeBean = null;
        if (null != keySet) {
            for (String key : keySet) {
                zTreeBean = new ZTreeBean();
                zTreeBean.setId(KeyUtil.getUUIDKey());
                zTreeBean.setPId(pid);
                zTreeBean.setName(key);
                zTreeBean.setParent(false);
                zTreeBean.setIndex(index);
                treeList.add(zTreeBean);
            }
        }
        return treeList;
    }


    // 解析服务器信息
    public static RedisInfo getRedisInfo(Jedis jedis) {
        RedisInfo redisInfo = null;
        Client client = jedis.getClient();
        client.info();
        String info = client.getBulkReply();
        String[] infos = info.split("# ");
        if (null != infos && infos.length > 0) {
            redisInfo = new RedisInfo();
            for (String infoStr : infos) {
                if (infoStr.startsWith("Server")) {
                    redisInfo.setServer(infoStr);
                }
                if (infoStr.startsWith("Clients")) {
                    redisInfo.setClient(infoStr);
                }
                if (infoStr.startsWith("Memory")) {
                    redisInfo.setMemory(infoStr);
                }
                if (infoStr.startsWith("Persistence")) {
                    redisInfo.setPersistence(infoStr);
                }
                if (infoStr.startsWith("Stats")) {
                    redisInfo.setStats(infoStr);
                }
                if (infoStr.startsWith("Replication")) {
                    redisInfo.setReplication(infoStr);
                }
                if (infoStr.startsWith("CPU")) {
                    redisInfo.setCpu(infoStr);
                }
                if (infoStr.startsWith("Cluster")) {
                    redisInfo.setCluster(infoStr);
                }
                if (infoStr.startsWith("Keyspace")) {
                    redisInfo.setKeyspace(infoStr);
                }
            }
        }
        return redisInfo;
    }


    /**
     * 获取redis信息
     */
    public static RedisInfo getRedisInfoList(Jedis jedis) {
        RedisInfo redisInfoBean = getRedisInfo(jedis);
        RedisInfo redisInfo = null;
        if (null != redisInfoBean) {
            redisInfo = new RedisInfo();
            redisInfo.setServer(returnServerInfo(redisInfoBean).toString());
            redisInfo.setClient(returnClientInfo(redisInfoBean).toString());
            redisInfo.setMemory(returnMemoryInfo(redisInfoBean).toString());
            redisInfo.setPersistence(returnPersistenceInfo(redisInfoBean).toString());
            redisInfo.setStats(returnStatsInfo(redisInfoBean).toString());
            redisInfo.setCpu(returnCpuInfo(redisInfoBean).toString());
        }
        return redisInfo;
    }

    /**
     * 解析服务端信息
     */
    private static StringBuffer returnServerInfo(RedisInfo redisInfo) {
        //服务端信息
        StringBuffer serverBuf = new StringBuffer("");
        String serverInfo = redisInfo.getServer();
        if (!StringUtils.isEmpty(serverInfo)) {
            String[] server = serverInfo.split("\n");
            for (String info : server) {
                String key = StringUtil.getKeyString(info);
                String value = StringUtil.getValueString(info);
                switch (key) {
                    case "redis_version":
                        serverBuf.append("服务版本: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "redis_mode":
                        serverBuf.append("服务模式: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "os":
                        serverBuf.append("系统版本: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "arch_bits":
                        serverBuf.append("系统架构: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "multiplexing_api":
                        serverBuf.append("事件机制: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "process_id":
                        serverBuf.append("进程编号: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "tcp_port":
                        serverBuf.append("服务端口: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "uptime_in_seconds":
                        serverBuf.append("运行时间: ").append(value);
                        serverBuf.append("</br>");
                        break;
                    case "config_file":
                        serverBuf.append("配置文件: ").append(value);
                        serverBuf.append("</br>");
                        break;
                }
            }
        }
        return serverBuf;
    }


    /**
     * 解析客户端信息
     */
    private static StringBuffer returnClientInfo(RedisInfo redisInfo) {
        StringBuffer clientBuf = new StringBuffer("");
        String clientInfo = redisInfo.getClient();
        if (!StringUtils.isEmpty(clientInfo)) {
            String[] client = clientInfo.split("\n");
            for (String info : client) {
                String key = StringUtil.getKeyString(info);
                String value = StringUtil.getValueString(info);
                switch (key) {
                    case "connected_clients":
                        clientBuf.append("当前已连接客户端数量: ").append(value);
                        clientBuf.append("</br>");
                        break;
                    case "blocked_clients":
                        clientBuf.append("当前已阻塞客户端数量: ").append(value);
                        clientBuf.append("</br>");
                        break;
                    case "client_longest_output_list":
                        clientBuf.append("当前连接的客户端当中，最长输出列表: ").append(value);
                        clientBuf.append("</br>");
                        break;
                    case "client_biggest_input_buf":
                        clientBuf.append("当前连接的客户端当中，最大输入缓存: ").append(value);
                        clientBuf.append("</br>");
                        break;
                }
            }
        }
        return clientBuf;
    }


    /**
     * 解析客户端信息
     */
    private static StringBuffer returnMemoryInfo(RedisInfo redisInfo) {
        StringBuffer memoryBuf = new StringBuffer("");
        String memoryInfo = redisInfo.getMemory();
        if (!StringUtils.isEmpty(memoryInfo)) {
            String[] memory = memoryInfo.split("\n");
            for (String info : memory) {
                String key = StringUtil.getKeyString(info);
                String value = StringUtil.getValueString(info);
                switch (key) {
                    case "used_memory":
                        memoryBuf.append("已占用内存量: ").append(value);
                        memoryBuf.append("</br>");
                        break;
                    case "used_memory_rss":
                        memoryBuf.append("分配内存总量: ").append(value);
                        memoryBuf.append("</br>");
                        break;
                    case "used_memory_peak_human":
                        memoryBuf.append("内存高峰值: ").append(value);
                        memoryBuf.append("</br>");
                        break;
                    case "mem_fragmentation_ratio":
                        memoryBuf.append("内存碎片率: ").append(value);
                        memoryBuf.append("</br>");
                        break;
                    case "mem_allocator":
                        memoryBuf.append("内存分配器: ").append(value);
                        memoryBuf.append("</br>");
                        break;
                }
            }
        }
        return memoryBuf;
    }


    /**
     * 解析持久化信息
     */
    private static StringBuffer returnPersistenceInfo(RedisInfo redisInfo) {
        StringBuffer persistenceBuf = new StringBuffer("");
        String persistenceInfo = redisInfo.getPersistence();
        if (!StringUtils.isEmpty(persistenceInfo)) {
            String[] persistence = persistenceInfo.split("\n");
            for (String info : persistence) {
                String key = StringUtil.getKeyString(info);
                String value = StringUtil.getValueString(info);
                switch (key) {
                    case "rdb_bgsave_in_progress":
                        persistenceBuf.append("是否正在创建RDB的文件: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "rdb_last_save_time":
                        persistenceBuf.append("最近成功创建RDB时间戳: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "rdb_last_bgsave_status":
                        persistenceBuf.append("最近创建RDB文件的结果: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "rdb_last_bgsave_time_sec":
                        persistenceBuf.append("最近创建RDB文件的耗时: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "aof_enabled":
                        persistenceBuf.append("服务是否已经开启了AOF: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "aof_rewrite_in_progress":
                        persistenceBuf.append("是否正在创建AOF的文件: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "aof_last_rewrite_time_sec":
                        persistenceBuf.append("最近创建AOF文件的耗时: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "aof_last_bgrewrite_status":
                        persistenceBuf.append("最近创建AOF文件的结果: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "aof_current_size":
                        persistenceBuf.append("当前AOF文件记录的大小: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                    case "aof_buffer_length":
                        persistenceBuf.append("当前AOF文件缓冲区大小: ").append(value);
                        persistenceBuf.append("</br>");
                        break;
                }
            }
        }
        return persistenceBuf;
    }

    /**
     * 解析连接的信息
     */
    private static StringBuffer returnStatsInfo(RedisInfo redisInfo) {
        StringBuffer statsBuf = new StringBuffer();
        String statsInfo = redisInfo.getStats();
        if (!StringUtils.isEmpty(statsInfo)) {
            String[] stats = statsInfo.split("\n");
            for (String info : stats) {
                String key = StringUtil.getKeyString(info);
                String value = StringUtil.getValueString(info);
                switch (key) {
                    case "total_connections_received":
                        statsBuf.append("已连接客户端总数: ").append(value);
                        statsBuf.append("</br>");
                        break;
                    case "total_commands_processed":
                        statsBuf.append("执行过的命令总数: ").append(value);
                        statsBuf.append("</br>");
                        break;
                    case "instantaneous_ops_per_sec":
                        statsBuf.append("服务每秒执行数量: ").append(value);
                        statsBuf.append("</br>");
                        break;
                    case "total_net_input_bytes":
                        statsBuf.append("服务输入网络流量: ").append(value);
                        statsBuf.append("</br>");
                        break;
                    case "total_net_output_bytes":
                        statsBuf.append("服务输出网络流量: ").append(value);
                        statsBuf.append("</br>");
                        break;
                    case "rejected_connections":
                        statsBuf.append("拒绝连接客户端数: ").append(value);
                        statsBuf.append("</br>");
                        break;
                }
            }
        }
        return statsBuf;
    }


    /**
     * 解析连接的信息
     */
    private static StringBuffer returnCpuInfo(RedisInfo redisInfo) {
        //处理器信息
        StringBuffer cpuBuf = new StringBuffer();
        String cpuInfo = redisInfo.getCpu();
        if (!StringUtils.isEmpty(cpuInfo)) {
            String[] cpu = cpuInfo.split("\n");
            for (String info : cpu) {
                String key = StringUtil.getKeyString(info);
                String value = StringUtil.getValueString(info);
                switch (key) {
                    case "used_cpu_sys":
                        cpuBuf.append("服务主进程在核心态累计CPU耗时: ").append(value);
                        cpuBuf.append("</br>");
                        break;
                    case "used_cpu_user":
                        cpuBuf.append("服务主进程在用户态累计CPU耗时: ").append(value);
                        cpuBuf.append("</br>");
                        break;
                    case "used_cpu_sys_children":
                        cpuBuf.append("服务后台进程在核心态累计CPU耗时: ").append(value);
                        cpuBuf.append("</br>");
                        break;
                    case "used_cpu_user_children":
                        cpuBuf.append("服务后台进程在用户态累计CPU耗时: ").append(value);
                        cpuBuf.append("</br>");
                        break;
                }
            }
        }
        return cpuBuf;
    }


    /**
     * 获取Redis Key信息
     */
    public static KeyBean getKeyInfo(Jedis jedis, String key, int index) {
        KeyBean keyBean = new KeyBean();
        jedis.select(index);
        keyBean.setKey(key);
        keyBean.setValue(jedis.get(key));
        keyBean.setSize(keyBean.getValue().getBytes().length);
        keyBean.setType(jedis.type(key));
        keyBean.setTtl(jedis.ttl(key));
        return keyBean;
    }

    /**
     * 获取Redis配置信息
     */
    public static List<ConfigBean> getRedisConfig(Jedis jedis) {
        List<ConfigBean> confList = new ArrayList<>();
        List<String> configList = jedis.configGet("*");
        for (int i = 0; i < configList.size(); i++) {
            if (i % 2 != 0) {
                ConfigBean configBean = new ConfigBean();
                configBean.setKey(configList.get(i - 1));
                String value = configList.get(i);
                if (StringUtils.isEmpty(value)) {
                    configBean.setValue("");
                } else {
                    configBean.setValue(value);
                }
                confList.add(configBean);
            }
        }
        return confList;
    }

    /**
     * 修改redis配置信息
     */
    public static void setRedisConfig(Jedis jedis, Map<String, String[]> confMap) {
        for (String key : confMap.keySet()) {
            jedis.configSet(key, confMap.get(key)[0]);
        }
    }

    /**
     * 获取redis日志信息
     */
    public static List<Slowlog> getRedisLog(Jedis jedis) {
        List<Slowlog> logList = jedis.slowlogGet(100);
        return logList;
    }

    /**
     * 执行redis命令信息
     */
    public static String execRediscommand(Jedis jedis, String command) {
        return jedis.info(command);
    }

    public static void main(String[] args) {
        Connect connect = new Connect();
        connect.setHost("maxbill");
        connect.setPort("6379");
        connect.setPass("");
        Jedis jedis = openJedis(connect);
        jedis.select(0);
        for (int i = 1; i <= 10; i++) {
            jedis.set("test" + i, "test" + i);
        }
        System.out.println("exec finish");
        //List list = new ArrayList<>();
        //list.add("1111111111111111111111111111");
        //list.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        //list.add("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
        //list.add("!@#$%^&*()_+=-|}{?><  QQQQQQ");
        //setList(jedis, "list-test", list);
        jedis.close();
    }

}
