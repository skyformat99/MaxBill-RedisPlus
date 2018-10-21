package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectController {

    @Autowired
    private DataService dataService;

    public String selectConnect() {
        System.out.println(111);
        List dataList = null;
        try {
            dataList = this.dataService.selectConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(dataList));
        return JSON.toJSONString(dataList);
    }
}
