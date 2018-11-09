package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.ZTreeBean;
import com.maxbill.core.desktop.Desktop;
import com.maxbill.tool.DateUtil;
import com.maxbill.tool.FileUtil;
import com.maxbill.tool.KeyUtil;
import com.maxbill.tool.RedisUtil;
import com.sun.glass.ui.Window;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.*;

import static com.maxbill.tool.DataUtil.getCurrentJedisObject;

@Component
public class DataSinglesController {


    public String treeInit() {
        List<ZTreeBean> treeList = new ArrayList<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                String role = jedis.info("server");
                for (int i = 0; i < 16; i++) {
                    long dbSize = 0l;
                    if (i > 0 && role.contains("redis_mode:cluster")) {
                        break;
                    }
                    if (role.contains("redis_mode:cluster")) {
                        dbSize = RedisUtil.dbSize(jedis, null);
                    } else {
                        dbSize = RedisUtil.dbSize(jedis, i);
                    }
                    ZTreeBean zTreeBean = new ZTreeBean();
                    zTreeBean.setId(KeyUtil.getUUIDKey());
                    zTreeBean.setName("DB" + i + " (" + dbSize + ")");
                    zTreeBean.setPattern("");
                    zTreeBean.setParent(true);
                    zTreeBean.setCount(dbSize);
                    zTreeBean.setPage(1);
                    zTreeBean.setIndex(i);
                    treeList.add(zTreeBean);
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(treeList);
    }

    public String likeInit(int index, String pattern) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                long keysCount = RedisUtil.getKeysCount(jedis, index, pattern);
                ZTreeBean zTreeBean = new ZTreeBean();
                zTreeBean.setId(KeyUtil.getUUIDKey());
                zTreeBean.setName("DB" + index + " (" + keysCount + ")");
                zTreeBean.setParent(true);
                zTreeBean.setCount(keysCount);
                zTreeBean.setPage(1);
                zTreeBean.setPattern(pattern);
                zTreeBean.setIndex(index);
                resultMap.put("code", 200);
                resultMap.put("data", zTreeBean);
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String treeData(String id, int index, int page, int count, String pattern) {
        List<ZTreeBean> treeList = new ArrayList<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                treeList = RedisUtil.getKeyTree(jedis, index, id, pattern);
                int startIndex = (page - 1) * 50;
                int endIndex = page * 50;
                if (endIndex > count) {
                    endIndex = count;
                }
                treeList = treeList.subList(startIndex, endIndex);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(treeList);
    }

    public String keysData(int index, String keys) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, keys)) {
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "打开连接成功");
                    resultMap.put("data", RedisUtil.getKeyInfo(jedis, index, keys));
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该key不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "获取数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String renameKey(int index, String oldKey, String newKey) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, oldKey)) {
                    if (!RedisUtil.existsKey(jedis, index, newKey)) {
                        RedisUtil.renameKey(jedis, index, oldKey, newKey);
                        resultMap.put("code", 200);
                        resultMap.put("msgs", "重命名KEY成功");
                    } else {
                        resultMap.put("code", 500);
                        resultMap.put("msgs", "该KEY已存在");
                    }
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该KEY不存在");
                }
                RedisUtil.closeJedis(jedis);
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String retimeKey(int index, String key, int time) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    RedisUtil.retimeKey(jedis, index, key, time);
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "设置TTL成功");
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该KEY不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String deleteKey(int index, String key) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    RedisUtil.deleteKey(jedis, index, key);
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "删除KEY成功");
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该KEY不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String updateStr(int index, String key, String val) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    RedisUtil.updateStr(jedis, index, key, val);
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "修改数据成功");
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该KEY不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String insertVal(int type, int index, String key, String val) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    //1:set,2:zset,3:list,4:hash
                    switch (type) {
                        case 1:
                            RedisUtil.insertSet(jedis, index, key, val);
                            break;
                        case 2:
                            RedisUtil.insertZset(jedis, index, key, val);
                            break;
                        case 3:
                            RedisUtil.insertList(jedis, index, key, val);
                            break;
                        case 4:
                            String[] valArray = val.split(":");
                            String mapKey = valArray[0];
                            String mapVal = valArray[1];
                            RedisUtil.insertHash(jedis, index, key, mapKey, mapVal);
                            break;
                    }
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "添加数据成功");
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该KEY不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "添加数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String deleteVal(int type, int index, String key, String val) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    //1:set,2:zset,3:list,4:hash
                    switch (type) {
                        case 1:
                            RedisUtil.deleteSet(jedis, index, key, val);
                            break;
                        case 2:
                            RedisUtil.deleteZset(jedis, index, key, val);
                            break;
                        case 3:
                            long keyIndex = Long.parseLong(val);
                            RedisUtil.deleteList(jedis, index, key, keyIndex);
                            break;
                        case 4:
                            String mapKey = val;
                            RedisUtil.deleteHash(jedis, index, key, mapKey);
                            break;
                    }
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "删除数据成功");
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "该KEY不存在");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "删除数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String insertKey(int type, int index, String key, String val, int time) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                //1:set,2:zset,3:list,4:hash
                switch (type) {
                    case 1:
                        RedisUtil.insertSet(jedis, index, key, val);
                        break;
                    case 2:
                        RedisUtil.insertZset(jedis, index, key, val);
                        break;
                    case 3:
                        RedisUtil.insertList(jedis, index, key, val);
                        break;
                    case 4:
                        String[] valArray = val.split(":");
                        String mapKey = valArray[0];
                        String mapVal = valArray[1];
                        RedisUtil.insertHash(jedis, index, key, mapKey, mapVal);
                        break;
                    case 5:
                        RedisUtil.insertStr(jedis, index, key, val);
                        break;
                }
                if (time != -1) {
                    RedisUtil.retimeKey(jedis, index, key, time);
                }
                resultMap.put("code", 200);
                resultMap.put("msgs", "新增数据成功");
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "添加数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String removeKey(int index) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                jedis.select(index);
                jedis.flushDB();
                resultMap.put("code", 200);
                resultMap.put("msgs", "清空数据成功");
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }

    public String backupKey(int index, String pattern) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                String baseUrl = System.getProperty("user.home");
                String filePath = baseUrl + "/" + "RedisPlus-" + DateUtil.formatDate(new Date(), DateUtil.DATE_STR_FILE) + ".bak";
                boolean flag = FileUtil.writeStringToFile(filePath, RedisUtil.backupKey(jedis, index, pattern));
                if (flag) {
                    resultMap.put("code", 200);
                    resultMap.put("msgs", "数据成功导出至当前用户目录中");
                } else {
                    resultMap.put("code", 500);
                    resultMap.put("msgs", "导出数据失败");
                }
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


    public String recoveKey(int index) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(Desktop.getRootStage());
                RedisUtil.recoveKey(jedis, index, FileUtil.readFileToString(file.toString()));
                resultMap.put("code", 200);
                resultMap.put("msgs", "还原数据成功");
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "连接已断开");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("msgs", "操作数据异常");
        }
        return JSON.toJSONString(resultMap);
    }


}
