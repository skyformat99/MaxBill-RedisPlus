package com.maxbill.base.bean;


import com.maxbill.tool.DateUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @功能 响应实体
 * @作者 MaxBill
 * @时间 2018年7月19日
 * @邮箱 maxbill1993@163.com
 */
@Component
@Data
public class ResponseBean {
    private Integer code;// 响应代码
    private String msgs;// 响应信息
    private Object data;// 响应数据
    private String time;// 响应时间

    public ResponseBean() {
        this.code = 200;
        this.time = DateUtil.formatDateTime(new Date());
    }

    public ResponseBean(Integer code, String msgs, Object data) {
        this.code = code;
        this.msgs = msgs;
        this.data = data;
        this.time = DateUtil.formatDateTime(new Date());
    }

}
