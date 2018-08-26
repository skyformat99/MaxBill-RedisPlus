package com.maxbill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    @GetMapping("/root")
    public String toRoot() {
        return "root";
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
