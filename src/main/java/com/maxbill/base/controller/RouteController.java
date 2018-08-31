package com.maxbill.base.controller;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.bean.ExcelBean;
import com.maxbill.base.service.DataService;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.DateUtil;
import com.maxbill.tool.ExcelUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


}
