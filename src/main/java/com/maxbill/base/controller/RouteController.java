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
        return "root_save";
    }

    @GetMapping("/root/edit")
    public ModelAndView toRootEdit(ModelAndView mv, String id) {
        mv.addObject("data", this.dataService.selectConnectById(id));
        mv.setViewName("root_edit");
        return mv;
    }


    @GetMapping("/data")
    public ModelAndView toData(ModelAndView mv) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            mv.addObject("status", "已经连接到： " + connect.getName());
        } else {
            mv.addObject("status", "未连接服务");
        }
        mv.setViewName("data");
        return mv;
    }


    @GetMapping("/info")
    public ModelAndView toInfo(ModelAndView mv) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            RedisUtil.getRedisLog(DataUtil.getCurrentJedisObject());
            mv.addObject("info", RedisUtil.getRedisInfoList(DataUtil.getCurrentJedisObject()));
            mv.addObject("status", "已经连接到： " + connect.getName());
        } else {
            mv.addObject("status", "未连接服务");
        }
        mv.setViewName("info");
        return mv;
    }

    @GetMapping("/conf")
    public String toConf() {
        return "conf";
    }

    @GetMapping("/exec")
    public String toExec() {
        return "exec";
    }

}
