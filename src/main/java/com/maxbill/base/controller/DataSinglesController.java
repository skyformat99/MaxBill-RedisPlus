package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.KeyBean;
import com.maxbill.base.bean.ZTreeBean;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.KeyUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxbill.tool.DataUtil.getCurrentJedisObject;

@Component
public class DataSinglesController {

    public Integer sayHello() {
        System.out.println("1111111111");
        return 1;
    }

    public String treeInit() {
        List<ZTreeBean> treeList = new ArrayList<>();
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                String role = jedis.info("server");
                for (int i = 0; i < 16; i++) {
                    long dbSize = 0l;
                    if (i > 0 && role.indexOf("redis_mode:cluster") > -1) {
                        break;
                    }
                    if (role.indexOf("redis_mode:cluster") > -1) {
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
        Map resultMap = new HashMap();
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
        Map resultMap = new HashMap();
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
        Map resultMap = new HashMap();
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
        Map resultMap = new HashMap();
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
        Map resultMap = new HashMap();
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


//
//    @RequestMapping("/data/updateStr")
//    public ResponseBean updateStr(int index, String key, String val) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.updateStr(jedis, index, key, val);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//    @RequestMapping("/data/insertSet")
//    public ResponseBean insertSet(int index, String key, String val) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.insertSet(jedis, index, key, val);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//    @RequestMapping("/data/insertZset")
//    public ResponseBean insertZset(int index, String key, String val) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.insertZset(jedis, index, key, val);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//    @RequestMapping("/data/insertList")
//    public ResponseBean insertList(int index, String key, String val) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.insertList(jedis, index, key, val);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//    RequestMapping("/data/insertHash")
//
//    public ResponseBean insertHash(int index, String key, String mapKey, String mapVal) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.insertHash(jedis, index, key, mapKey, mapVal);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//    @RequestMapping("/data/deleteSet")
//    public ResponseBean deleteSet(int index, String key, String val) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.deleteSet(jedis, index, key, val);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//
//    @RequestMapping("/data/deleteZset")
//    public ResponseBean deleteZset(int index, String key, String val) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.deleteZset(jedis, index, key, val);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//
//    @RequestMapping("/data/deleteList")
//    public ResponseBean deleteList(int index, String key, long keyIndex) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.deleteList(jedis, index, key, keyIndex);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }
//
//
//    @RequestMapping("/data/deleteHash")
//    public ResponseBean deleteHash(int index, String key, String mapKey) {
//        ResponseBean responseBean = new ResponseBean();
//        try {
//            Jedis jedis = getCurrentJedisObject();
//            if (null != jedis) {
//                if (RedisUtil.existsKey(jedis, index, key)) {
//                    RedisUtil.deleteHash(jedis, index, key, mapKey);
//                } else {
//                    responseBean.setCode(0);
//                    responseBean.setMsgs("'" + key + "' 该key不存在");
//                }
//                RedisUtil.closeJedis(jedis);
//            } else {
//                responseBean.setCode(0);
//                responseBean.setMsgs("打开连接异常");
//            }
//        } catch (Exception e) {
//            log.error(e);
//            responseBean.setCode(500);
//            responseBean.setMsgs("打开连接异常");
//        }
//        return responseBean;
//    }


}
