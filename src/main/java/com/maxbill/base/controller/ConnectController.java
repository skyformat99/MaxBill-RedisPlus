package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.maxbill.base.bean.Connect;
import com.maxbill.base.service.DataService;
import com.maxbill.tool.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.Map;

import static com.maxbill.core.desktop.Desktop.setEndsViewImage;
import static com.maxbill.core.desktop.Desktop.setEndsViewTitle;

@Component
public class ConnectController {

    @Autowired
    private DataService dataService;

    /**
     * 查询连接列表
     */
    public String selectConnect() {
        System.out.println("查询连接数据中...");
        return JSON.toJSONString(this.dataService.selectConnect());
    }

    /**
     * 新增连接数据
     */
    public String insertConnect(String json) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            JSONObject data = JSON.parseObject(json);
            Connect connect = new Connect();
            connect.setText(data.getString("text"));
            connect.setType(data.getString("type"));
            connect.setIsha(data.getString("isha"));
            connect.setRhost(data.getString("rhost"));
            connect.setRport(data.getString("rport"));
            connect.setRpass(data.getString("rpass"));
            connect.setShost(data.getString("shost"));
            connect.setSport(data.getString("sport"));
            connect.setSpass(data.getString("sname"));
            connect.setSpass(data.getString("spass"));
            this.dataService.insertConnect(connect);
            resultMap.put("code", 200);
            resultMap.put("msgs", "新增连接成功");
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "新增连接异常");
        }
        return JSON.toJSONString(resultMap);
    }

    /**
     * 更新连接数据
     */
    public String updateConnect(String json) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            JSONObject data = JSON.parseObject(json);
            Connect connect = new Connect();
            connect.setId(data.getString("id"));
            connect.setText(data.getString("text"));
            connect.setType(data.getString("type"));
            connect.setIsha(data.getString("isha"));
            connect.setRhost(data.getString("rhost"));
            connect.setRport(data.getString("rport"));
            connect.setRpass(data.getString("rpass"));
            connect.setShost(data.getString("shost"));
            connect.setSport(data.getString("sport"));
            connect.setSpass(data.getString("sname"));
            connect.setSpass(data.getString("spass"));
            this.dataService.updateConnect(connect);
            resultMap.put("code", 200);
            resultMap.put("msgs", "修改连接成功");
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "修改连接异常");
        }
        return JSON.toJSONString(resultMap);
    }

    /**
     * 删除连接数据
     */
    public String deleteConnect(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            this.dataService.deleteConnectById(id);
            resultMap.put("code", 200);
            resultMap.put("msgs", "删除连接成功");
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "删除连接异常");
        }
        return JSON.toJSONString(resultMap);
    }

    /**
     * 查询连接数据
     */
    public String querysConnect(String id) {
        return JSON.toJSONString(this.dataService.selectConnectById(id));
    }


    /**
     * 打开连接数据
     */
    public String createConnect(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Connect connect = this.dataService.selectConnectById(id);
            if ("1".equals(connect.getType())) {
                JschUtil.openSSH(connect);
            }
            if (connect.getIsha().equals("0")) {
                Jedis jedis = RedisUtil.openJedis(connect);
                if (null != jedis) {
                    DataUtil.setConfig("currentOpenConnect", connect);
                    DataUtil.setConfig("currentJedisObject", jedis);
                    setEndsViewTitle(ItemUtil.DESKTOP_STATUS_OK + connect.getText(), "ok");
                    setEndsViewImage(ItemUtil.DESKTOP_STATUS_IMAGE_OK);
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "打开连接成功");
                }
            } else {
                JedisCluster cluster = ClusterUtil.openCulter(connect);
                if (null == cluster || cluster.getClusterNodes().size() == 0) {
                } else {
                    DataUtil.setConfig("currentOpenConnect", connect);
                    DataUtil.setConfig("jedisClusterObject", cluster);
                    setEndsViewTitle(ItemUtil.DESKTOP_STATUS_OK + connect.getText(), "ok");
                    setEndsViewImage(ItemUtil.DESKTOP_STATUS_IMAGE_OK);
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "打开连接成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "打开连接异常");
        }
        return JSON.toJSONString(resultMap);
    }

    /**
     * 断开连接数据
     */
    public String disconConnect(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Connect connect = (Connect) DataUtil.getConfig("currentOpenConnect");
            if (connect.getIsha().equals("0")) {
                Jedis jedis = RedisUtil.openJedis(connect);
                if (null != jedis) {
                    RedisUtil.closeJedis(jedis);
                    DataUtil.clearConfig();
                }
            } else {
                ClusterUtil.closeCulter();
                DataUtil.clearConfig();
            }
            setEndsViewTitle(ItemUtil.DESKTOP_STATUS_NO, "no");
            setEndsViewImage(ItemUtil.DESKTOP_STATUS_IMAGE_NO);
            resultMap.put("code", 200);
            resultMap.put("msgs", "关闭连接成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "关闭连接异常");
        }
        return JSON.toJSONString(resultMap);
    }


    /**
     * 检测连接状态
     */
    public Integer isopenConnect() {
        Connect connect = (Connect) DataUtil.getConfig("currentOpenConnect");
        if (null != connect) {
            if (connect.getIsha().equals("0")) {
                Object jedis = DataUtil.getConfig("currentJedisObject");
                if (null != jedis) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                Object jedisCluster = DataUtil.getConfig("jedisClusterObject");
                if (null != jedisCluster) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

}
