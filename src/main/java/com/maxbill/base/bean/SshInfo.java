package com.maxbill.base.bean;

import lombok.Data;

@Data
public class SshInfo {

    private String sshId;

    //用户名
    private String sshName;

    //主机
    private String sshHost;

    //端口
    private String sshPort;

    //密码
    private String sshPass;

}
