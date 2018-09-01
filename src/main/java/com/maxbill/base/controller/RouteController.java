package com.maxbill.base.controller;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.service.DataService;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RouteController {

    @Autowired
    private DataService dataService;

    @GetMapping("/root")
    public ModelAndView toRoot(ModelAndView mv) {
        initSystem();
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            mv.addObject("status", "已经连接到： " + connect.getName());
        } else {
            mv.addObject("status", "未连接服务");
        }
        mv.setViewName("root");
        return mv;
    }

    @GetMapping("/root/save")
    public String toRootSave() {
        return "save";
    }

    @GetMapping("/root/edit")
    public ModelAndView toRootEdit(ModelAndView mv, String id) {
        mv.addObject("data", this.dataService.selectConnectById(id));
        mv.setViewName("edit");
        return mv;
    }


    @GetMapping("/data")
    public ModelAndView toData(ModelAndView mv) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            mv.addObject("status", "已经连接到： " + connect.getName());
            mv.setViewName("data");
        } else {
            mv.addObject("status", "未连接服务");
            mv.setViewName("root");
        }
        return mv;
    }


    @GetMapping("/info")
    public ModelAndView toInfo(ModelAndView mv) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            RedisUtil.getRedisConfig(DataUtil.getCurrentJedisObject());
            mv.addObject("info", RedisUtil.getRedisInfoList(DataUtil.getCurrentJedisObject()));
            mv.addObject("status", "已经连接到： " + connect.getName());
            mv.setViewName("info");
        } else {
            mv.addObject("status", "未连接服务");
            mv.setViewName("root");
        }
        return mv;
    }

    @GetMapping("/conf")
    public ModelAndView toConf(ModelAndView mv) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            mv.addObject("status", "已经连接到： " + connect.getName());
            mv.setViewName("conf");
        } else {
            mv.addObject("status", "未连接服务");
            mv.setViewName("root");
        }
        return mv;
    }

    @GetMapping("/self")
    public ModelAndView toSelf(ModelAndView mv) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            mv.addObject("status", "已经连接到： " + connect.getName());
        } else {
            mv.addObject("status", "未连接服务");
        }
        mv.setViewName("self");
        return mv;
    }


    private void initSystem() {
        try {
            int tableCount = this.dataService.isExistsTable("T_CONNECT");
            if (tableCount == 0) {
                this.dataService.createConnectTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
