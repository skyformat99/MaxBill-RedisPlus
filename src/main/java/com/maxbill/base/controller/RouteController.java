package com.maxbill.base.controller;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.service.DataService;
import com.maxbill.tool.ClusterUtil;
import com.maxbill.tool.DataUtil;
import com.maxbill.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RouteController {

    private final String APP_VERSION = "Version: 1.1.5";

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

    @GetMapping("/root/node")
    public ModelAndView toRootNode(ModelAndView mv, String id) {
        Connect connect = DataUtil.getCurrentOpenConnect();
        Connect data = this.dataService.selectConnectById(id);
        boolean isCulter = ClusterUtil.isCulter(data);
        if (isCulter) {
            data.setIsha("集群模式");
        } else {
            data.setIsha("单机模式");
        }
        mv.addObject("data", data);
        if (null != connect && connect.getId().equals(id)) {
            mv.addObject("flag", 1);
        } else {
            mv.addObject("flag", 0);
        }
        mv.setViewName("node");
        return mv;
    }


    @GetMapping("/data")
    public ModelAndView toData(ModelAndView mv) {
        mv.addAllObjects(setPageInfo());
        //判断是集群还是单机
        boolean isCulter = ClusterUtil.isCulter(DataUtil.getCurrentOpenConnect());
        if (isCulter) {
            mv.setViewName("many");
        } else {
            mv.setViewName("data");
        }
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

    @GetMapping("/book")
    public ModelAndView toBook(ModelAndView mv) {
        mv.addAllObjects(setPageInfo());
        mv.setViewName("book");
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
            mvMap.put("message", "已经连接到： " + connect.getText());
        } else {
            mvMap.put("marktip", "conn-no");
            mvMap.put("message", "未连接服务");
        }
        mvMap.put("version", APP_VERSION);
        return mvMap;
    }

}
