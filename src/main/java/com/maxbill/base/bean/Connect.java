package com.maxbill.base.bean;

import lombok.Data;

@Data
public class Connect {

    private String id;

    //SSH
    private String ssh;

    //用户名
    private String name;

    //主机
    private String host;

    //端口
    private String port;

    //密码
    private String pass;

    //时间
    private String time;

    //类型：0默认，1：ssh
    private String type;


}
