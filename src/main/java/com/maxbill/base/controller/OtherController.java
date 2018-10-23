package com.maxbill.base.controller;

import com.alibaba.fastjson.JSON;
import com.maxbill.tool.MailUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OtherController {

    public String sendMail(String mailAddr, String mailText) {
        Map resultMap = new HashMap();
        try {
            boolean sendFlag = new MailUtil().sendMail(mailAddr, mailText);
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
