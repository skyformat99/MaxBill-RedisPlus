package com.maxbill.tool;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.maxbill.base.bean.Connect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JschUtil {

    static Logger log = LoggerFactory.getLogger("JschUtil");
    private static Session session;
    private static final int SSH_PORT = 22679;
    private static final int localPort = 55555;
    private static final String remoteHost = "zuoshuai.iok.la";
    private static final int remotePort = 6379;

    public static boolean openSSH(Connect connect) {
        boolean openFlag = true;
        try {
            closeSSH();
            session = new JSch().getSession(connect.getName(), connect.getHost(), 22);
            session.setPassword(connect.getPass());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            log.info("已使用SSH通道，SSH服务器版本：" + session.getServerVersion());
            session.setPortForwardingL(localPort, connect.getHost(), remotePort);
        } catch (Exception e) {
            openFlag = false;
            e.printStackTrace();
            closeSSH();
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
        Connect connect = new Connect();
        connect.setHost("192.168.76.137");
        connect.setName("root");
        connect.setPass("123456");
        openSSH(connect);
    }

}
