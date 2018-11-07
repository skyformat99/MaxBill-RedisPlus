package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.base.bean.Connect;
import com.maxbill.core.desktop.Desktop;
import com.maxbill.tool.MailUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.maxbill.tool.DataUtil.getCurrentOpenConnect;
import static com.maxbill.tool.ItemUtil.*;

@Component
public class OtherController {


    public void changeWebview(int pageNo) {
        Connect connect = getCurrentOpenConnect();
        String pageUrl = "";
        switch (pageNo) {
            case 1:
                pageUrl = PAGE_CONNECT;
                break;
            case 2:
                if (connect.getIsha().equals("0")) {
                    pageUrl = PAGE_DATA_SINGLES;
                }
                if (connect.getIsha().equals("1")) {
                    pageUrl = PAGE_DATA_CLUSTER;
                }
                break;
            case 3:
                if (connect.getIsha().equals("0")) {
                    pageUrl = PAGE_INFO_SINGLES;
                }
                if (connect.getIsha().equals("1")) {
                    pageUrl = PAGE_INFO_CLUSTER;
                }
                break;
            case 4:
                if (connect.getIsha().equals("0")) {
                    pageUrl = PAGE_CONF_SINGLES;
                }
                if (connect.getIsha().equals("1")) {
                    pageUrl = PAGE_CONF_CLUSTER;
                }
                break;
            case 5:
                if (connect.getIsha().equals("0")) {
                    pageUrl = PAGE_MONITOR_SINGLES;
                }
                if (connect.getIsha().equals("1")) {
                    pageUrl = PAGE_MONITOR_CLUSTER;
                }
                break;
        }
        System.out.println(pageUrl);
        Desktop.setWebViewPage(pageUrl);
    }

    public String sendMail(String mailAddr, String mailText) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            boolean sendFlag = MailUtil.sendMail(mailAddr, mailText);
            if (sendFlag) {
                resultMap.put("code", 200);
                resultMap.put("msgs", "发送邮件成功");
            } else {
                resultMap.put("code", 500);
                resultMap.put("msgs", "发送邮件失败");
            }
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("msgs", "发送邮件异常");
        }
        return JSON.toJSONString(resultMap);
    }

}
