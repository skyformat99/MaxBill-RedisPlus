package com.maxbill.base.controller;

import com.maxbill.base.bean.Connect;
import com.maxbill.core.desktop.Desktop;
import com.maxbill.tool.MailUtil;
import org.springframework.stereotype.Component;

import static com.maxbill.base.bean.ResultInfo.*;
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
        try {
            boolean sendFlag = MailUtil.sendMail(mailAddr, mailText);
            if (sendFlag) {
                return getOkByJson("发送邮件成功");
            } else {
                return getNoByJson("发送邮件失败");
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

}
