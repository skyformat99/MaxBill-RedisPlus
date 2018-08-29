package com.maxbill.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;

public class MailUtil {

    private static final String MAILTIPS = "软件使用反馈";
    private static final String FROMUSER = "vau.ting@qq.com ";
    private static final String LOOKUSER = "maxbill1993@qq.com";

    @Autowired
    private static JavaMailSender javaMailSender;

    public static boolean sendMail(String user, String msgs) {
        try {
            if (StringUtils.isEmpty(user)) {
                user = "";
            } else {
                user = "[" + user + "]";
            }
            SimpleMailMessage mainMessage = new SimpleMailMessage();
            mainMessage.setFrom(FROMUSER);
            mainMessage.setTo(LOOKUSER);
            mainMessage.setSubject(MAILTIPS + user);
            mainMessage.setText(msgs);
            javaMailSender.send(mainMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
