package com.maxbill.base.bean;

import lombok.Data;

@Data
public class KeyBean {

    private String key;

    private String type;

    private long size;

    private long ttl;

    private String value;

}
