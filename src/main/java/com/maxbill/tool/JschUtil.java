package com.maxbill.tool;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.maxbill.base.bean.Connect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JschUtil {

    static Logger log = LoggerFactory.getLogger("JschUtil");
    private static Session session;

    public static boolean openSSH(Connect connect) {
        boolean openFlag = true;
        try {
            closeSSH();
            session = new JSch().getSession(connect.getSname(), connect.getShost(), Integer.valueOf(connect.getSport()));
            session.setPassword(connect.getSpass());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            log.info("已使用SSH通道，SSH服务器版本：" + session.getServerVersion());
            int aport = session.setPortForwardingL(55555, connect.getShost(), Integer.valueOf(connect.getRport()));
            log.info(connect.getRhost() + ":" + aport + " -> " + connect.getShost() + ":" + connect.getSport());
        } catch (Exception e) {
            openFlag = false;
            log.info("连接SSH服务器失败");
            closeSSH();
            e.printStackTrace();
        }
        return openFlag;
    }

    public static void closeSSH() {
        try {
            if (null != session) {
                session.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }

}
