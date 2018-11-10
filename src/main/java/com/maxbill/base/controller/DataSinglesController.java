package com.maxbill.base.controller;

import com.maxbill.base.bean.ZTreeBean;
import com.maxbill.core.desktop.Desktop;
import com.maxbill.tool.DateUtil;
import com.maxbill.tool.FileUtil;
import com.maxbill.tool.KeyUtil;
import com.maxbill.tool.RedisUtil;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.maxbill.base.bean.ResultInfo.*;
import static com.maxbill.tool.DataUtil.getCurrentJedisObject;

@Component
public class DataSinglesController {

    public String treeInit() {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                List<ZTreeBean> treeList = new ArrayList<>();
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
                return getOkByJson(treeList);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String likeInit(int index, String pattern) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                long keysCount = RedisUtil.getKeysCount(jedis, index, pattern);
                ZTreeBean ztreeBean = new ZTreeBean();
                ztreeBean.setId(KeyUtil.getUUIDKey());
                ztreeBean.setName("DB" + index + " (" + keysCount + ")");
                ztreeBean.setParent(true);
                ztreeBean.setCount(keysCount);
                ztreeBean.setPage(1);
                ztreeBean.setPattern(pattern);
                ztreeBean.setIndex(index);
                return getOkByJson(ztreeBean);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String treeData(String id, int index, int page, int count, String pattern) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                List<ZTreeBean> treeList = RedisUtil.getKeyTree(jedis, index, id, pattern);
                int startIndex = (page - 1) * 50;
                int endIndex = page * 50;
                if (endIndex > count) {
                    endIndex = count;
                }
                treeList = treeList.subList(startIndex, endIndex);
                return getOkByJson(treeList);
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String keysData(int index, String keys) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, keys)) {
                    return getOkByJson(RedisUtil.getKeyInfo(jedis, index, keys));
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String renameKey(int index, String oldKey, String newKey) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, oldKey)) {
                    if (!RedisUtil.existsKey(jedis, index, newKey)) {
                        RedisUtil.renameKey(jedis, index, oldKey, newKey);
                        return getOkByJson("重命名KEY成功");
                    } else {
                        return getNoByJson("该KEY已存在");
                    }
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String retimeKey(int index, String key, int time) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    RedisUtil.retimeKey(jedis, index, key, time);
                    return getOkByJson("设置TTL成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String deleteKey(int index, String key) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    RedisUtil.deleteKey(jedis, index, key);
                    return getOkByJson("删除KEY成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String updateStr(int index, String key, String val) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                if (RedisUtil.existsKey(jedis, index, key)) {
                    RedisUtil.updateStr(jedis, index, key, val);
                    return getOkByJson("修改数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String insertVal(int type, int index, String key, String val) {
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
                    return getOkByJson("添加数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String deleteVal(int type, int index, String key, String val) {
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
                    return getOkByJson("删除数据成功");
                } else {
                    return getNoByJson("该KEY不存在");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String insertKey(int type, int index, String key, String val, int time) {
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
                return getOkByJson("新增数据成功");
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String removeKey(int index) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                jedis.select(index);
                jedis.flushDB();
                return getOkByJson("清空数据成功");
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    public String backupKey(int index, String pattern) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                String baseUrl = System.getProperty("user.home");
                String fileName = "RedisPlus-" + DateUtil.formatDate(new Date(), DateUtil.DATE_STR_FILE) + ".bak";
                String filePath = baseUrl + "/" + fileName;
                boolean flag = FileUtil.writeStringToFile(filePath, RedisUtil.backupKey(jedis, index, pattern));
                if (flag) {
                    return getOkByJson("数据成功导出至当前用户目录中");
                } else {
                    return getNoByJson("导出数据失败");
                }
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


    public String recoveKey(int index) {
        try {
            Jedis jedis = getCurrentJedisObject();
            if (null != jedis) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(Desktop.getRootStage());
                RedisUtil.recoveKey(jedis, index, FileUtil.readFileToString(file.toString()));
                return getOkByJson("还原数据成功");
            } else {
                return disconnect();
            }
        } catch (Exception e) {
            return exception(e);
        }
    }


}
