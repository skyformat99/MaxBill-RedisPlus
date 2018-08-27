package com.maxbill.base.controller;

import com.maxbill.base.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RouteController {

    @Autowired
    private DataService dataService;

    @GetMapping("/root")
    public String toRoot() {
        return "root";
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
    public String toData() {
        return "data";
    }

    @GetMapping("/info")
    public String toInfo() {
        return "info";
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
