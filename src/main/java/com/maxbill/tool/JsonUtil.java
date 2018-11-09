package com.maxbill.tool;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.KeyBean;

public class JsonUtil {

    public static KeyBean parseKeyBeanObject(String json) {
        try {
            if (!json.equals("")) {
                return JSON.parseObject(json, KeyBean.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
