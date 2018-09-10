package com.maxbill.base.controller;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.service.DataService;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RouteController {

    private final String APP_VERSION = "Version: 1.0.6";

    @Autowired
    private DataService dataService;

    @GetMapping("/root")
    public ModelAndView toRoot(ModelAndView mv) {
        initSystem();
        mv.addAllObjects(setPageInfo());
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
        mv.addAllObjects(setPageInfo());
        mv.setViewName("data");
        return mv;
    }


    @GetMapping("/info")
    public ModelAndView toInfo(ModelAndView mv) {
        mv.addObject("info", RedisUtil.getRedisInfoList(DataUtil.getCurrentJedisObject()));
        mv.addAllObjects(setPageInfo());
        mv.setViewName("info");
        return mv;
    }

    @GetMapping("/conf")
    public ModelAndView toConf(ModelAndView mv) {
        mv.addAllObjects(setPageInfo());
        mv.setViewName("conf");
        return mv;
    }

    @GetMapping("/self")
    public ModelAndView toSelf(ModelAndView mv) {
        mv.addAllObjects(setPageInfo());
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

    private Map setPageInfo() {
        Map mvMap = new HashMap();
        Connect connect = DataUtil.getCurrentOpenConnect();
        if (null != connect) {
            mvMap.put("marktip", "conn-ok");
            mvMap.put("message", "已经连接到： " + connect.getName());
        } else {
            mvMap.put("marktip", "conn-no");
            mvMap.put("message", "未连接服务");
        }
        mvMap.put("version", APP_VERSION);
        return mvMap;
    }

}
