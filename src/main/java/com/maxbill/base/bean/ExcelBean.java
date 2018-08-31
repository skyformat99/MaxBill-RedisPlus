package com.maxbill.base.bean;

import lombok.Data;

import java.util.List;

@Data
public class ExcelBean {

    // 表头
    private List<String> titles;

    // 数据
    private List<Connect> rows;

    // 页签名称
    private String name;

}
