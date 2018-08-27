package com.maxbill.base.controller;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.bean.DataTable;
import com.maxbill.base.bean.ResponseBean;
import com.maxbill.base.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {


    @Autowired
    private DataService dataService;

    @RequestMapping("/test")
    public String test() {
        try {
            this.dataService.createConnectTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "test";
    }

    @RequestMapping("/connect/select")
    public DataTable selectConnect() {
        DataTable tableData = new DataTable();
        try {
            List dataList = this.dataService.selectConnect();
            tableData.setCode(200);
            tableData.setCount(dataList.size());
            tableData.setData(dataList);
        } catch (Exception e) {
            tableData.setCode(500);
            tableData.setMsgs("加载数据失败");
        }
        return tableData;
    }


    @RequestMapping("/connect/insert")
    public ResponseBean insertConnect(Connect connect) {
        ResponseBean responseBean = new ResponseBean();
        try {
            int insFlag = this.dataService.insertConnect(connect);
            if (insFlag != 1) {
                responseBean.setCode(201);
                responseBean.setMsgs("新增连接失败");
            }
        } catch (Exception e) {
            responseBean.setCode(500);
            responseBean.setMsgs("新增连接异常");
        }
        return responseBean;
    }

    @RequestMapping("/connect/update")
    public ResponseBean updateConnect(Connect connect) {
        ResponseBean responseBean = new ResponseBean();
        try {
            int updFlag = this.dataService.updateConnect(connect);
            if (updFlag != 1) {
                responseBean.setCode(201);
                responseBean.setMsgs("修改连接失败");
            }
        } catch (Exception e) {
            responseBean.setCode(500);
            responseBean.setMsgs("修改连接异常");
        }
        return responseBean;
    }


    @RequestMapping("/connect/delete")
    public ResponseBean deleteConnect(String id) {
        ResponseBean responseBean = new ResponseBean();
        try {
            int delFlag = this.dataService.deleteConnectById(id);
            if (delFlag != 1) {
                responseBean.setCode(201);
                responseBean.setMsgs("删除连接失败");
            }
        } catch (Exception e) {
            responseBean.setCode(500);
            responseBean.setMsgs("删除连接异常");
        }
        return responseBean;
    }

}
