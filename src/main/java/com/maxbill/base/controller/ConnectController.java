package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.maxbill.base.bean.Connect;
import com.maxbill.base.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConnectController {

    @Autowired
    private DataService dataService;

    public String selectConnect() {
        return JSON.toJSONString(this.dataService.selectConnect());
    }

    public String insertConnect(String json) {
        System.out.println(json);
        Map resultMap = new HashMap();
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

}
